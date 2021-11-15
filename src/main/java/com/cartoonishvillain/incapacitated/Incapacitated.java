package com.cartoonishvillain.incapacitated;

import com.cartoonishvillain.incapacitated.components.IncapacitationWorker;
import com.cartoonishvillain.incapacitated.components.PlayerComponent;
import com.cartoonishvillain.incapacitated.config.IncapacitationConfig;
import io.netty.buffer.Unpooled;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.damagesource.DamageSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static com.cartoonishvillain.incapacitated.components.ComponentStarter.PLAYERCOMPONENTINSTANCE;

public class Incapacitated implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LogManager.getLogger("incapacitated");
	public static IncapacitationConfig config;
	public static ArrayList<String> ReviveFoods;
	public static ArrayList<String> HealingFoods;
	public static ArrayList<String> instantKillDamageSourcesMessageID;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		AutoConfig.register(IncapacitationConfig.class, JanksonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(IncapacitationConfig.class).getConfig();

		instantKillDamageSourcesMessageID = new ArrayList<>(List.of("bleedout", DamageSource.OUT_OF_WORLD.msgId, DamageSource.LAVA.msgId, DamageSource.WITHER.msgId));

		ReviveFoods = getFoodForReviving();
		HealingFoods = getFoodForHealing();

		ServerPlayConnectionEvents.JOIN.register(JoinListener.getInstance());
	}

	private ArrayList<String> getFoodForReviving() {
		final String FoodList = config.config.REVIVEFOODS;
		String[] reviveFoods = FoodList.split(",");
		ArrayList<String> reviveFoodList = new ArrayList<>();
		try {
			for(String string : reviveFoods){
				String food = new ResourceLocation(string).getPath();
				reviveFoodList.add(food);
			}
		}catch(ResourceLocationException e){
			Incapacitated.LOGGER.error("Incapacitation: Revive foods not parsed. Non [a-z0-9_.-] character in config! Using default...");
			return new ArrayList<>(List.of("enchanted_golden_apple"));
		}
		return reviveFoodList;
	}

	private ArrayList<String> getFoodForHealing() {
		final String FoodList = config.config.HEALINGFOODS;
		String[] healFoods = FoodList.split(",");
		ArrayList<String> healFoodList = new ArrayList<>();
		try {
			for(String string : healFoods){
				String food = new ResourceLocation(string).getPath();
				healFoodList.add(food);
			}
		}catch(ResourceLocationException e){
			Incapacitated.LOGGER.error("Incapacitation: Healing foods not parsed. Non [a-z0-9_.-] character in config! Using default...");
			return new ArrayList<>(List.of("golden_apple"));
		}
		return healFoodList;
	}

	public static class JoinListener implements ServerPlayConnectionEvents.Join{
		private static final JoinListener INSTANCE = new JoinListener();
		@Override
		public void onPlayReady(ServerGamePacketListenerImpl handler, PacketSender sender, MinecraftServer server) {
			PlayerComponent playerComponent = PLAYERCOMPONENTINSTANCE.get(handler.player);
			playerComponent.sync();
		}
		public static JoinListener getInstance() {return INSTANCE;}

	}
}
