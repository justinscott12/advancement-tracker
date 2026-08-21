package com.advancementTracker.data;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.util.*;

public class PlayerTrackingData {
    private final UUID playerId;
    private final Set<Identifier> discoveredBiomes = new HashSet<>();
    private final Set<Identifier> discoveredNetherBiomes = new HashSet<>();
    private final Set<EntityType<?>> tamedCats = new HashSet<>();
    private final Set<EntityType<?>> tamedWolves = new HashSet<>();
    private final Set<EntityType<?>> ledFrogs = new HashSet<>();
    private final Set<Item> eatenFoods = new HashSet<>();
    private final Set<EntityType<?>> bredAnimals = new HashSet<>();
    private final Set<EntityType<?>> killedMobs = new HashSet<>();

    private final Set<Identifier> tamedCatVariants = new HashSet<>();
    private final Set<Identifier> tamedWolfVariants = new HashSet<>();
    private final Set<Identifier> ledFrogVariants = new HashSet<>();

    public PlayerTrackingData(UUID playerId) {
        this.playerId = playerId;
    }

    public void addDiscoveredBiome(Identifier biomeId) {
        discoveredBiomes.add(biomeId);
    }

    public void addDiscoveredNetherBiome(Identifier biomeId) {
        discoveredNetherBiomes.add(biomeId);
    }

    public Set<Identifier> getDiscoveredBiomes() {
        return new HashSet<>(discoveredBiomes);
    }

    public Set<Identifier> getDiscoveredNetherBiomes() {
        return new HashSet<>(discoveredNetherBiomes);
    }

    public void addEatenFood(Item food) {
        eatenFoods.add(food);
    }

    public Set<Item> getEatenFoods() {
        return new HashSet<>(eatenFoods);
    }

    public void addBredAnimal(EntityType<?> animalType) {
        bredAnimals.add(animalType);
    }

    public Set<EntityType<?>> getBredAnimals() {
        return new HashSet<>(bredAnimals);
    }

    public void addKilledMob(EntityType<?> mobType) {
        killedMobs.add(mobType);
    }

    public Set<EntityType<?>> getKilledMobs() {
        return new HashSet<>(killedMobs);
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void addTamedCatVariant(Identifier variantId) {
        tamedCatVariants.add(variantId);
    }

    public void addTamedWolfVariant(Identifier variantId) {
        tamedWolfVariants.add(variantId);
    }

    public Set<Identifier> getTamedCatVariants() {
        return new HashSet<>(tamedCatVariants);
    }

    public Set<Identifier> getTamedWolfVariants() {
        return new HashSet<>(tamedWolfVariants);
    }

    public void addLedFrogVariant(Identifier variantId) {
        ledFrogVariants.add(variantId);
    }

    public Set<Identifier> getLedFrogVariants() {
        return new HashSet<>(ledFrogVariants);
    }

    public CompoundTag writeToNbt() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("PlayerId", playerId.toString());

        ListTag biomesList = new ListTag();
        discoveredBiomes.forEach(id -> biomesList.add(StringTag.valueOf(id.toString())));
        nbt.put("DiscoveredBiomes", biomesList);

        ListTag netherBiomesList = new ListTag();
        discoveredNetherBiomes.forEach(id -> netherBiomesList.add(StringTag.valueOf(id.toString())));
        nbt.put("DiscoveredNetherBiomes", netherBiomesList);

        ListTag tamedCatVariantsList = new ListTag();
        tamedCatVariants.forEach(id -> tamedCatVariantsList.add(StringTag.valueOf(id.toString())));
        nbt.put("TamedCatVariants", tamedCatVariantsList);

        ListTag tamedWolfVariantsList = new ListTag();
        tamedWolfVariants.forEach(id -> tamedWolfVariantsList.add(StringTag.valueOf(id.toString())));
        nbt.put("TamedWolfVariants", tamedWolfVariantsList);

        ListTag ledFrogVariantsList = new ListTag();
        ledFrogVariants.forEach(id -> ledFrogVariantsList.add(StringTag.valueOf(id.toString())));
        nbt.put("LedFrogVariants", ledFrogVariantsList);

        ListTag eatenFoodsList = new ListTag();
        eatenFoods.forEach(item -> eatenFoodsList.add(StringTag.valueOf(BuiltInRegistries.ITEM.getKey(item).toString())));
        nbt.put("EatenFoods", eatenFoodsList);

        ListTag bredAnimalsList = new ListTag();
        bredAnimals.forEach(type -> bredAnimalsList.add(StringTag.valueOf(BuiltInRegistries.ENTITY_TYPE.getKey(type).toString())));
        nbt.put("BredAnimals", bredAnimalsList);

        ListTag killedMobsList = new ListTag();
        killedMobs.forEach(type -> killedMobsList.add(StringTag.valueOf(BuiltInRegistries.ENTITY_TYPE.getKey(type).toString())));
        nbt.put("KilledMobs", killedMobsList);

        return nbt;
    }

    public static PlayerTrackingData readFromNbt(CompoundTag nbt) {
        String playerIdString = nbt.getString("PlayerId").orElse("");
        UUID playerId;

        if (!playerIdString.isEmpty()) {
            try {
                playerId = UUID.fromString(playerIdString);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid UUID format: " + playerIdString, e);
            }
        } else {
            throw new IllegalArgumentException("Missing or empty PlayerId in NBT data");
        }

        PlayerTrackingData data = new PlayerTrackingData(playerId);

        loadIdentifierSetFromNbt(nbt, "DiscoveredBiomes", data.discoveredBiomes);
        loadIdentifierSetFromNbt(nbt, "DiscoveredNetherBiomes", data.discoveredNetherBiomes);
        loadIdentifierSetFromNbt(nbt, "TamedCatVariants", data.tamedCatVariants);
        loadIdentifierSetFromNbt(nbt, "TamedWolfVariants", data.tamedWolfVariants);
        loadIdentifierSetFromNbt(nbt, "LedFrogVariants", data.ledFrogVariants);

        loadRegistrySetFromNbt(nbt, "EatenFoods", BuiltInRegistries.ITEM, data.eatenFoods);
        loadRegistrySetFromNbt(nbt, "BredAnimals", BuiltInRegistries.ENTITY_TYPE, data.bredAnimals);
        loadRegistrySetFromNbt(nbt, "KilledMobs", BuiltInRegistries.ENTITY_TYPE, data.killedMobs);

        return data;
    }

    private static void loadIdentifierSetFromNbt(CompoundTag nbt, String key, Set<Identifier> targetSet) {
        nbt.getList(key).ifPresent(list -> {
            for (int i = 0; i < list.size(); i++) {
                String idStr = list.getStringOr(i, "");
                if (!idStr.isEmpty()) {
                    Identifier identifier = Identifier.tryParse(idStr);
                    if (identifier != null) {
                        targetSet.add(identifier);
                    }
                }
            }
        });
    }

    private static <T> void loadRegistrySetFromNbt(CompoundTag nbt, String key, Registry<T> registry, Set<T> targetSet) {
        nbt.getList(key).ifPresent(list -> {
            for (int i = 0; i < list.size(); i++) {
                String idStr = list.getStringOr(i, "");
                if (!idStr.isEmpty()) {
                    Identifier identifier = Identifier.tryParse(idStr);
                    if (identifier != null) {
                        T value = registry.getValue(identifier);
                        if (value != null) {
                            targetSet.add(value);
                        }
                    }
                }
            }
        });
    }
}
