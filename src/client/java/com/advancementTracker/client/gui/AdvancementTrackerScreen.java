package com.advancementTracker.client.gui;

import com.advancementTracker.data.AdvancementRequirements;
import com.advancementTracker.data.PlayerTrackingData;
import com.advancementTracker.manager.ClientDataManager;
import com.advancementTracker.network.AdvancementTrackerClientNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AdvancementTrackerScreen extends Screen {
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;

    private int currentCategory = 0;
    private final String[] categories = {
            "Adventuring Time", "Hot Tourist Destinations", "A Complete Catalogue",
            "The Whole Pack", "When the Squad Hops into Town", "A Balanced Diet",
            "Two by Two", "Monsters Hunted"
    };

    private int scrollOffset = 0;

    public AdvancementTrackerScreen() {
        super(Component.literal("Advancement Tracker"));
    }

    @Override
    protected void init() {
        super.init();

        // Category selection buttons
        int startX = 10;
        int startY = 30;

        for (int i = 0; i < categories.length; i++) {
            final int categoryIndex = i;
            Button button = Button.builder(
                            Component.literal(categories[i]),
                            btn -> {
                                currentCategory = categoryIndex;
                                scrollOffset = 0;
                            }
                    )
                    .bounds(startX, startY + i * 25, BUTTON_WIDTH, BUTTON_HEIGHT)
                    .build();

            this.addRenderableWidget(button);
        }

        // Close button
        this.addRenderableWidget(Button.builder(
                        Component.literal("Close"),
                        btn -> this.onClose()
                )
                .bounds(this.width / 2 - 50, this.height - 30, 100, 20)
                .build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        super.extractRenderState(extractor, mouseX, mouseY, delta);
        renderCategoryContent(extractor);
    }

    private void renderCategoryContent(GuiGraphicsExtractor extractor) {
        int buttonAreaRight = 10 + BUTTON_WIDTH;
        int minContentStartX = buttonAreaRight + 10;
        int contentWidthEstimate = 600;
        int desiredCenterX = (this.width + minContentStartX) / 2;
        int startX = Math.max(minContentStartX, desiredCenterX - contentWidthEstimate / 2);
        int contentBlockStartY = 32;

        // Get player data
        Minecraft client = Minecraft.getInstance();
        if (client == null || client.player == null) {
            extractor.text(this.font, Component.literal("Player not available."), startX, contentBlockStartY, 0xFFFFFFFF, true);
            return;
        }

        PlayerTrackingData data = ClientDataManager.getPlayerData(client.player.getUUID());

        // If no data available, request it from server
        if (data == null && client.getConnection() != null) {
            AdvancementTrackerClientNetworking.requestPlayerData(client.player.getUUID());
            data = new PlayerTrackingData(client.player.getUUID());
        } else if (data == null) {
            data = new PlayerTrackingData(client.player.getUUID());
        }

        // Display category title
        extractor.text(this.font,
                Component.literal("Category: " + categories[currentCategory]).withStyle(ChatFormatting.YELLOW),
                startX, contentBlockStartY, 0xFFFFFFFF, true);

        // Display completion progress
        int completed = getCompletedCount(data);
        int total = getTotalCount();
        extractor.text(this.font,
                Component.literal("Progress: " + completed + "/" + total).withStyle(ChatFormatting.AQUA),
                startX, contentBlockStartY + 20, 0xFFFFFFFF, true);

        // Display specific items in category
        List<String> categoryContent = getCategoryContent(data);
        int itemListStartY = contentBlockStartY + 45;

        if (categoryContent.isEmpty()) {
            extractor.text(this.font, Component.literal("No items in this category yet."), startX, itemListStartY, 0xFF888888, true);
        } else {
            final int ITEMS_PER_COLUMN = 20;
            final int BASE_COLUMN_WIDTH = 140;
            final int LINE_HEIGHT = 10;

            int availableWidth = this.width - startX - 20;
            int maxColumns = 3;
            int maxWidth = Math.min(BASE_COLUMN_WIDTH, availableWidth / maxColumns);

            int availableHeight = this.height - itemListStartY - 40;
            int maxRowsPerColumn = Math.min(ITEMS_PER_COLUMN, availableHeight / LINE_HEIGHT);

            int maxDisplayableItems = maxRowsPerColumn * maxColumns;
            int itemsToDisplay = Math.min(categoryContent.size() - scrollOffset, maxDisplayableItems);

            for (int i = 0; i < itemsToDisplay; i++) {
                int contentIndex = scrollOffset + i;
                if (contentIndex >= categoryContent.size()) break;

                int column = i / maxRowsPerColumn;
                int row = i % maxRowsPerColumn;
                int itemX = startX + (column * maxWidth);
                int itemY = itemListStartY + (row * LINE_HEIGHT);

                String currentItemString = categoryContent.get(contentIndex);

                int textColor;
                if (currentItemString.startsWith("✓")) { // Checkmark (✓)
                    textColor = 0xFF00FF00; // Green
                } else if (currentItemString.startsWith("✗")) { // Ballot X (✗)
                    textColor = 0xFFFF0000; // Red
                } else {
                    textColor = 0xFFFFFFFF; // White
                }

                // Truncate text if too long
                String displayString = currentItemString;
                if (this.font.width(displayString) > maxWidth) {
                    while (this.font.width(displayString + "...") > maxWidth && displayString.length() > 1) {
                        displayString = displayString.substring(0, displayString.length() - 1);
                    }
                    displayString = displayString + "...";
                }

                extractor.text(this.font, Component.literal(displayString), itemX, itemY, textColor, true);
            }
        }
    }

    private int getCompletedCount(PlayerTrackingData data) {
        return switch (currentCategory) {
            case 0 -> data.getDiscoveredBiomes().size();
            case 1 -> data.getDiscoveredNetherBiomes().size();
            case 2 -> data.getTamedCatVariants().size();
            case 3 -> data.getTamedWolfVariants().size();
            case 4 -> data.getLedFrogVariants().size();
            case 5 -> data.getEatenFoods().size();
            case 6 -> data.getBredAnimals().size();
            case 7 -> data.getKilledMobs().size();
            default -> 0;
        };
    }

    private List<String> getCategoryContent(PlayerTrackingData data) {
        List<String> content = new ArrayList<>();

        switch (currentCategory) {
            case 0: {
                Set<Identifier> missing = AdvancementRequirements.getMissingBiomes(data);
                content = AdvancementRequirements.ALL_BIOMES.stream()
                        .sorted(Comparator.comparing(Identifier::getPath))
                        .map(biome -> (missing.contains(biome) ? "✗" : "✓") + " " + formatName(biome.getPath()))
                        .collect(Collectors.toList());
                break;
            }
            case 1: {
                Set<Identifier> missing = AdvancementRequirements.getMissingNetherBiomes(data);
                content = AdvancementRequirements.ALL_NETHER_BIOMES.stream()
                        .sorted(Comparator.comparing(Identifier::getPath))
                        .map(biome -> (missing.contains(biome) ? "✗" : "✓") + " " + formatName(biome.getPath()))
                        .collect(Collectors.toList());
                break;
            }
            case 2: {
                Set<Identifier> missing = AdvancementRequirements.getMissingCats(data);
                content = AdvancementRequirements.ALL_CAT_VARIANTS.stream()
                        .sorted(Comparator.comparing(Identifier::getPath))
                        .map(v -> (missing.contains(v) ? "✗" : "✓") + " " + formatName(v.getPath()))
                        .collect(Collectors.toList());
                break;
            }
            case 3: {
                Set<Identifier> missing = AdvancementRequirements.getMissingWolves(data);
                content = AdvancementRequirements.ALL_WOLF_VARIANTS.stream()
                        .sorted(Comparator.comparing(Identifier::getPath))
                        .map(v -> (missing.contains(v) ? "✗" : "✓") + " " + formatName(v.getPath()))
                        .collect(Collectors.toList());
                break;
            }
            case 4: {
                Set<Identifier> missing = AdvancementRequirements.getMissingFrogs(data);
                content = AdvancementRequirements.ALL_FROG_VARIANTS.stream()
                        .sorted(Comparator.comparing(Identifier::getPath))
                        .map(v -> (missing.contains(v) ? "✗" : "✓") + " " + formatName(v.getPath()))
                        .collect(Collectors.toList());
                break;
            }
            case 5: {
                Set<Item> missing = AdvancementRequirements.getMissingFoods(data);
                content = AdvancementRequirements.ALL_EDIBLE_ITEMS.stream()
                        .sorted(Comparator.comparing(food -> BuiltInRegistries.ITEM.getKey(food).getPath()))
                        .map(food -> (missing.contains(food) ? "✗" : "✓") + " " + formatName(BuiltInRegistries.ITEM.getKey(food).getPath()))
                        .collect(Collectors.toList());
                break;
            }
            case 6: {
                Set<EntityType<?>> missing = AdvancementRequirements.getMissingBreedableAnimals(data);
                content = AdvancementRequirements.ALL_BREEDABLE_ANIMALS.stream()
                        .sorted(Comparator.comparing(a -> BuiltInRegistries.ENTITY_TYPE.getKey(a).getPath()))
                        .map(a -> (missing.contains(a) ? "✗" : "✓") + " " + formatName(BuiltInRegistries.ENTITY_TYPE.getKey(a).getPath()))
                        .collect(Collectors.toList());
                break;
            }
            case 7: {
                Set<EntityType<?>> missing = AdvancementRequirements.getMissingHostileMobs(data);
                content = AdvancementRequirements.ALL_HOSTILE_MOBS.stream()
                        .sorted(Comparator.comparing(m -> BuiltInRegistries.ENTITY_TYPE.getKey(m).getPath()))
                        .map(m -> (missing.contains(m) ? "✗" : "✓") + " " + formatName(BuiltInRegistries.ENTITY_TYPE.getKey(m).getPath()))
                        .collect(Collectors.toList());
                break;
            }
        }

        return content;
    }

    private int getTotalCount() {
        return switch (currentCategory) {
            case 0 -> AdvancementRequirements.ALL_BIOMES.size();
            case 1 -> AdvancementRequirements.ALL_NETHER_BIOMES.size();
            case 2 -> AdvancementRequirements.ALL_CAT_VARIANTS.size();
            case 3 -> AdvancementRequirements.ALL_WOLF_VARIANTS.size();
            case 4 -> AdvancementRequirements.ALL_FROG_VARIANTS.size();
            case 5 -> AdvancementRequirements.ALL_EDIBLE_ITEMS.size();
            case 6 -> AdvancementRequirements.ALL_BREEDABLE_ANIMALS.size();
            case 7 -> AdvancementRequirements.ALL_HOSTILE_MOBS.size();
            default -> 0;
        };
    }

    private String formatName(String name) {
        String replaced = name.replace("_", " ");
        String[] words = replaced.split(" ");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (i > 0) result.append(" ");
            if (!words[i].isEmpty()) {
                result.append(Character.toUpperCase(words[i].charAt(0)));
                if (words[i].length() > 1) {
                    result.append(words[i].substring(1).toLowerCase());
                }
            }
        }
        return result.toString();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
