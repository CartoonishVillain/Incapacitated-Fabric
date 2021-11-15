package com.cartoonishvillain.incapacitated;

import com.cartoonishvillain.incapacitated.mixin.DamageSourceInvoker;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;

public class BleedOutDamage extends DamageSource {
    public BleedOutDamage(String p_i1566_1_) {
        super(p_i1566_1_);
    }
    public static DamageSource playerOutOfTime(Entity entity){
        return ((DamageSourceInvoker) new EntityDamageSource("bleedout", entity)).IncapacitatedInvokeBypassArmor();
    }
}