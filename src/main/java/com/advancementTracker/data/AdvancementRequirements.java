package com.advancementTracker.data;

import net.minecraft.world.entity.EntityType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.resources.Identifier;


import java.util.*;

public class AdvancementRequirements {

    // 26.2 has no EntityType.COW-style constants — look entity types up by id.
    private static EntityType<?> et(String path) {
        return BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.withDefaultNamespace(path));
    }

    // All biomes for "Adventuring Time"
    public static final Set<Identifier> ALL_BIOMES = Set.of(
            Identifier.parse("minecraft:badlands"),
            Identifier.parse("minecraft:bamboo_jungle"),
            Identifier.parse("minecraft:beach"),
            Identifier.parse("minecraft:birch_forest"),
            Identifier.parse("minecraft:cherry_grove"),
            Identifier.parse("minecraft:cold_ocean"),
            Identifier.parse("minecraft:dark_forest"),
            Identifier.parse("minecraft:deep_cold_ocean"),
            Identifier.parse("minecraft:deep_dark"),
            Identifier.parse("minecraft:deep_frozen_ocean"),
            Identifier.parse("minecraft:deep_lukewarm_ocean"),
            Identifier.parse("minecraft:deep_ocean"),
            Identifier.parse("minecraft:desert"),
            Identifier.parse("minecraft:dripstone_caves"),
            Identifier.parse("minecraft:eroded_badlands"),
            Identifier.parse("minecraft:flower_forest"),
            Identifier.parse("minecraft:forest"),
            Identifier.parse("minecraft:frozen_ocean"),
            Identifier.parse("minecraft:frozen_peaks"),
            Identifier.parse("minecraft:frozen_river"),
            Identifier.parse("minecraft:grove"),
            Identifier.parse("minecraft:ice_spikes"),
            Identifier.parse("minecraft:jagged_peaks"),
            Identifier.parse("minecraft:jungle"),
            Identifier.parse("minecraft:lukewarm_ocean"),
            Identifier.parse("minecraft:lush_caves"),
            Identifier.parse("minecraft:mangrove_swamp"),
            Identifier.parse("minecraft:meadow"),
            Identifier.parse("minecraft:mushroom_fields"),
            Identifier.parse("minecraft:ocean"),
            Identifier.parse("minecraft:old_growth_birch_forest"),
            Identifier.parse("minecraft:old_growth_pine_taiga"),
            Identifier.parse("minecraft:old_growth_spruce_taiga"),
            Identifier.parse("minecraft:pale_garden"),
            Identifier.parse("minecraft:plains"),
            Identifier.parse("minecraft:river"),
            Identifier.parse("minecraft:savanna"),
            Identifier.parse("minecraft:savanna_plateau"),
            Identifier.parse("minecraft:snowy_beach"),
            Identifier.parse("minecraft:snowy_plains"),
            Identifier.parse("minecraft:snowy_slopes"),
            Identifier.parse("minecraft:snowy_taiga"),
            Identifier.parse("minecraft:sparse_jungle"),
            Identifier.parse("minecraft:stony_peaks"),
            Identifier.parse("minecraft:stony_shore"),
            Identifier.parse("minecraft:sunflower_plains"),
            Identifier.parse("minecraft:swamp"),
            Identifier.parse("minecraft:taiga"),
            Identifier.parse("minecraft:warm_ocean"),
            Identifier.parse("minecraft:windswept_forest"),
            Identifier.parse("minecraft:windswept_gravelly_hills"),
            Identifier.parse("minecraft:windswept_hills"),
            Identifier.parse("minecraft:windswept_savanna"),
            Identifier.parse("minecraft:wooded_badlands")
    );

    // All nether biomes for "Hot Tourist Destinations"
    public static final Set<Identifier> ALL_NETHER_BIOMES = Set.of(
            Identifier.parse("minecraft:basalt_deltas"),
            Identifier.parse("minecraft:crimson_forest"),
            Identifier.parse("minecraft:nether_wastes"),
            Identifier.parse("minecraft:soul_sand_valley"),
            Identifier.parse("minecraft:warped_forest")
    );

    public static final Set<Identifier> ALL_CAT_VARIANTS = Set.of(
            Identifier.parse("minecraft:black"),
            Identifier.parse("minecraft:british_shorthair"),
            Identifier.parse("minecraft:calico"),
            Identifier.parse("minecraft:jellie"),
            Identifier.parse("minecraft:persian"),
            Identifier.parse("minecraft:ragdoll"),
            Identifier.parse("minecraft:red"),
            Identifier.parse("minecraft:siamese"),
            Identifier.parse("minecraft:tabby"),
            Identifier.parse("minecraft:tuxedo"),
            Identifier.parse("minecraft:white")
    );

    public static final Set<Identifier> ALL_WOLF_VARIANTS = Set.of(
            Identifier.parse("minecraft:ashen"),
            Identifier.parse("minecraft:black"),
            Identifier.parse("minecraft:chestnut"),
            Identifier.parse("minecraft:pale"),
            Identifier.parse("minecraft:rusty"),
            Identifier.parse("minecraft:snowy"),
            Identifier.parse("minecraft:spotted"),
            Identifier.parse("minecraft:striped"),
            Identifier.parse("minecraft:woods")
            );

    public static final Set<Identifier> ALL_FROG_VARIANTS = Set.of(
            Identifier.parse("minecraft:cold"),
            Identifier.parse("minecraft:temperate"),
            Identifier.parse("minecraft:warm")
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
            et("cow"),
            et("pig"),
            et("sheep"),
            et("chicken"),
            et("rabbit"),
            et("horse"),
            et("donkey"),
            et("mule"),
            et("llama"),
            et("cat"),
            et("wolf"),
            et("ocelot"),
            et("fox"),
            et("bee"),
            et("panda"),
            et("turtle"),
            et("polar_bear"),
            et("mooshroom"),
            et("goat"),
            et("axolotl"),
            et("glow_squid"),
            et("frog"),
            et("camel"),
            et("sniffer"),
            et("armadillo")
    );

    // All hostile mobs for "Monsters Hunted"
    public static final Set<EntityType<?>> ALL_HOSTILE_MOBS = Set.of(
            et("blaze"),
            et("bogged"),
            et("breeze"),
            et("cave_spider"),
            et("creeper"),
            et("drowned"),
            et("elder_guardian"),
            et("ender_dragon"),
            et("enderman"),
            et("endermite"),
            et("evoker"),
            et("ghast"),
            et("guardian"),
            et("hoglin"),
            et("husk"),
            et("illusioner"),
            et("magma_cube"),
            et("phantom"),
            et("piglin_brute"),
            et("pillager"),
            et("ravager"),
            et("shulker"),
            et("silverfish"),
            et("skeleton"),
            et("slime"),
            et("spider"),
            et("stray"),
            et("vex"),
            et("vindicator"),
            et("warden"),
            et("witch"),
            et("wither"),
            et("wither_skeleton"),
            et("zoglin"),
            et("zombie"),
            et("zombie_villager"),
            et("zombified_piglin")
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

