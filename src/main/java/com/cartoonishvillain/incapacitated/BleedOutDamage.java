package com.cartoonishvillain.incapacitated;

import com.cartoonishvillain.incapacitated.mixin.DamageSourceInvoker;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class BleedOutDamage extends DamageSource {
    private final DamageSource originalSource;
    public BleedOutDamage(DamageSource originalKillMethod) {
        super("bleedout");
        originalSource = originalKillMethod;
    }

    @Override
    public Component getLocalizedDeathMessage(LivingEntity livingEntity) {
        Component originalDeathMsg = originalSource.getLocalizedDeathMessage(livingEntity);
        return new TranslatableComponent("death.attack." + this.msgId, originalDeathMsg);
    }

    public static DamageSource playerOutOfTime(Entity entity){
        return ((DamageSourceInvoker) new EntityDamageSource("bleedout", entity)).IncapacitatedInvokeBypassArmor();
    }
}