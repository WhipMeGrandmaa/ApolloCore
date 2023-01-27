package me.whipmegrandma.apollocore.settings;

import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.settings.YamlStaticConfig;

import java.util.Arrays;
import java.util.List;


public class TokensSettings extends YamlStaticConfig {

	public static CompMaterial material;
	public static String name;
	public static List<String> lore;
	public static Boolean glow;

	@Override
	protected void onLoad() throws Exception {
		this.loadConfiguration("tokensettings.yml");
	}

	private static void init() {
		setPathPrefix("Withdraw_Item");

		material = getMaterial("Material") != null ? getMaterial("Material") : CompMaterial.GOLD_NUGGET;
		name = getString("Name") != null ? getString("Name") : "&a&l%tokens% TOKENS";
		lore = getStringList("Lore") != null ? getStringList("Lore") : Arrays.asList("", "&7Right click to cash in.");
		glow = getBoolean("Glow");
	}
}
