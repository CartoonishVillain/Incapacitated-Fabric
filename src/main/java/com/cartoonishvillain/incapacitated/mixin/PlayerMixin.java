package com.cartoonishvillain.incapacitated.mixin;

import com.cartoonishvillain.incapacitated.components.PlayerComponent;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.cartoonishvillain.incapacitated.components.ComponentStarter.PLAYERCOMPONENTINSTANCE;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(at = @At("HEAD"), method = "updatePlayerPose", cancellable = true)
    private void updatePlayerPose(CallbackInfo ci){
        PlayerComponent playerComponent = PLAYERCOMPONENTINSTANCE.get((Player) (Object) this);
        if(playerComponent.getIsIncapacitated()){
            ((Player) (Object) this).setPose(Pose.SWIMMING);
            ci.cancel();
        }
    }
}
