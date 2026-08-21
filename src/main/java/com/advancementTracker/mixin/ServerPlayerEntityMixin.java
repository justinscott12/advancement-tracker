package com.advancementTracker.mixin;

import com.advancementTracker.manager.AdvancementTrackingManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        ServerWorld world = player.getServerWorld();
        BlockPos pos = player.getBlockPos();

        // Check biome every few ticks to avoid performance issues
        if (player.age % 20 == 0) { // Every second
            var biomeId = world.getBiome(pos).getKey().orElseThrow().getValue();
            if (biomeId != null) {
                AdvancementTrackingManager.onBiomeDiscovered(player, biomeId);
            }
        }
    }
}
