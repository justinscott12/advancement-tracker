package com.advancementTracker.network;

import com.advancementTracker.AdvancementTrackerMod;
import com.advancementTracker.data.PlayerTrackingData;
import com.advancementTracker.manager.AdvancementTrackingManager;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

public class AdvancementTrackerNetworking {

    // Packet identifiers
    public static final Identifier SYNC_PLAYER_DATA_ID = Identifier.of(AdvancementTrackerMod.MOD_ID, "sync_player_data");
    public static final Identifier REQUEST_PLAYER_DATA_ID = Identifier.of(AdvancementTrackerMod.MOD_ID, "request_player_data");

    // Custom payload records
    public record SyncPlayerDataPayload(UUID playerId, NbtCompound nbtData) implements CustomPayload {
        public static final CustomPayload.Id<SyncPlayerDataPayload> ID = new CustomPayload.Id<>(SYNC_PLAYER_DATA_ID);
        public static final PacketCodec<RegistryByteBuf, SyncPlayerDataPayload> CODEC = PacketCodec.tuple(
                Uuids.PACKET_CODEC, SyncPlayerDataPayload::playerId,
                PacketCodecs.NBT_COMPOUND, SyncPlayerDataPayload::nbtData,
                SyncPlayerDataPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public record RequestPlayerDataPayload(UUID playerId) implements CustomPayload {
        public static final CustomPayload.Id<RequestPlayerDataPayload> ID = new CustomPayload.Id<>(REQUEST_PLAYER_DATA_ID);
        public static final PacketCodec<RegistryByteBuf, RequestPlayerDataPayload> CODEC = PacketCodec.tuple(
                Uuids.PACKET_CODEC, RequestPlayerDataPayload::playerId,
                RequestPlayerDataPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public static void registerPayloads() {
        // Register payload types
        PayloadTypeRegistry.playS2C().register(SyncPlayerDataPayload.ID, SyncPlayerDataPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(RequestPlayerDataPayload.ID, RequestPlayerDataPayload.CODEC);
    }

    public static void registerServerNetworking() {
        // Handle client requests for player data
        ServerPlayNetworking.registerGlobalReceiver(RequestPlayerDataPayload.ID, (payload, context) -> {
            UUID requestedPlayerId = payload.playerId();

            context.server().execute(() -> {
                PlayerTrackingData data = AdvancementTrackingManager.getPlayerData(requestedPlayerId);
                if (data != null) {
                    sendPlayerDataToClient(context.player(), data);
                } else {
                    // Send empty data if no data exists yet
                    PlayerTrackingData emptyData = new PlayerTrackingData(requestedPlayerId);
                    sendPlayerDataToClient(context.player(), emptyData);
                }
            });
        });
    }

    // Server-side method to send player data to client
    public static void sendPlayerDataToClient(ServerPlayerEntity player, PlayerTrackingData data) {
        SyncPlayerDataPayload payload = new SyncPlayerDataPayload(data.getPlayerId(), data.writeToNbt());
        ServerPlayNetworking.send(player, payload);
        AdvancementTrackerMod.LOGGER.debug("Sent player data to client for: {}", data.getPlayerId());
    }
}