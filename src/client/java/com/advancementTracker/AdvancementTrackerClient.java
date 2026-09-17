package com.advancementTracker;

import com.advancementTracker.client.gui.AdvancementTrackerScreen;
import com.advancementTracker.manager.ClientDataManager;
import com.advancementTracker.network.AdvancementTrackerClientNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;

public class AdvancementTrackerClient implements ClientModInitializer {

    private static KeyMapping openScreenKeybinding;

    @Override
    public void onInitializeClient() {
        // Register client networking
        AdvancementTrackerClientNetworking.registerClientNetworking();

        // Wipe cached data when leaving a world so the next world doesn't
        // display the previous world's progress.
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ClientDataManager.clear());

        // Pull fresh data for the local player as soon as we join a world.
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (client.player != null) {
                AdvancementTrackerClientNetworking.requestPlayerData(client.player.getUUID());
            }
        });

        // Register keybinding (J by default)
        // 26.3: Minecraft moved from GLFW to SDL for input. Type.KEYSYM is now
        // Type.KEYBOARD, and key codes come from InputConstants (SDL scancodes)
        // instead of org.lwjgl.glfw.GLFW.
        openScreenKeybinding = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "Advancement Tracker",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_J,
                KeyMapping.Category.MISC
        ));

        // Register client tick event to handle keybinding
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openScreenKeybinding.consumeClick()) {
                client.setScreenAndShow(new AdvancementTrackerScreen());
            }
        });
    }
}
