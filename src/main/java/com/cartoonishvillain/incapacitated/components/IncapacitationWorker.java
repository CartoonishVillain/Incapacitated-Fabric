package com.cartoonishvillain.incapacitated.components;

import com.cartoonishvillain.incapacitated.BleedOutDamage;
import com.cartoonishvillain.incapacitated.Incapacitated;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

import static com.cartoonishvillain.incapacitated.components.ComponentStarter.PLAYERCOMPONENTINSTANCE;

public class IncapacitationWorker {

    public static void deathEvent(DamageSource damageSource, Player player, CallbackInfo ci){
        PlayerComponent h = PLAYERCOMPONENTINSTANCE.get(player);
        //if the player is not already incapacitated
        if (!h.getIsIncapacitated()) {
            //reduce downs until death
            h.setDownsUntilDeath(h.getDownsUntilDeath() - 1);
            //if downs until death is 0 or higher, we can cancel the death event because the user is down.
            if (h.getDownsUntilDeath() > -1) {
                h.setIsIncapacitated(true);
                ci.cancel();
                player.setHealth(player.getMaxHealth());
                if(Incapacitated.config.config.GLOWING)
                    player.addEffect(new MobEffectInstance(MobEffects.GLOWING, Integer.MAX_VALUE, 0));

                ArrayList<Player> playerEntities = (ArrayList<Player>) player.level.getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(50));

                for(Player players : playerEntities) {
                    players.displayClientMessage(new TextComponent(player.getScoreboardName() + " is incapacitated!"), false);
                }

            }
        }
    }

    public static void tick(Player player){
        PlayerComponent h = PLAYERCOMPONENTINSTANCE.get(player);
//        if(h.getIsIncapacitated()){
//            player.setPose(Pose.SWIMMING);
//        }

        if(!player.level.isClientSide() && h.getIsIncapacitated()){
            ArrayList<Player> playerEntities = (ArrayList<Player>) player.level.getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(3));
            boolean reviving = false;

            Player revivingPlayer = null;
            for(Player players : playerEntities) {
                PlayerComponent y = PLAYERCOMPONENTINSTANCE.get(players);
                if (players.isCrouching() && !y.getIsIncapacitated()) {
                    reviving = true;
                    revivingPlayer = player;
                    break;
                }
            }

            if(reviving){
                if(h.downReviveCount()){
                    h.setIsIncapacitated(false);
                    h.setReviveCount(Incapacitated.config.config.REVIVETICKS);
                    h.resetGiveUpJumps();
                    player.removeEffect(MobEffects.GLOWING);
                    player.setHealth(player.getMaxHealth()/3f);
                    player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_PLING, SoundSource.PLAYERS, 1, 1);

                }else{
                    player.displayClientMessage(new TextComponent("You are being revived! " + (float)h.getReviveCount()/20f + " seconds..").withStyle(ChatFormatting.GREEN), true);
                    revivingPlayer.displayClientMessage(new TextComponent("Reviving " + player.getScoreboardName() + " " + (float)h.getReviveCount()/20f + " seconds...").withStyle(ChatFormatting.GREEN), true);
                }
            }
            else {
                if (h.countTicksUntilDeath()) {
                    player.hurt(BleedOutDamage.playerOutOfTime(player), player.getMaxHealth() * 10);
                    h.setReviveCount(Incapacitated.config.config.REVIVETICKS);
                    h.resetGiveUpJumps();
                    player.removeEffect(MobEffects.GLOWING);
                    h.setIsIncapacitated(false);
                } else if (h.getTicksUntilDeath() % 20 == 0) {
                    player.displayClientMessage(new TextComponent("Incapacitated! Call for help or jump " + h.getJumpCount() + " times to give up! " + ((float) h.getTicksUntilDeath() / 20f) + " seconds left!").withStyle(ChatFormatting.RED), true);
                }

                if(h.getReviveCount() != Incapacitated.config.config.REVIVETICKS) h.setReviveCount(Incapacitated.config.config.REVIVETICKS);
            }
        }

    }
}
