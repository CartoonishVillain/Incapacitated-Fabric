package com.cartoonishvillain.incapacitated.components;


import com.cartoonishvillain.incapacitated.BleedOutDamage;
import com.cartoonishvillain.incapacitated.Incapacitated;
import com.cartoonishvillain.incapacitated.mixin.DamageSourceInvoker;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;

public class PlayerComponent implements ComponentV3, AutoSyncedComponent {
    private final Object provider;
    protected boolean incapacitated = false;
    protected int ticksUntilDeath = Incapacitated.config.config.DOWNTICKS;
    protected int downsUntilDeath = Incapacitated.config.config.DOWNCOUNT;
    protected int giveUpJumps = 3;
    protected int reviveCounter = Incapacitated.config.config.REVIVETICKS;
    protected int jumpDelay = 0;

    private DamageSource originalSource;

    public PlayerComponent(Object provider){this.provider = provider;}

    public boolean getIsIncapacitated() {
        return incapacitated;
    }

    public void setIsIncapacitated(boolean isIncapacitated) {
        incapacitated = isIncapacitated;
        ComponentStarter.PLAYERCOMPONENTINSTANCE.sync(this.provider);
    }

    public void sync() {
        ComponentStarter.PLAYERCOMPONENTINSTANCE.sync(this.provider);
    }

    public int getTicksUntilDeath() {
        return ticksUntilDeath;
    }

    public boolean countTicksUntilDeath() {
        ticksUntilDeath--;
        return ticksUntilDeath <= 0;
    }

    public void setTicksUntilDeath(int ticks) {
        ticksUntilDeath = ticks;
    }

    public int getDownsUntilDeath() {
        return downsUntilDeath;
    }

    public void setDownsUntilDeath(int downs) {
        downsUntilDeath = downs;
    }

    public boolean giveUpJumpCount() {
        giveUpJumps--;
        return giveUpJumps <= 0;
    }

    public void resetGiveUpJumps() {
        giveUpJumps = 3;
    }

    public int getJumpCount() {
        return giveUpJumps;
    }

    public void setJumpCount(int jumps) {
        giveUpJumps = jumps;
    }

    public boolean downReviveCount() {
        reviveCounter--;
        return reviveCounter <= 0;
    }

    public int getReviveCount() {
        return reviveCounter;
    }

    public void setReviveCount(int count) {
        reviveCounter = count;
    }

    public int getJumpDelay() {
        return jumpDelay;
    }

    public void setJumpDelay(int delay) {
        jumpDelay = delay;
    }

    public void countDelay() {
        if(jumpDelay > 0) jumpDelay--;
        else if(jumpDelay < 0) jumpDelay = 0;
    }

    public DamageSource getSourceOfDeath() {
        return originalSource != null
                ? ((DamageSourceInvoker) originalSource).IncapacitatedInvokeBypassArmor()
                : ((DamageSourceInvoker) new BleedOutDamage(DamageSource.OUT_OF_WORLD)).IncapacitatedInvokeBypassArmor();
    }

    public void setSourceOfDeath(DamageSource causeOfDeath) {
        originalSource = new BleedOutDamage(causeOfDeath);
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        incapacitated = tag.getBoolean("incapacitation");
        ticksUntilDeath = tag.getInt("incapTimer");
        downsUntilDeath = tag.getInt("incapCounter");
        giveUpJumps = tag.getInt("jumpCounter");
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putBoolean("incapacitation", incapacitated);
        tag.putInt("incapTimer", ticksUntilDeath);
        tag.putInt("incapCounter", downsUntilDeath);
        tag.putInt("jumpCounter", giveUpJumps);
    }

    @Override
    public void writeSyncPacket(FriendlyByteBuf buf, ServerPlayer recipient) {
        buf.writeBoolean(this.getIsIncapacitated());
    }

    @Override
    public void applySyncPacket(FriendlyByteBuf buf) {
        this.setIsIncapacitated(buf.readBoolean());
    }
}
