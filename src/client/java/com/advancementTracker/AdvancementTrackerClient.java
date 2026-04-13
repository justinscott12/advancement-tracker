package com.advancementTracker;

import com.advancementTracker.client.gui.AdvancementTrackerScreen;
import com.advancementTracker.network.AdvancementTrackerClientNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

public class AdvancementTrackerClient implements ClientModInitializer {

    private static KeyBinding openScreenKeybinding;

    @Override
    public void onInitializeClient() {
        // Register client networking
        AdvancementTrackerClientNetworking.registerClientNetworking();

        // Register keybinding
        openScreenKeybinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.advancement-tracker.open",
                InputUtil.Type.KEYSYM,
                InputUtil.GLFW_KEY_J,
                KeyBinding.Category.create(Identifier.of("advancement-tracker", "general"))
        ));

        // Register client tick event to handle keybinding
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openScreenKeybinding.wasPressed()) {
                client.setScreen(new AdvancementTrackerScreen());
            }
        });
    }
}