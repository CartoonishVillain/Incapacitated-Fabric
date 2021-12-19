package com.cartoonishvillain.incapacitated.mixin;

import com.cartoonishvillain.incapacitated.components.IncapacitationWorker;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerPlayer.class, priority = 99999)
public class PlayerDeathMixin {

    @Inject(at = @At("HEAD"), method = "die", cancellable = true)
    private void die(DamageSource damageSource, CallbackInfo ci){
        Player player = ((Player) (Object) this);
        if (!ci.isCancelled()) {
            IncapacitationWorker.deathEvent(damageSource, player, ci);
        }
    }
}
