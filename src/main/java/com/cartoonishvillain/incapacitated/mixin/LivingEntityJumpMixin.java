package com.cartoonishvillain.incapacitated.mixin;

import com.cartoonishvillain.incapacitated.components.IncapacitationWorker;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityJumpMixin {

    @Inject(at = @At("HEAD"), method = "jumpFromGround")
    private void jumpFromGround(CallbackInfo ci){
        LivingEntity entity = ((LivingEntity) (Object) this);
        if(entity instanceof Player && !entity.level.isClientSide)
        IncapacitationWorker.jump((Player) entity);
    }
}
