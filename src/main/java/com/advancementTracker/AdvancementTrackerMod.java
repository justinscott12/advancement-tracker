package com.advancementTracker;

import com.advancementTracker.manager.AdvancementTrackingManager;
import com.advancementTracker.network.AdvancementTrackerNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdvancementTrackerMod implements ModInitializer {
    public static final String MOD_ID = "advancement-tracker";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Advanced Advancement Tracker mod initialized!");

        // Register payload types first
        AdvancementTrackerNetworking.registerPayloads();

        // Initialize tracking manager
        AdvancementTrackingManager.init();

        // Register server networking
        AdvancementTrackerNetworking.registerServerNetworking();

        // Register server lifecycle events
        ServerLifecycleEvents.SERVER_STARTED.register(AdvancementTrackingManager::onServerStart);

        ServerLifecycleEvents.SERVER_STOPPING.register(AdvancementTrackingManager::onServerStop);
    }

}
