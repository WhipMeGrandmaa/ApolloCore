package me.whipmegrandma.apollocore.settings;

import org.mineacademy.fo.settings.YamlStaticConfig;


public class BlocksBrokenSettings extends YamlStaticConfig {

	public static Integer time;

	@Override
	protected void onLoad() throws Exception {
		this.loadConfiguration("blockbrokensettings.yml");
	}

	private static void init() {
		time = isSet("Seconds_Between_Blocks_Broken_Message") ? getInteger("Seconds_Between_Blocks_Broken_Message") : 60;
	}
}
