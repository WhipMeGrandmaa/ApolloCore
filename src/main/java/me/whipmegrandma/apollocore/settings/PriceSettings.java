package me.whipmegrandma.apollocore.settings;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.settings.ConfigItems;
import org.mineacademy.fo.settings.YamlConfig;

import java.util.List;


public class PriceSettings extends YamlConfig {

	private static ConfigItems<PriceSettings> prices = ConfigItems.fromFile("", "pricesettings.yml", PriceSettings.class);

	private double price;

	private PriceSettings(String name) {
		this.loadConfiguration("pricesettings.yml");

		if (CompMaterial.fromString(name) == null) {
			prices.removeItemByName(name);

			return;
		}

		this.price = isSet(name) ? this.getDouble(name) : 0;
	}

	public static void loadPrices() {
		prices.loadItems();
	}

	public static double getPrice(String name) {
		return getPrice(CompMaterial.fromString(name));
	}

	public static double getPrice(CompMaterial material) {
		return getPrice(material.getMaterial());
	}

	public static double getPrice(Block block) {
		return getPrice(block.getType().toString());

	}

	public static double getPrice(Location location) {
		return getPrice(location.getBlock().getType());
	}

	public static double getPrice(Material material) {
		String type = material.toString();

		return prices.isItemLoaded(type) ? prices.findItem(type).price : 0;
	}

	public static List<PriceSettings> getPrices() {
		return prices.getItems();
	}
}
