package com.cartoonishvillain.incapacitated;

import com.cartoonishvillain.incapacitated.config.IncapacitationConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Incapacitated implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LogManager.getLogger("incapacitated");
	public static IncapacitationConfig config;
	public static ArrayList<ResourceLocation> ReviveFoods;
	public static ArrayList<ResourceLocation> HealingFoods;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		AutoConfig.register(IncapacitationConfig.class, GsonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(IncapacitationConfig.class).getConfig();

		ReviveFoods = getFoodForReviving();
		HealingFoods = getFoodForHealing();

	}

	private ArrayList<ResourceLocation> getFoodForReviving() {
		final String FoodList = config.config.REVIVEFOODS;
		String[] reviveFoods = FoodList.split(",");
		ArrayList<ResourceLocation> reviveFoodList = new ArrayList<>();
		try {
			for(String string : reviveFoods){
				ResourceLocation food = new ResourceLocation(string);
				reviveFoodList.add(food);
			}
		}catch(ResourceLocationException e){
			Incapacitated.LOGGER.error("Incapacitation: Revive foods not parsed. Non [a-z0-9_.-] character in config! Using default...");
			return new ArrayList<>(List.of(new ResourceLocation("enchanted_golden_apple")));
		}
		return reviveFoodList;
	}

	private ArrayList<ResourceLocation> getFoodForHealing() {
		final String FoodList = config.config.HEALINGFOODS;
		String[] healFoods = FoodList.split(",");
		ArrayList<ResourceLocation> healFoodList = new ArrayList<>();
		try {
			for(String string : healFoods){
				ResourceLocation food = new ResourceLocation(string);
				healFoodList.add(food);
			}
		}catch(ResourceLocationException e){
			Incapacitated.LOGGER.error("Incapacitation: Healing foods not parsed. Non [a-z0-9_.-] character in config! Using default...");
			return new ArrayList<>(List.of(new ResourceLocation("golden_apple")));
		}
		return healFoodList;
	}
}
