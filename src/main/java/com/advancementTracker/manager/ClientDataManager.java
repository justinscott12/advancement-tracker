package com.advancementTracker.manager;

import com.advancementTracker.data.PlayerTrackingData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class ClientDataManager {
    private static final Map<UUID, PlayerTrackingData> clientPlayerData = new HashMap<>();

    public static void updatePlayerData(UUID playerId, PlayerTrackingData data) {
        clientPlayerData.put(playerId, data);
    }

    public static PlayerTrackingData getPlayerData(UUID playerId) {
        return clientPlayerData.get(playerId);
    }

    // Drop all cached data when leaving a world so the next world doesn't
    // display the previous world's progress (client stays alive across worlds).
    public static void clear() {
        clientPlayerData.clear();
    }
}
