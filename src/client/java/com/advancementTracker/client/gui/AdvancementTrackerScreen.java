package com.advancementTracker.client.gui;

import com.advancementTracker.data.AdvancementRequirements;
import com.advancementTracker.data.PlayerTrackingData;
import com.advancementTracker.manager.ClientDataManager;
import com.advancementTracker.network.AdvancementTrackerClientNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;

import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        super(Text.literal("Advancement Tracker"));
    }

    @Override
    protected void init() {
        super.init();

        // Category selection buttons
        int startX = 10;
        int startY = 30;

        for (int i = 0; i < categories.length; i++) {
            final int categoryIndex = i;
            ButtonWidget button = ButtonWidget.builder(
                            Text.literal(categories[i]),
                            btn -> {
                                currentCategory = categoryIndex;
                                scrollOffset = 0;
                            }
                    )
                    .dimensions(startX, startY + i * 25, BUTTON_WIDTH, BUTTON_HEIGHT)
                    .build();

            this.addDrawableChild(button);
        }

        // Close button
        this.addDrawableChild(ButtonWidget.builder(
                        Text.literal("Close"),
                        btn -> this.close()
                )
                .dimensions(this.width / 2 - 50, this.height - 30, 100, 20)
                .build());
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderInGameBackground(context);

        // Call super.render to handle buttons and other UI elements
        super.render(context, mouseX, mouseY, delta);

        renderCategoryContent(context);
    }

    private void renderCategoryContent(DrawContext context) {
        System.out.println("[AdvancementTrackerScreen] Inside renderCategoryContent().");

        int buttonAreaRight = 10 + BUTTON_WIDTH;
        int minContentStartX = buttonAreaRight + 10;
        int contentWidthEstimate = 600;
        int desiredCenterX = (this.width + minContentStartX) / 2;
        int startX = Math.max(minContentStartX, desiredCenterX - contentWidthEstimate / 2);
        int contentBlockStartY = 50;

        // Get player data
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            context.drawTextWithShadow(this.textRenderer, Text.literal("Player not available."), startX, contentBlockStartY, 0xFFFFFFFF);
            return;
        }

        PlayerTrackingData data = ClientDataManager.getPlayerData(client.player.getUuid());
        System.out.println("[AdvancementTrackerScreen] Player UUID: " + client.player.getUuid());
        System.out.println("[AdvancementTrackerScreen] Retrieved player data: " + (data != null ? "found" : "null"));

        // If no data available, request it from server
        if (data == null && client.getNetworkHandler() != null) {
            AdvancementTrackerClientNetworking.requestPlayerData(client.player.getUuid());
            // Create empty data temporarily for display
            data = new PlayerTrackingData(client.player.getUuid());
            System.out.println("[AdvancementTrackerScreen] Requested player data from server");
        } else if (data == null) {
            data = new PlayerTrackingData(client.player.getUuid());
        }

        // Display category title
        context.drawTextWithShadow(this.textRenderer,
                Text.literal("Category: " + categories[currentCategory]).formatted(Formatting.YELLOW),
                startX, contentBlockStartY, 0xFFFFFFFF);

        // Display completion progress
        int completed = getCompletedCount(data);
        int total = getTotalCount();
        context.drawTextWithShadow(this.textRenderer,
                Text.literal("Progress: " + completed + "/" + total).formatted(Formatting.AQUA),
                startX, contentBlockStartY + 20, 0xFFFFFFFF);

        // Rest of the rendering code remains the same...
        // Display specific items in category
        List<String> categoryContent = getCategoryContent(data);
        int itemListStartY = contentBlockStartY + 45;

        if (categoryContent.isEmpty()) {
            context.drawTextWithShadow(this.textRenderer, Text.literal("No items in this category yet."), startX, itemListStartY, 0xFF888888);
        } else {
            // Multi-column layout (same as before)
            final int ITEMS_PER_COLUMN = 25;
            final int COLUMN_WIDTH = 200;
            final int LINE_HEIGHT = 12;

            int availableHeight = this.height - itemListStartY - 40;
            int maxRowsPerColumn = Math.min(ITEMS_PER_COLUMN, availableHeight / LINE_HEIGHT);

            for (int i = 0; i < categoryContent.size() && i < maxRowsPerColumn * 3; i++) {
                int contentIndex = scrollOffset + i;
                if (contentIndex >= categoryContent.size()) break;

                int column = i / maxRowsPerColumn;
                int row = i % maxRowsPerColumn;
                int itemX = startX + (column * COLUMN_WIDTH);
                int itemY = itemListStartY + (row * LINE_HEIGHT);

                String currentItemString = categoryContent.get(contentIndex);

                Text displayText;
                int textColor;
                if (currentItemString.startsWith("\u2713")) { // Checkmark (✓)
                    displayText = Text.literal(currentItemString);
                    textColor = 0xFF00FF00; // Green
                } else if (currentItemString.startsWith("\u2717")) { // Ballot X (✗)
                    displayText = Text.literal(currentItemString);
                    textColor = 0xFFFF0000; // Red
                } else {
                    displayText = Text.literal(currentItemString);
                    textColor = 0xFFFFFFFF; // White
                }

                // Truncate text if too long
                String displayString = displayText.getString();
                int maxWidth = COLUMN_WIDTH - 10;
                if (this.textRenderer.getWidth(displayString) > maxWidth) {
                    while (this.textRenderer.getWidth(displayString + "...") > maxWidth && displayString.length() > 1) {
                        displayString = displayString.substring(0, displayString.length() - 1);
                    }
                    displayText = Text.literal(displayString + "...");
                }

                context.drawTextWithShadow(this.textRenderer, displayText, itemX, itemY, textColor);
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
            case 0: // Adventuring Time
                Set<Identifier> missingBiomes = AdvancementRequirements.getMissingBiomes(data);

                for (Identifier biome : AdvancementRequirements.ALL_BIOMES) {
                    String status = missingBiomes.contains(biome) ? "\u2717" : "\u2713"; // Unicode for ✗ and ✓
                    content.add(status + " " + formatName(biome.getPath()));
                }
                break;

            case 1: // Hot Tourist Destinations
                Set<Identifier> missingNetherBiomes = AdvancementRequirements.getMissingNetherBiomes(data);

                for (Identifier biome : AdvancementRequirements.ALL_NETHER_BIOMES) {
                    String status = missingNetherBiomes.contains(biome) ? "\u2717" : "\u2713"; // Unicode for ✗ and ✓
                    content.add(status + " " + formatName(biome.getPath()));
                }
                break;

            case 2: // A Complete Catalogue
                Set<Identifier> missingCats = AdvancementRequirements.getMissingCats(data);

                for (Identifier catVariant : AdvancementRequirements.ALL_CAT_VARIANTS) {
                    String status = missingCats.contains(catVariant) ? "\u2717" : "\u2713"; // Unicode for ✗ and ✓
                    content.add(status + " " + formatName(catVariant.getPath())); // Display variant name
                }
                break;

            case 3: // The Whole Pack
                Set<Identifier> missingWolves = AdvancementRequirements.getMissingWolves(data);

                for (Identifier wolfVariant : AdvancementRequirements.ALL_WOLF_VARIANTS) {
                    String status = missingWolves.contains(wolfVariant) ? "\u2717" : "\u2713"; // Unicode for ✗ and ✓
                    content.add(status + " " + formatName(wolfVariant.getPath())); // Display variant name
                }
                break;

            case 4: // When the Squad Hops into Town
                Set<Identifier> missingFrogs = AdvancementRequirements.getMissingFrogs(data);

                for (Identifier frogVariant : AdvancementRequirements.ALL_FROG_VARIANTS) {
                    String status = missingFrogs.contains(frogVariant) ? "\u2717" : "\u2713"; // Unicode for ✗ and ✓
                    content.add(status + " " + formatName(frogVariant.getPath())); // Display variant name
                }
                break;

            case 5: // A Balanced Diet
                Set<Item> missingFoods = AdvancementRequirements.getMissingFoods(data);

                for (Item food : AdvancementRequirements.ALL_EDIBLE_ITEMS) {
                    String status = missingFoods.contains(food) ? "\u2717" : "\u2713"; // Unicode for ✗ and ✓
                    content.add(status + " " + formatName(Registries.ITEM.getId(food).getPath()));
                }
                break;

            case 6: // Two by Two
                Set<EntityType<?>> missingAnimals = AdvancementRequirements.getMissingBreedableAnimals(data);

                for (EntityType<?> animal : AdvancementRequirements.ALL_BREEDABLE_ANIMALS) {
                    String status = missingAnimals.contains(animal) ? "\u2717" : "\u2713"; // Unicode for ✗ and ✓
                    content.add(status + " " + formatName(Registries.ENTITY_TYPE.getId(animal).getPath()));
                }
                break;

            case 7: // Monsters Hunted
                Set<EntityType<?>> missingMobs = AdvancementRequirements.getMissingHostileMobs(data);

                for (EntityType<?> mob : AdvancementRequirements.ALL_HOSTILE_MOBS) {
                    String status = missingMobs.contains(mob) ? "\u2717" : "\u2713"; // Unicode for ✗ and ✓
                    content.add(status + " " + formatName(Registries.ENTITY_TYPE.getId(mob).getPath()));
                }
                break;
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
        // Capitalize first letter of each word
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
    public boolean shouldPause() {
        return false;
    }
}