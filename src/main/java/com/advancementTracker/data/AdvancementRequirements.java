package com.advancementTracker.data;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;


import java.util.*;

public class AdvancementRequirements {

    // All biomes for "Adventuring Time"
    public static final Set<Identifier> ALL_BIOMES = Set.of(
            Identifier.of("minecraft:badlands"),
            Identifier.of("minecraft:bamboo_jungle"),
            Identifier.of("minecraft:beach"),
            Identifier.of("minecraft:birch_forest"),
            Identifier.of("minecraft:cherry_grove"),
            Identifier.of("minecraft:cold_ocean"),
            Identifier.of("minecraft:dark_forest"),
            Identifier.of("minecraft:deep_cold_ocean"),
            Identifier.of("minecraft:deep_dark"),
            Identifier.of("minecraft:deep_frozen_ocean"),
            Identifier.of("minecraft:deep_lukewarm_ocean"),
            Identifier.of("minecraft:deep_ocean"),
            Identifier.of("minecraft:desert"),
            Identifier.of("minecraft:dripstone_caves"),
            Identifier.of("minecraft:eroded_badlands"),
            Identifier.of("minecraft:flower_forest"),
            Identifier.of("minecraft:forest"),
            Identifier.of("minecraft:frozen_ocean"),
            Identifier.of("minecraft:frozen_peaks"),
            Identifier.of("minecraft:frozen_river"),
            Identifier.of("minecraft:grove"),
            Identifier.of("minecraft:ice_spikes"),
            Identifier.of("minecraft:jagged_peaks"),
            Identifier.of("minecraft:jungle"),
            Identifier.of("minecraft:lukewarm_ocean"),
            Identifier.of("minecraft:lush_caves"),
            Identifier.of("minecraft:mangrove_swamp"),
            Identifier.of("minecraft:meadow"),
            Identifier.of("minecraft:mushroom_fields"),
            Identifier.of("minecraft:ocean"),
            Identifier.of("minecraft:old_growth_birch_forest"),
            Identifier.of("minecraft:old_growth_pine_taiga"),
            Identifier.of("minecraft:old_growth_spruce_taiga"),
            Identifier.of("minecraft:pale_garden"),
            Identifier.of("minecraft:plains"),
            Identifier.of("minecraft:river"),
            Identifier.of("minecraft:savanna"),
            Identifier.of("minecraft:savanna_plateau"),
            Identifier.of("minecraft:snowy_beach"),
            Identifier.of("minecraft:snowy_plains"),
            Identifier.of("minecraft:snowy_slopes"),
            Identifier.of("minecraft:snowy_taiga"),
            Identifier.of("minecraft:sparse_jungle"),
            Identifier.of("minecraft:stony_peaks"),
            Identifier.of("minecraft:stony_shore"),
            Identifier.of("minecraft:sunflower_plains"),
            Identifier.of("minecraft:swamp"),
            Identifier.of("minecraft:taiga"),
            Identifier.of("minecraft:warm_ocean"),
            Identifier.of("minecraft:windswept_forest"),
            Identifier.of("minecraft:windswept_gravelly_hills"),
            Identifier.of("minecraft:windswept_hills"),
            Identifier.of("minecraft:windswept_savanna"),
            Identifier.of("minecraft:wooded_badlands")
    );

    // All nether biomes for "Hot Tourist Destinations"
    public static final Set<Identifier> ALL_NETHER_BIOMES = Set.of(
            Identifier.of("minecraft:nether_wastes"),
            Identifier.of("minecraft:soul_sand_valley"),
            Identifier.of("minecraft:crimson_forest"),
            Identifier.of("minecraft:warped_forest"),
            Identifier.of("minecraft:basalt_deltas")
    );

    public static final Set<Identifier> ALL_CAT_VARIANTS = Set.of(
            Identifier.of("minecraft:tabby"),
            Identifier.of("minecraft:tuxedo"),
            Identifier.of("minecraft:red"),
            Identifier.of("minecraft:siamese"),
            Identifier.of("minecraft:british_shorthair"),
            Identifier.of("minecraft:calico"),
            Identifier.of("minecraft:persian"),
            Identifier.of("minecraft:ragdoll"),
            Identifier.of("minecraft:white"),
            Identifier.of("minecraft:jellie"),
            Identifier.of("minecraft:black")
    );

    public static final Set<Identifier> ALL_WOLF_VARIANTS = Set.of(
            Identifier.of("minecraft:pale"),
            Identifier.of("minecraft:woods"),
            Identifier.of("minecraft:ashen"),
            Identifier.of("minecraft:black"),
            Identifier.of("minecraft:chestnut"),
            Identifier.of("minecraft:rusty"),
            Identifier.of("minecraft:spotted"),
            Identifier.of("minecraft:striped"),
            Identifier.of("minecraft:snowy")
    );

    public static final Set<Identifier> ALL_FROG_VARIANTS = Set.of(
            Identifier.of("minecraft:temperate"),
            Identifier.of("minecraft:warm"),
            Identifier.of("minecraft:cold")
    );

