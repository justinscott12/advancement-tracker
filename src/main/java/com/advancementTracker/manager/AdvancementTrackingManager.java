package com.advancementTracker.manager;

import com.advancementTracker.AdvancementTrackerMod;
import com.advancementTracker.data.PlayerTrackingData;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

import com.advancementTracker.network.AdvancementTrackerNetworking;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class AdvancementTrackingManager {
    private static final Map<UUID, PlayerTrackingData> playerData = new HashMap<>();
    private static MinecraftServer server;
    private static File dataFile;

    public static void init() {
        // Register event listeners
        registerEventListeners();
        AdvancementTrackerMod.LOGGER.info("Advancement Tracking Manager initialized");
    }

    public static void onServerStart(MinecraftServer minecraftServer) {
        server = minecraftServer;
        dataFile = new File(server.getSavePath(net.minecraft.util.WorldSavePath.ROOT).toFile(), "advancement_tracker_data.dat");
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
            ItemStack stack = player.getStackInHand(hand);
            if (stack.contains(DataComponentTypes.FOOD) && player instanceof ServerPlayerEntity serverPlayer) {
                PlayerTrackingData data = getOrCreatePlayerData(serverPlayer.getUuid());
                data.addEatenFood(stack.getItem());
                AdvancementTrackerMod.LOGGER.debug("Player {} ate {}", serverPlayer.getName().getString(),
                        Registries.ITEM.getId(stack.getItem()));
            }
            return ActionResult.PASS;
        });

        // Block use events (for cake consumption)
        UseBlockCallback.EVENT.register((player, world, hand, blockHitResult) -> {
            if (player instanceof ServerPlayerEntity serverPlayer && !world.isClient) {
                var blockState = world.getBlockState(blockHitResult.getBlockPos());
                if (blockState.getBlock() == net.minecraft.block.Blocks.CAKE) {
                    PlayerTrackingData data = getOrCreatePlayerData(serverPlayer.getUuid());
                    data.addEatenFood(net.minecraft.item.Items.CAKE);
                    AdvancementTrackerMod.LOGGER.debug("Player {} ate cake", serverPlayer.getName().getString());
                }
            }
            return ActionResult.PASS;
        });

        // Entity interaction events (for leashing)
        net.fabricmc.fabric.api.event.player.UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {

            if (player instanceof ServerPlayerEntity serverPlayer && !world.isClient) {
                ItemStack stack = player.getStackInHand(hand);
                if (stack.getItem() == net.minecraft.item.Items.LEAD) {
                    // Track potential frog leashing - this will fire before the leash is actually applied
                    onFrogLeashed(serverPlayer, entity);
                }
            }
            return ActionResult.PASS;
        });

        // Entity death events (for monster hunting)
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
            if (entity instanceof ServerPlayerEntity player && killedEntity instanceof LivingEntity) {
                PlayerTrackingData data = getOrCreatePlayerData(player.getUuid());
                data.addKilledMob(killedEntity.getType());
                AdvancementTrackerMod.LOGGER.debug("Player {} killed {}", player.getName().getString(),
                    Registries.ENTITY_TYPE.getId(killedEntity.getType()));
            }
        });

        // Player join events
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            getOrCreatePlayerData(newPlayer.getUuid());
        });
    }

    public static PlayerTrackingData getOrCreatePlayerData(UUID playerId) {
        return playerData.computeIfAbsent(playerId, PlayerTrackingData::new);
    }

    // Updated method to sync data after changes
    private static void updateAndSyncPlayerData(ServerPlayerEntity player, PlayerTrackingData data) {
        // Send updated data to the specific player
        AdvancementTrackerNetworking.sendPlayerDataToClient(player, data);
        
        // Also send to other players if they're tracking this player's data
        // (You can modify this logic based on your needs)
    }

    // Called from mixins
    public static void onBiomeDiscovered(ServerPlayerEntity player, Identifier biomeId) {
        PlayerTrackingData data = getOrCreatePlayerData(player.getUuid());
        data.addDiscoveredBiome(biomeId);

        // Check if it's a nether biome
        if (isNetherBiome(biomeId)) {
            data.addDiscoveredNetherBiome(biomeId);
        }

        // Auto-sync the updated data
        updateAndSyncPlayerData(player, data);

        AdvancementTrackerMod.LOGGER.debug("Player {} discovered biome {}", 
            player.getName().getString(), biomeId);
    }

    public static void onAnimalTamed(ServerPlayerEntity player, TameableEntity entity) {
        PlayerTrackingData data = getOrCreatePlayerData(player.getUuid());
        EntityType<?> entityType = entity.getType();

        if (entityType == EntityType.CAT) {
            // Extract cat variant from the entity and convert to identifier
            RegistryEntry<CatVariant> catVariantEntry = getCatVariant(entity);
            Identifier catVariantId = catVariantEntry.getKey().map(key -> key.getValue()).orElse(null);
            if (catVariantId != null) {
                data.addTamedCatVariant(catVariantId);
            }
        } else if (entityType == EntityType.WOLF) {
            // Extract wolf variant from the entity
            Identifier wolfVariant = getWolfVariant(entity);
            data.addTamedWolfVariant(wolfVariant);
        }

        // Auto-sync the updated data
        updateAndSyncPlayerData(player, data);

        AdvancementTrackerMod.LOGGER.debug("Player {} tamed {}",
                player.getName().getString(), Registries.ENTITY_TYPE.getId(entityType));
    }


    public static void onAnimalBred(ServerPlayerEntity player, AnimalEntity baby) {
        PlayerTrackingData data = getOrCreatePlayerData(player.getUuid());
        data.addBredAnimal(baby.getType());
        
        // Auto-sync the updated data
        updateAndSyncPlayerData(player, data);
        
        AdvancementTrackerMod.LOGGER.debug("Player {} bred {}", 
            player.getName().getString(), Registries.ENTITY_TYPE.getId(baby.getType()));
    }

    public static void onFrogLeashed(ServerPlayerEntity player, Entity frog) {
        if (frog.getType() == EntityType.FROG) {
            PlayerTrackingData data = getOrCreatePlayerData(player.getUuid());

            // Extract frog variant from the entity and convert to identifier
            RegistryEntry<FrogVariant> frogVariantEntry = getFrogVariant(frog);
            Identifier frogVariantId = frogVariantEntry.getKey().map(key -> key.getValue()).orElse(null);
            if (frogVariantId != null) {
                data.addLedFrogVariant(frogVariantId);
            }

            // Auto-sync the updated data
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
            NbtCompound rootNbt = new NbtCompound();
            NbtCompound playersNbt = new NbtCompound();

            for (Map.Entry<UUID, PlayerTrackingData> entry : playerData.entrySet()) {
                playersNbt.put(entry.getKey().toString(), entry.getValue().writeToNbt());
            }

            rootNbt.put("Players", playersNbt);

            net.minecraft.nbt.NbtIo.writeCompressed(rootNbt, dataFile.toPath());

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
            NbtCompound rootNbt = net.minecraft.nbt.NbtIo.readCompressed(dataFile.toPath(), net.minecraft.nbt.NbtSizeTracker.ofUnlimitedBytes());

            if (rootNbt.contains("Players")) {
                NbtCompound playersNbt = (NbtCompound) rootNbt.get("Players");
                for (String playerIdStr : playersNbt.getKeys()) {
                    try {
                        UUID playerId = UUID.fromString(playerIdStr);
                        NbtCompound playerNbt = (NbtCompound) playersNbt.get(playerIdStr);
                        PlayerTrackingData data = PlayerTrackingData.readFromNbt(playerNbt);
                        playerData.put(playerId, data);
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

    private static RegistryEntry<CatVariant> getCatVariant(TameableEntity cat) {
        CatEntity catEntity = (CatEntity) cat;
        return catEntity.getVariant();
    }

    private static Identifier getWolfVariant(TameableEntity wolf) {
        WolfEntity wolfEntity = (WolfEntity) wolf;
        try {
            // Use reflection to access the private getVariant method
            java.lang.reflect.Method getVariantMethod = WolfEntity.class.getDeclaredMethod("getVariant");
            getVariantMethod.setAccessible(true);
            RegistryEntry<WolfVariant> variantEntry = (RegistryEntry<WolfVariant>) getVariantMethod.invoke(wolfEntity);
            return variantEntry.getKey().map(key -> key.getValue()).orElse(Identifier.of("minecraft", "pale"));
        } catch (Exception e) {
            AdvancementTrackerMod.LOGGER.warn("Failed to get wolf variant via reflection: {}", e.getMessage());
            return Identifier.of("minecraft", "pale");
        }
    }

    private static RegistryEntry<FrogVariant> getFrogVariant(Entity frog) {
        FrogEntity frogEntity = (FrogEntity) frog;
        return frogEntity.getVariant();
    }
}
