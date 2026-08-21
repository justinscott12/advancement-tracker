package com.advancementTracker;

import com.advancementTracker.client.gui.AdvancementTrackerScreen;
import com.advancementTracker.network.AdvancementTrackerClientNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public class AdvancementTrackerClient implements ClientModInitializer {

    private static KeyMapping openScreenKeybinding;

    @Override
    public void onInitializeClient() {
        // Register client networking
        AdvancementTrackerClientNetworking.registerClientNetworking();

        // Register keybinding (J by default)
        openScreenKeybinding = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "Advancement Tracker",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_J,
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
