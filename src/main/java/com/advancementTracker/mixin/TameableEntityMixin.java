package com.advancementTracker.mixin;

import com.advancementTracker.manager.AdvancementTrackingManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TameableEntity.class)
public class TameableEntityMixin {

    @Inject(method = "setOwner", at = @At("TAIL"))
    private void onSetOwner(LivingEntity owner, CallbackInfo ci) {
        if (owner instanceof ServerPlayerEntity serverPlayer && !owner.getEntityWorld().isClient()) {
            TameableEntity entity = (TameableEntity) (Object) this;
            AdvancementTrackingManager.onAnimalTamed(serverPlayer, entity);
        }
    }
}
