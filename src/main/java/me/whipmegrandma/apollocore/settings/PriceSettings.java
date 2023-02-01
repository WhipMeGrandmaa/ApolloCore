package me.whipmegrandma.apollocore.settings;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.settings.ConfigItems;
import org.mineacademy.fo.settings.YamlConfig;

import java.text.DecimalFormat;
import java.util.Collection;
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
		ItemStack hand = player.getInventory().getItemInMainHand();
		Collection<ItemStack> drops = !block.getDrops(hand).isEmpty() ? block.getDrops(hand) : block.getDrops();
		double price = 0;

		for (ItemStack drop : drops)
			price += getPrice(drop);

		return price;
	}

	public static double getPrice(ItemStack item) {
		int amount = item.getAmount();
		String type = item.getType().toString();

		DecimalFormat decimal = new DecimalFormat("0.00");
		
		return prices.isItemLoaded(type) ? Double.parseDouble(decimal.format(prices.findItem(type).getPrice() * amount)) : 0;
	}

	public static List<PriceSettings> getPrices() {
		return prices.getItems();
	}
}