    // All edible items for "A Balanced Diet"
    public static final Set<Item> ALL_EDIBLE_ITEMS = Set.of(
            Items.APPLE,
            Items.BAKED_POTATO,
            Items.BEEF,
            Items.BEETROOT,
            Items.BEETROOT_SOUP,
            Items.BREAD,
            Items.CAKE,
            Items.CARROT,
            Items.CHICKEN,
            Items.CHORUS_FRUIT,
            Items.COD,
            Items.COOKED_BEEF,
            Items.COOKED_CHICKEN,
            Items.COOKED_COD,
            Items.COOKED_MUTTON,
            Items.COOKED_PORKCHOP,
            Items.COOKED_RABBIT,
            Items.COOKED_SALMON,
            Items.COOKIE,
            Items.DRIED_KELP,
            Items.ENCHANTED_GOLDEN_APPLE,
            Items.GOLDEN_APPLE,
            Items.GOLDEN_CARROT,
            Items.HONEY_BOTTLE,
            Items.MELON_SLICE,
            Items.MILK_BUCKET,
            Items.MUSHROOM_STEW,
            Items.MUTTON,
            Items.POISONOUS_POTATO,
            Items.PORKCHOP,
            Items.POTATO,
            Items.PUMPKIN_PIE,
            Items.RABBIT,
            Items.RABBIT_STEW,
            Items.ROTTEN_FLESH,
            Items.SALMON,
            Items.SPIDER_EYE,
            Items.SUSPICIOUS_STEW,
            Items.SWEET_BERRIES,
            Items.TROPICAL_FISH,
            Items.GLOW_BERRIES
    );

    // All animals for "Two by Two"
    public static final Set<EntityType<?>> ALL_BREEDABLE_ANIMALS = Set.of(
            EntityType.COW,
            EntityType.PIG,
            EntityType.SHEEP,
            EntityType.CHICKEN,
            EntityType.RABBIT,
            EntityType.HORSE,
            EntityType.DONKEY,
            EntityType.MULE,
            EntityType.LLAMA,
            EntityType.CAT,
            EntityType.WOLF,
            EntityType.OCELOT,
            EntityType.FOX,
            EntityType.BEE,
            EntityType.PANDA,
            EntityType.TURTLE,
            EntityType.POLAR_BEAR,
            EntityType.MOOSHROOM,
            EntityType.GOAT,
            EntityType.AXOLOTL,
            EntityType.GLOW_SQUID,
            EntityType.FROG,
            EntityType.CAMEL,
            EntityType.SNIFFER,
            EntityType.ARMADILLO
    );

    // All hostile mobs for "Monsters Hunted"
    public static final Set<EntityType<?>> ALL_HOSTILE_MOBS = Set.of(
            EntityType.ZOMBIE,
            EntityType.SKELETON,
            EntityType.SPIDER,
            EntityType.CREEPER,
            EntityType.ENDERMAN,
            EntityType.WITCH,
            EntityType.SLIME,
            EntityType.GHAST,
            EntityType.ZOMBIFIED_PIGLIN,
            EntityType.BLAZE,
            EntityType.MAGMA_CUBE,
            EntityType.ENDER_DRAGON,
            EntityType.WITHER,
            EntityType.GUARDIAN,
            EntityType.ELDER_GUARDIAN,
            EntityType.SHULKER,
            EntityType.HUSK,
            EntityType.STRAY,
            EntityType.WITHER_SKELETON,
            EntityType.ZOMBIE_VILLAGER,
            EntityType.EVOKER,
            EntityType.VINDICATOR,
            EntityType.VEX,
            EntityType.ILLUSIONER,
            EntityType.CAVE_SPIDER,
            EntityType.SILVERFISH,
            EntityType.ENDERMITE,
            EntityType.PHANTOM,
            EntityType.DROWNED,
            EntityType.PILLAGER,
            EntityType.RAVAGER,
            EntityType.HOGLIN,
            EntityType.ZOGLIN,
            EntityType.PIGLIN_BRUTE,
            EntityType.WARDEN,
            EntityType.BREEZE,
            EntityType.BOGGED
    );

    // Helper methods to get missing items for each advancement
    public static Set<Identifier> getMissingBiomes(PlayerTrackingData data) {
        Set<Identifier> missing = new HashSet<>(ALL_BIOMES);
        missing.removeAll(data.getDiscoveredBiomes());
        return missing;
    }

    public static Set<Identifier> getMissingNetherBiomes(PlayerTrackingData data) {
        Set<Identifier> missing = new HashSet<>(ALL_NETHER_BIOMES);
        missing.removeAll(data.getDiscoveredNetherBiomes());
        return missing;
    }

    public static Set<Identifier> getMissingCats(PlayerTrackingData data) {
        Set<Identifier> missing = new HashSet<>(ALL_CAT_VARIANTS);
        missing.removeAll(data.getTamedCatVariants());
        return missing;
    }

    public static Set<Identifier> getMissingWolves(PlayerTrackingData data) {
        Set<Identifier> missing = new HashSet<>(ALL_WOLF_VARIANTS);
        missing.removeAll(data.getTamedWolfVariants());
        return missing;
    }

    public static Set<Identifier> getMissingFrogs(PlayerTrackingData data) {
        Set<Identifier> missing = new HashSet<>(ALL_FROG_VARIANTS);
        missing.removeAll(data.getLedFrogVariants());
        return missing;
    }

    public static Set<Item> getMissingFoods(PlayerTrackingData data) {
        Set<Item> missing = new HashSet<>(ALL_EDIBLE_ITEMS);
        missing.removeAll(data.getEatenFoods());
        return missing;
    }

    public static Set<EntityType<?>> getMissingBreedableAnimals(PlayerTrackingData data) {
        Set<EntityType<?>> missing = new HashSet<>(ALL_BREEDABLE_ANIMALS);
        missing.removeAll(data.getBredAnimals());
        return missing;
    }

    public static Set<EntityType<?>> getMissingHostileMobs(PlayerTrackingData data) {
        Set<EntityType<?>> missing = new HashSet<>(ALL_HOSTILE_MOBS);
        missing.removeAll(data.getKilledMobs());
        return missing;
    }
}

