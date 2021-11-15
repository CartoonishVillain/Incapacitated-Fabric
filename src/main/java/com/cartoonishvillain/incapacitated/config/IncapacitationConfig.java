package com.cartoonishvillain.incapacitated.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "incapacitation")
public class IncapacitationConfig implements ConfigData {

    @ConfigEntry.Gui.CollapsibleObject
    public IncapacitationOptions config = new IncapacitationOptions();

    public static class IncapacitationOptions{
        @Comment("A list of comma separated item IDs for foods player can eat to revive themselves. IE: minecraft:enchanted_golden_apple,minecraft:apple")
        public String REVIVEFOODS = "minecraft:enchanted_golden_apple";
        @Comment("A list of comma separated item IDs for foods player can eat to reset their down counters. IE: minecraft:golden_apple,minecraft:golden_carrot")
        public String HEALINGFOODS = "minecraft:golden_apple";
        @Comment("How many ticks a player can be downed without dying")
        public int DOWNTICKS = 2000;
        @Comment("How long it takes to revive a downed player manually")
        public int REVIVETICKS = 150;
        @Comment("How many times a player can go down without a healing or revive item, without instantly dying the next time they are supposed to go down.")
        public int DOWNCOUNT = 3;
        @Comment("Do players glow while downed to be easier to find?")
        public boolean GLOWING = true;
        @Comment("Are downed players invincible?")
        public boolean INVINCIBLEDOWN = false;
        @Comment("Do some damage types like Lava down players, or instantly kill?")
        public boolean SOMEINSTANTKILLS = true;
        @Comment("Are incapacitation messsages globally sent")
        public boolean GLOBALINCAPMESSAGES = true;
    }
}
