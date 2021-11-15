package com.cartoonishvillain.incapacitated.mixin;

import com.cartoonishvillain.incapacitated.components.IncapacitationWorker;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class LocalPlayerTickMixin {
    @Inject(at = @At("HEAD"), method = "tick")
    private void tick(CallbackInfo ci){
        IncapacitationWorker.tick((LocalPlayer) (Object) this);
    }
}
