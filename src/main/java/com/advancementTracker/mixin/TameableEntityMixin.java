package com.advancementTracker.mixin;

import com.advancementTracker.manager.AdvancementTrackingManager;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TameableEntity.class)
public class TameableEntityMixin {

    // Read the owner via getOwner() at TAIL instead of capturing the setOwner
    // argument, so this is robust to setOwner(...)'s signature differences
    // across the 1.21.x line.
    @Inject(method = "setOwner", at = @At("TAIL"))
    private void onSetOwner(CallbackInfo ci) {
        TameableEntity entity = (TameableEntity) (Object) this;
        if (!entity.getWorld().isClient
                && entity.getOwner() instanceof ServerPlayerEntity serverPlayer) {
            AdvancementTrackingManager.onAnimalTamed(serverPlayer, entity);
        }
    }
}
