package com.advancementTracker.network;

import com.advancementTracker.AdvancementTrackerMod;
import com.advancementTracker.data.PlayerTrackingData;
import com.advancementTracker.manager.AdvancementTrackingManager;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class AdvancementTrackerNetworking {

    // Packet identifiers
    public static final Identifier SYNC_PLAYER_DATA_ID = Identifier.fromNamespaceAndPath(AdvancementTrackerMod.MOD_ID, "sync_player_data");
    public static final Identifier REQUEST_PLAYER_DATA_ID = Identifier.fromNamespaceAndPath(AdvancementTrackerMod.MOD_ID, "request_player_data");

    // Custom payload records
    public record SyncPlayerDataPayload(UUID playerId, CompoundTag nbtData) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<SyncPlayerDataPayload> ID = new CustomPacketPayload.Type<>(SYNC_PLAYER_DATA_ID);
        public static final StreamCodec<RegistryFriendlyByteBuf, SyncPlayerDataPayload> CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC, SyncPlayerDataPayload::playerId,
                ByteBufCodecs.COMPOUND_TAG, SyncPlayerDataPayload::nbtData,
                SyncPlayerDataPayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return ID;
        }
    }

    public record RequestPlayerDataPayload(UUID playerId) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<RequestPlayerDataPayload> ID = new CustomPacketPayload.Type<>(REQUEST_PLAYER_DATA_ID);
        public static final StreamCodec<RegistryFriendlyByteBuf, RequestPlayerDataPayload> CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC, RequestPlayerDataPayload::playerId,
                RequestPlayerDataPayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return ID;
        }
    }

    public static void registerPayloads() {
        PayloadTypeRegistry.clientboundPlay().register(SyncPlayerDataPayload.ID, SyncPlayerDataPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(RequestPlayerDataPayload.ID, RequestPlayerDataPayload.CODEC);
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
                    PlayerTrackingData emptyData = new PlayerTrackingData(requestedPlayerId);
                    sendPlayerDataToClient(context.player(), emptyData);
                }
            });
        });
    }

    // Server-side method to send player data to client
    public static void sendPlayerDataToClient(ServerPlayer player, PlayerTrackingData data) {
        SyncPlayerDataPayload payload = new SyncPlayerDataPayload(data.getPlayerId(), data.writeToNbt());
        ServerPlayNetworking.send(player, payload);
        AdvancementTrackerMod.LOGGER.debug("Sent player data to client for: {}", data.getPlayerId());
    }
}
