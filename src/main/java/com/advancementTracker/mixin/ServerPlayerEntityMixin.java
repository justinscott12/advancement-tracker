package com.advancementTracker.mixin;

import com.advancementTracker.manager.AdvancementTrackingManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerEntityMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        Level world = player.level();
        BlockPos pos = player.blockPosition();

        // Check biome every second to avoid performance issues
        if (player.tickCount % 20 == 0) {
            Identifier biomeId = world.getBiome(pos).unwrapKey().orElseThrow().identifier();
            if (biomeId != null) {
                AdvancementTrackingManager.onBiomeDiscovered(player, biomeId);
            }
        }
    }
}
