package com.advancementTracker;

import com.advancementTracker.client.gui.AdvancementTrackerScreen;
import com.advancementTracker.manager.ClientDataManager;
import com.advancementTracker.network.AdvancementTrackerClientNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class AdvancementTrackerClient implements ClientModInitializer {

    private static KeyBinding openScreenKeybinding;

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
                AdvancementTrackerClientNetworking.requestPlayerData(client.player.getUuid());
            }
        });

        // Register keybinding
        openScreenKeybinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Advancement Tracker",
                InputUtil.Type.KEYSYM,
                InputUtil.GLFW_KEY_J,
                "category.advancement-tracker.general"
        ));

        // Register client tick event to handle keybinding
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openScreenKeybinding.wasPressed()) {
                client.setScreen(new AdvancementTrackerScreen());
            }
        });
    }
}