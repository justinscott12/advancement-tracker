package com.advancementTracker.mixin;

import com.advancementTracker.manager.AdvancementTrackingManager;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnimalEntity.class)
public class AnimalEntityMixin {

    // Target by name only (no descriptor / captured args) so this applies
    // regardless of breed(...)'s exact signature across the 1.21.x line
    // (1.21.1 has breed(ServerWorld, AnimalEntity); later versions added a
    // PassiveEntity baby parameter).
    @Inject(method = "breed", at = @At("HEAD"))
    private void breed(CallbackInfo ci) {
        AnimalEntity self = (AnimalEntity) (Object) this;
        PlayerEntity lovingPlayer = self.getLovingPlayer();

        if (lovingPlayer instanceof ServerPlayerEntity serverPlayer) {
            AdvancementTrackingManager.onAnimalBred(serverPlayer, self);
        }
    }
}
