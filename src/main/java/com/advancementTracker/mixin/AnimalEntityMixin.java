package com.advancementTracker.mixin;

import com.advancementTracker.manager.AdvancementTrackingManager;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnimalEntity.class)
public class AnimalEntityMixin {

    @Inject(method = "breed(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/AnimalEntity;Lnet/minecraft/entity/passive/PassiveEntity;)V", at = @At("HEAD"))
    private void breed(ServerWorld world, AnimalEntity other, PassiveEntity baby, CallbackInfo ci) {
        AnimalEntity self = (AnimalEntity) (Object) this;
        PlayerEntity lovingPlayer = self.getLovingPlayer();

        if (lovingPlayer instanceof ServerPlayerEntity serverPlayer) {
            AdvancementTrackingManager.onAnimalBred(serverPlayer, self);
        }
    }
}
