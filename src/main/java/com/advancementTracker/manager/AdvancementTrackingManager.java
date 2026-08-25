package com.advancementTracker.manager;

import com.advancementTracker.AdvancementTrackerMod;
import com.advancementTracker.data.PlayerTrackingData;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.feline.CatVariant;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.FrogVariant;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.animal.wolf.WolfVariant;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.LevelResource;

import com.advancementTracker.network.AdvancementTrackerNetworking;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class AdvancementTrackingManager {
    private static final Map<UUID, PlayerTrackingData> playerData = new HashMap<>();
    private static MinecraftServer server;
    private static File dataFile;

    // 26.2 has no EntityType.CAT-style constants — resolve by id.
    private static EntityType<?> et(String path) {
        return BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.withDefaultNamespace(path));
    }

    public static void init() {
        registerEventListeners();
        AdvancementTrackerMod.LOGGER.info("Advancement Tracking Manager initialized");
    }

    public static void onServerStart(MinecraftServer minecraftServer) {
        server = minecraftServer;
        dataFile = new File(server.getWorldPath(LevelResource.ROOT).toFile(), "advancement_tracker_data.dat");
        loadData();
    }

    public static void onServerStop(MinecraftServer minecraftServer) {
        saveData();
        // Clear in-memory state so it doesn't leak into the next world loaded
        // in the same JVM session (singleplayer keeps the process alive).
        playerData.clear();
        server = null;
        dataFile = null;
    }

    private static void registerEventListeners() {
        // Item use events (for food consumption)
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.has(DataComponents.FOOD) && player instanceof ServerPlayer serverPlayer) {
                PlayerTrackingData data = getOrCreatePlayerData(serverPlayer.getUUID());
                data.addEatenFood(stack.getItem());
                AdvancementTrackerMod.LOGGER.debug("Player {} ate {}", serverPlayer.getName().getString(),
                        BuiltInRegistries.ITEM.getKey(stack.getItem()));
            }
            return InteractionResult.PASS;
        });

        // Block use events (for cake consumption)
        UseBlockCallback.EVENT.register((player, world, hand, blockHitResult) -> {
            if (player instanceof ServerPlayer serverPlayer && !world.isClientSide()) {
                var blockState = world.getBlockState(blockHitResult.getBlockPos());
                if (blockState.getBlock() == Blocks.CAKE) {
                    PlayerTrackingData data = getOrCreatePlayerData(serverPlayer.getUUID());
                    data.addEatenFood(net.minecraft.world.item.Items.CAKE);
                    AdvancementTrackerMod.LOGGER.debug("Player {} ate cake", serverPlayer.getName().getString());
                }
            }
            return InteractionResult.PASS;
        });

        // Entity interaction events (for leashing)
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player instanceof ServerPlayer serverPlayer && !world.isClientSide()) {
                ItemStack stack = player.getItemInHand(hand);
                if (stack.getItem() == net.minecraft.world.item.Items.LEAD) {
                    onFrogLeashed(serverPlayer, entity);
                }
            }
            return InteractionResult.PASS;
        });

        // Entity death events (for monster hunting)
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity, damageSource) -> {
            if (entity instanceof ServerPlayer player && killedEntity instanceof LivingEntity) {
                PlayerTrackingData data = getOrCreatePlayerData(player.getUUID());
                data.addKilledMob(killedEntity.getType());
                AdvancementTrackerMod.LOGGER.debug("Player {} killed {}", player.getName().getString(),
                    BuiltInRegistries.ENTITY_TYPE.getKey(killedEntity.getType()));
            }
        });

        // Player respawn events
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            getOrCreatePlayerData(newPlayer.getUUID());
        });
    }

    public static PlayerTrackingData getOrCreatePlayerData(UUID playerId) {
        return playerData.computeIfAbsent(playerId, PlayerTrackingData::new);
    }

    private static void updateAndSyncPlayerData(ServerPlayer player, PlayerTrackingData data) {
        AdvancementTrackerNetworking.sendPlayerDataToClient(player, data);
    }

    // Called from mixins
    public static void onBiomeDiscovered(ServerPlayer player, Identifier biomeId) {
        PlayerTrackingData data = getOrCreatePlayerData(player.getUUID());
        data.addDiscoveredBiome(biomeId);

        if (isNetherBiome(biomeId)) {
            data.addDiscoveredNetherBiome(biomeId);
        }

        updateAndSyncPlayerData(player, data);

        AdvancementTrackerMod.LOGGER.debug("Player {} discovered biome {}",
            player.getName().getString(), biomeId);
    }

    public static void onAnimalTamed(ServerPlayer player, TamableAnimal entity) {
        PlayerTrackingData data = getOrCreatePlayerData(player.getUUID());
        EntityType<?> entityType = entity.getType();

        if (entityType == et("cat")) {
            Holder<CatVariant> catVariant = getCatVariant(entity);
            Identifier catVariantId = catVariant.unwrapKey().map(ResourceKey::identifier).orElse(null);
            if (catVariantId != null) {
                data.addTamedCatVariant(catVariantId);
            }
        } else if (entityType == et("wolf")) {
            Identifier wolfVariant = getWolfVariant(entity);
            data.addTamedWolfVariant(wolfVariant);
        }

        updateAndSyncPlayerData(player, data);

        AdvancementTrackerMod.LOGGER.debug("Player {} tamed {}",
                player.getName().getString(), BuiltInRegistries.ENTITY_TYPE.getKey(entityType));
    }

    public static void onAnimalBred(ServerPlayer player, Animal baby) {
        PlayerTrackingData data = getOrCreatePlayerData(player.getUUID());
        data.addBredAnimal(baby.getType());

        updateAndSyncPlayerData(player, data);

        AdvancementTrackerMod.LOGGER.debug("Player {} bred {}",
            player.getName().getString(), BuiltInRegistries.ENTITY_TYPE.getKey(baby.getType()));
    }

    public static void onFrogLeashed(ServerPlayer player, Entity frog) {
        if (frog.getType() == et("frog")) {
            PlayerTrackingData data = getOrCreatePlayerData(player.getUUID());

            Holder<FrogVariant> frogVariant = getFrogVariant(frog);
            Identifier frogVariantId = frogVariant.unwrapKey().map(ResourceKey::identifier).orElse(null);
            if (frogVariantId != null) {
                data.addLedFrogVariant(frogVariantId);
            }

            updateAndSyncPlayerData(player, data);

            AdvancementTrackerMod.LOGGER.debug("Player {} leashed a frog", player.getName().getString());
        }
    }

    private static boolean isNetherBiome(Identifier biomeId) {
        return biomeId.getNamespace().equals("minecraft") && (
            biomeId.getPath().equals("nether_wastes") ||
            biomeId.getPath().equals("soul_sand_valley") ||
            biomeId.getPath().equals("crimson_forest") ||
            biomeId.getPath().equals("warped_forest") ||
            biomeId.getPath().equals("basalt_deltas")
        );
    }

    public static PlayerTrackingData getPlayerData(UUID playerId) {
        return playerData.get(playerId);
    }

    private static void saveData() {
        if (dataFile == null) return;

        try {
            CompoundTag rootNbt = new CompoundTag();
            CompoundTag playersNbt = new CompoundTag();

            for (Map.Entry<UUID, PlayerTrackingData> entry : playerData.entrySet()) {
                playersNbt.put(entry.getKey().toString(), entry.getValue().writeToNbt());
            }

            rootNbt.put("Players", playersNbt);

            NbtIo.writeCompressed(rootNbt, dataFile.toPath());

            AdvancementTrackerMod.LOGGER.info("Saved advancement tracking data for {} players", playerData.size());
        } catch (IOException e) {
            AdvancementTrackerMod.LOGGER.error("Failed to save advancement tracking data", e);
        }
    }

    private static void loadData() {
        // Start from a clean slate every load so a new world (which may have no
        // data file) never inherits the previously loaded world's progress.
        playerData.clear();

        if (dataFile == null || !dataFile.exists()) return;

        try {
            CompoundTag rootNbt = NbtIo.readCompressed(dataFile.toPath(), NbtAccounter.unlimitedHeap());

            CompoundTag playersNbt = rootNbt.getCompound("Players").orElse(null);
            if (playersNbt != null) {
                for (String playerIdStr : playersNbt.keySet()) {
                    try {
                        UUID playerId = UUID.fromString(playerIdStr);
                        CompoundTag playerNbt = playersNbt.getCompound(playerIdStr).orElse(null);
                        if (playerNbt != null) {
                            PlayerTrackingData data = PlayerTrackingData.readFromNbt(playerNbt);
                            playerData.put(playerId, data);
                        }
                    } catch (Exception e) {
                        AdvancementTrackerMod.LOGGER.warn("Failed to load data for player {}", playerIdStr, e);
                    }
                }
            }
            AdvancementTrackerMod.LOGGER.info("Loaded advancement tracking data for {} players", playerData.size());
        } catch (IOException e) {
            AdvancementTrackerMod.LOGGER.error("Failed to load advancement tracking data", e);
        }
    }

    private static Holder<CatVariant> getCatVariant(TamableAnimal cat) {
        Cat catEntity = (Cat) cat;
        return catEntity.getVariant();
    }

    @SuppressWarnings("unchecked")
    private static Identifier getWolfVariant(TamableAnimal wolf) {
        Wolf wolfEntity = (Wolf) wolf;
        try {
            // getVariant() is private on Wolf — reach it reflectively.
            java.lang.reflect.Method getVariantMethod = Wolf.class.getDeclaredMethod("getVariant");
            getVariantMethod.setAccessible(true);
            Holder<WolfVariant> variant = (Holder<WolfVariant>) getVariantMethod.invoke(wolfEntity);
            return variant.unwrapKey().map(ResourceKey::identifier).orElse(Identifier.fromNamespaceAndPath("minecraft", "pale"));
        } catch (Exception e) {
            AdvancementTrackerMod.LOGGER.warn("Failed to get wolf variant via reflection: {}", e.getMessage());
            return Identifier.fromNamespaceAndPath("minecraft", "pale");
        }
    }

    private static Holder<FrogVariant> getFrogVariant(Entity frog) {
        Frog frogEntity = (Frog) frog;
        return frogEntity.getVariant();
    }
}
