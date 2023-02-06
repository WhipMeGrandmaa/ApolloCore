package me.whipmegrandma.apollocore.settings;

import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.settings.YamlStaticConfig;

import java.util.Arrays;
import java.util.List;


public class PersonalPickaxeSettings extends YamlStaticConfig {

	public static CompMaterial material;
	public static String name;
	public static List<String> lore;
	public static Boolean glow;
	public static Integer hotbarSlot;

	@Override
	protected void onLoad() throws Exception {
		this.loadConfiguration("personalpickaxesettings.yml");
	}

	private static void init() {
		material = isSet("Material") ? getMaterial("Material") : CompMaterial.DIAMOND_PICKAXE;
		name = isSet("Name") ? getString("Name") : "&d&l%player_name% &b&lPickaxe";
		lore = isSet("Lore") ? getStringList("Lore") : Arrays.asList("", "&7Right click to open", "&7the pickaxe menu.");
		glow = isSet("Glow_When_Not_Enchanted") ? getBoolean("Glow_When_Not_Enchanted") : false;
		hotbarSlot = isSet("Hotbar_Slot") ? getInteger("Hotbar_Slot") - 1 : 0;

		if (hotbarSlot > 8)
			hotbarSlot = 8;

		else if (hotbarSlot < 0)
			hotbarSlot = 0;
	}
}
