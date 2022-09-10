package com.cartoonishvillain.incapacitated.components;

import com.cartoonishvillain.incapacitated.Incapacitated;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.UUID;

import static com.cartoonishvillain.incapacitated.components.ComponentStarter.PLAYERCOMPONENTINSTANCE;

public class IncapacitationWorker {

    public static void deathEvent(DamageSource damageSource, Player player, CallbackInfo ci){
        PlayerComponent h = PLAYERCOMPONENTINSTANCE.get(player);
        //if the player is not already incapacitated
        if (!h.getIsIncapacitated() && !(Incapacitated.config.config.SOMEINSTANTKILLS && Incapacitated.instantKillDamageSourcesMessageID.contains(damageSource.getMsgId()))){
            //reduce downs until death
            h.setDownsUntilDeath(h.getDownsUntilDeath() - 1);
            //if downs until death is 0 or higher, we can cancel the death event because the user is down.
            if (h.getDownsUntilDeath() > -1) {
                h.setIsIncapacitated(true);
                h.setSourceOfDeath(damageSource);
                ci.cancel();
                player.setHealth(player.getMaxHealth());
                if(Incapacitated.config.config.GLOWING)
                    player.addEffect(new MobEffectInstance(MobEffects.GLOWING, Integer.MAX_VALUE, 0));

                if(Incapacitated.config.config.GLOBALINCAPMESSAGES){
                    broadcast(player.getServer(), Component.literal(player.getScoreboardName() + " is incapacitated!"));
                }
                else {
                    ArrayList<Player> playerEntities = (ArrayList<Player>) player.level.getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(50));

                    for(Player players : playerEntities) {
                        players.displayClientMessage(Component.literal(player.getScoreboardName() + " is incapacitated!"), false);
                    }
                }

            }
        }
    }

    public static void tick(Player player){
        PlayerComponent h = PLAYERCOMPONENTINSTANCE.get(player);

        if(!player.level.isClientSide() && h.getIsIncapacitated()){
            if(h.getJumpDelay() > 0){h.setJumpDelay(h.getJumpDelay()-1);}
            else if(h.getJumpDelay() < 0){h.setJumpDelay(0);}
            ArrayList<Player> playerEntities = (ArrayList<Player>) player.level.getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(3));
            boolean reviving = false;

            Player revivingPlayer = null;
            for(Player players : playerEntities) {
                PlayerComponent y = PLAYERCOMPONENTINSTANCE.get(players);
                if (players.isCrouching() && !y.getIsIncapacitated()) {
                    reviving = true;
                    revivingPlayer = players;
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
                    player.displayClientMessage(Component.literal("You are being revived! " + (h.getReviveCount() / 20) + " seconds..").withStyle(ChatFormatting.GREEN), true);
                    revivingPlayer.displayClientMessage(Component.literal("Reviving " + player.getScoreboardName() + " " + (int) (h.getReviveCount() / 20) + " seconds...").withStyle(ChatFormatting.GREEN), true);
                }
            }
            else {
                if (h.countTicksUntilDeath()) {
                    player.hurt(h.getSourceOfDeath(), player.getMaxHealth() * 10);
                    //if something is trying to keep them from dying at this point, kill them again.
                    player.kill();
                    h.resetGiveUpJumps();
                } else if (h.getTicksUntilDeath() % 20 == 0) {
                    player.displayClientMessage(Component.literal("Incapacitated! Call for help or jump " + h.getJumpCount() + " times to give up! " + ((float) h.getTicksUntilDeath() / 20f) + " seconds left!").withStyle(ChatFormatting.RED), true);
                }

                if(h.getReviveCount() != Incapacitated.config.config.REVIVETICKS) h.setReviveCount(Incapacitated.config.config.REVIVETICKS);
            }
        }

    }

    public static void jump(Player player){
        PlayerComponent h = PLAYERCOMPONENTINSTANCE.get(player);
        if(h.getIsIncapacitated() && h.getJumpDelay() == 0){
            if(h.giveUpJumpCount()){
                player.hurt(h.getSourceOfDeath(), player.getMaxHealth() * 10);
                //if something tries to revive them, kill them again. No free passes.
                player.kill();
                h.resetGiveUpJumps();
                player.removeEffect(MobEffects.GLOWING);
            }
        }
    }
    public static void hurt(Player player, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir){
        PlayerComponent h = PLAYERCOMPONENTINSTANCE.get(player);
        if(h.getIsIncapacitated() && Incapacitated.config.config.INVINCIBLEDOWN && !damageSource.getMsgId().equals("bleedout")){
            cir.cancel();
            return;
        }
        h.setJumpDelay(20);
    }

    public static void eat(LivingEntity entity, ItemStack itemStack){
        if(entity instanceof Player player && !entity.level.isClientSide()){
            Item item = itemStack.getItem();
            PlayerComponent h = PLAYERCOMPONENTINSTANCE.get(player);
                if(Incapacitated.HealingFoods.contains(item.toString())) {h.setDownsUntilDeath(Incapacitated.config.config.DOWNCOUNT); h.setTicksUntilDeath(Incapacitated.config.config.DOWNTICKS);}
                if(h.getIsIncapacitated()){
                    if(Incapacitated.ReviveFoods.contains(item.toString())){
                        h.setIsIncapacitated(false);
                        h.setReviveCount(Incapacitated.config.config.REVIVETICKS);
                        h.resetGiveUpJumps();
                        h.setDownsUntilDeath(Incapacitated.config.config.DOWNCOUNT);
                        player.removeEffect(MobEffects.GLOWING);
                        h.setTicksUntilDeath(Incapacitated.config.config.DOWNTICKS);
                        player.setHealth(player.getMaxHealth()/3f);
                        player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_PLING, SoundSource.PLAYERS, 1, 1);
                    }
                }else if(Incapacitated.ReviveFoods.contains(item.toString())) {h.setDownsUntilDeath(Incapacitated.config.config.DOWNCOUNT); h.setTicksUntilDeath(Incapacitated.config.config.DOWNTICKS);}
        }

    }

    private static void broadcast(MinecraftServer server, Component translationTextComponent){
        server.getPlayerList().broadcastSystemMessage(translationTextComponent, false);
    }
}
