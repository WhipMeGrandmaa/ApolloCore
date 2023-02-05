package me.whipmegrandma.apollocore.settings;

import me.whipmegrandma.apollocore.enums.CompMetadataTags;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.settings.YamlStaticConfig;

import java.util.ArrayList;
import java.util.List;

public class PlayerShopSettings extends YamlStaticConfig {

	public static List<CompMaterial> bannedItems;
	public static List<CompMetadataTags> bannedSpecialItems;
	public static Integer minimumPrice;
	public static Integer maximumPrice;

	@Override
	protected void onLoad() throws Exception {
		this.loadConfiguration("playershopsettings.yml");
	}

	private static void init() {
		bannedItems = isSet("Banned_Items") ? getMaterialList("Banned_Items") : new ArrayList<>();

		List<CompMetadataTags> specialItems = new ArrayList<>();

		for (String specialItem : getStringList("Banned_Special_Items")) {

			if (specialItem.equalsIgnoreCase("PERSONAL_PICKAXE"))
				specialItems.add(CompMetadataTags.PICKAXE);

			if (specialItem.equalsIgnoreCase("TOKENS_NOTE"))
				specialItems.add(CompMetadataTags.WITHDRAW);

			if (specialItem.equalsIgnoreCase("MINEBOMB")) {
				specialItems.add(CompMetadataTags.MINEBOMB);
			}

			bannedSpecialItems = !specialItems.isEmpty() ? specialItems : new ArrayList<>();

			minimumPrice = isSet("Minimum_Price") ? getInteger("Minimum_Price") : 0;
			maximumPrice = isSet("Maximum_Price") ? getInteger("Maximum_Price") : Integer.MAX_VALUE;
		}
	}
}
