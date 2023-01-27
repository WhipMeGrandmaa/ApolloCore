package me.whipmegrandma.apollocore.settings;

import org.mineacademy.fo.settings.YamlStaticConfig;


public class ChatSettings extends YamlStaticConfig {

	public static String chatFormat;

	@Override
	protected void onLoad() throws Exception {
		this.loadConfiguration("chatsettings.yml");
	}

	private static void init() {
		chatFormat = getString("Chat_Format");
	}
}
