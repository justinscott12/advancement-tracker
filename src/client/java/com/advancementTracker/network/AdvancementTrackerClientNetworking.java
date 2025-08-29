package com.advancementTracker.network;

import com.advancementTracker.AdvancementTrackerMod;
import com.advancementTracker.data.PlayerTrackingData;
import com.advancementTracker.manager.ClientDataManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import java.util.UUID;

public class AdvancementTrackerClientNetworking {

    public static void registerClientNetworking() {
        // Handle server sending player data to client
        ClientPlayNetworking.registerGlobalReceiver(AdvancementTrackerNetworking.SyncPlayerDataPayload.ID, (payload, context) -> {
            UUID playerId = payload.playerId();

            context.client().execute(() -> {
                try {
                    if (payload.nbtData() != null) {
                        PlayerTrackingData data = PlayerTrackingData.readFromNbt(payload.nbtData());
                        ClientDataManager.updatePlayerData(playerId, data);
                        AdvancementTrackerMod.LOGGER.info("Received and updated player data for: {}", playerId);
                    }
                } catch (Exception e) {
                    AdvancementTrackerMod.LOGGER.error("Failed to process received player data", e);
                }
            });
        });
    }

    // Client-side method to request player data from server
    public static void requestPlayerData(UUID playerId) {
        AdvancementTrackerNetworking.RequestPlayerDataPayload payload =
                new AdvancementTrackerNetworking.RequestPlayerDataPayload(playerId);
        ClientPlayNetworking.send(payload);
        AdvancementTrackerMod.LOGGER.debug("Requested player data from server for: {}", playerId);
    }
}