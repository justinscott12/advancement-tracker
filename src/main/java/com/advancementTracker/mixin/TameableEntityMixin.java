package com.advancementTracker.mixin;

import com.advancementTracker.manager.AdvancementTrackingManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TamableAnimal.class)
public class TameableEntityMixin {

    @Inject(method = "setOwner", at = @At("TAIL"))
    private void onSetOwner(LivingEntity owner, CallbackInfo ci) {
        if (owner instanceof ServerPlayer serverPlayer && !owner.level().isClientSide()) {
            TamableAnimal entity = (TamableAnimal) (Object) this;
            AdvancementTrackingManager.onAnimalTamed(serverPlayer, entity);
        }
    }
}
