package com.advancementTracker.mixin;

import com.advancementTracker.manager.AdvancementTrackingManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Animal.class)
public class AnimalEntityMixin {

    // 26.2: breeding entry point is spawnChildFromBreeding(ServerLevel, Animal).
    // getLoveCause() returns the ServerPlayer that triggered breeding (or null).
    @Inject(method = "spawnChildFromBreeding", at = @At("HEAD"))
    private void onBreed(CallbackInfo ci) {
        Animal self = (Animal) (Object) this;
        ServerPlayer lovingPlayer = self.getLoveCause();

        if (lovingPlayer != null) {
            AdvancementTrackingManager.onAnimalBred(lovingPlayer, self);
        }
    }
}
