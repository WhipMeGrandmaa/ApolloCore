package me.whipmegrandma.apollocore.settings;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.settings.ConfigItems;
import org.mineacademy.fo.settings.YamlConfig;

import java.text.DecimalFormat;
import java.util.List;


public class PriceSettings extends YamlConfig {

	private static ConfigItems<PriceSettings> prices = ConfigItems.fromFile("", "pricesettings.yml", PriceSettings.class);
	@Getter
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

	public static double getPrice(Location location, Player player) {
		return getPrice(location.getBlock(), player);
	}

	public static double getPrice(Block block, Player player) {
		return getPrice(block.getType(), player);
	}

	public static double getPrice(Material material, Player player) {
		String type = material.toString();
		ItemStack hand = player.getInventory().getItemInMainHand();
		int level = hand.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS) ? hand.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) : 1;

		DecimalFormat decimal = new DecimalFormat("0.00");

		return prices.isItemLoaded(type) ? Double.parseDouble(decimal.format(prices.findItem(type).getPrice() * level)) : 0;
	}

	public static List<PriceSettings> getPrices() {
		return prices.getItems();
	}
}
