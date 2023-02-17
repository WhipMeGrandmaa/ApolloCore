package me.whipmegrandma.apollocore.settings;

import me.whipmegrandma.apollocore.model.EnchantByLevelSettings;
import org.mineacademy.fo.settings.YamlStaticConfig;

import java.util.HashMap;


public class EnchantSettings extends YamlStaticConfig {

	public static HashMap<Integer, EnchantByLevelSettings> jackHammerSettings;
	public static HashMap<Integer, EnchantByLevelSettings> tokenGreedSettings;
	public static HashMap<Integer, EnchantByLevelSettings> wrathSettings;
	public static HashMap<Integer, EnchantByLevelSettings> spaceDropletsSettings;
	public static HashMap<Integer, EnchantByLevelSettings> starMultiplierSettings;
	public static HashMap<Integer, EnchantByLevelSettings> blackHoleSettings;

	@Override
	protected void onLoad() throws Exception {
		this.loadConfiguration("enchantsettings.yml");
	}

	private static void init() {
		jackHammerSettings = getMap("Jack_Hammer", Integer.class, EnchantByLevelSettings.class);
		tokenGreedSettings = getMap("Token_Greed", Integer.class, EnchantByLevelSettings.class);
		wrathSettings = getMap("Wrath", Integer.class, EnchantByLevelSettings.class);
		spaceDropletsSettings = getMap("Space_Droplets", Integer.class, EnchantByLevelSettings.class);
		starMultiplierSettings = getMap("Star_Multiplier", Integer.class, EnchantByLevelSettings.class);
		blackHoleSettings = getMap("Black_Hole", Integer.class, EnchantByLevelSettings.class);
	}
}
