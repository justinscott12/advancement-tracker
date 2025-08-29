package com.advancementTracker.data;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

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

    public NbtCompound writeToNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("PlayerId", playerId.toString());

        NbtList biomesList = new NbtList();
        discoveredBiomes.forEach(id -> biomesList.add(NbtString.of(id.toString())));
        nbt.put("DiscoveredBiomes", biomesList);

        NbtList netherBiomesList = new NbtList();
        discoveredNetherBiomes.forEach(id -> netherBiomesList.add(NbtString.of(id.toString())));
        nbt.put("DiscoveredNetherBiomes", netherBiomesList);

        NbtList tamedCatVariantsList = new NbtList();
        tamedCatVariants.forEach(id -> tamedCatVariantsList.add(NbtString.of(id.toString())));
        nbt.put("TamedCatVariants", tamedCatVariantsList);

        NbtList tamedWolfVariantsList = new NbtList();
        tamedWolfVariants.forEach(id -> tamedWolfVariantsList.add(NbtString.of(id.toString())));
        nbt.put("TamedWolfVariants", tamedWolfVariantsList);

        NbtList ledFrogVariantsList = new NbtList();
        ledFrogVariants.forEach(id -> ledFrogVariantsList.add(NbtString.of(id.toString())));
        nbt.put("LedFrogVariants", ledFrogVariantsList);

        NbtList eatenFoodsList = new NbtList();
        eatenFoods.forEach(item -> eatenFoodsList.add(NbtString.of(Registries.ITEM.getId(item).toString())));
        nbt.put("EatenFoods", eatenFoodsList);

        NbtList bredAnimalsList = new NbtList();
        bredAnimals.forEach(type -> bredAnimalsList.add(NbtString.of(Registries.ENTITY_TYPE.getId(type).toString())));
        nbt.put("BredAnimals", bredAnimalsList);

        NbtList killedMobsList = new NbtList();
        killedMobs.forEach(type -> killedMobsList.add(NbtString.of(Registries.ENTITY_TYPE.getId(type).toString())));
        nbt.put("KilledMobs", killedMobsList);

        return nbt;
    }


    public static PlayerTrackingData readFromNbt(NbtCompound nbt) {
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

        loadRegistrySetFromNbt(nbt, "EatenFoods", Registries.ITEM, data.eatenFoods);
        loadRegistrySetFromNbt(nbt, "BredAnimals", Registries.ENTITY_TYPE, data.bredAnimals);
        loadRegistrySetFromNbt(nbt, "KilledMobs", Registries.ENTITY_TYPE, data.killedMobs);

        return data;
    }


    private static void loadIdentifierSetFromNbt(NbtCompound nbt, String key, Set<Identifier> targetSet) {
        if (nbt.contains(key)) {
            Optional<NbtList> optionalList = nbt.getList(key);
            if (optionalList.isPresent()) {
                NbtList list = optionalList.get();
                for (int i = 0; i < list.size(); i++) {
                    String idStr = list.getString(i).orElse("");
                    if (!idStr.isEmpty()) {
                        Identifier identifier = Identifier.tryParse(idStr);
                        if (identifier != null) {
                            targetSet.add(identifier);
                        }
                    }
                }
            }
        }
    }

    private static <T> void loadRegistrySetFromNbt(NbtCompound nbt, String key, Registry<T> registry, Set<T> targetSet) {
        if (nbt.contains(key)) {
            Optional<NbtList> optionalList = nbt.getList(key);
            if (optionalList.isPresent()) {
                NbtList list = optionalList.get();
                for (int i = 0; i < list.size(); i++) {
                    String idStr = list.getString(i).orElse("");
                    if (!idStr.isEmpty()) {
                        Identifier identifier = Identifier.tryParse(idStr);
                        if (identifier != null) {
                            T value = registry.get(identifier);
                            if (value != null) {
                                targetSet.add(value);
                            }
                        }
                    }
                }
            }
        }
    }
}
