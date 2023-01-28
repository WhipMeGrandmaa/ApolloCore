package me.whipmegrandma.apollocore.util;

import me.whipmegrandma.apollocore.hook.VaultHook;
import me.whipmegrandma.apollocore.listener.PlayerListener;
import me.whipmegrandma.apollocore.settings.PriceSettings;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.mineacademy.fo.model.Tuple;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class VaultEcoUtil {

	private static final Economy economy = VaultHook.getEconomy();

	public static void sell(Player player, Set<Location> blocks) {
		HashMap<UUID, Tuple<Integer, Double>> map = PlayerListener.getBlocksBroken();
		UUID uuid = player.getUniqueId();
		double moneyMade = 0;
		int broken = 0;

		for (Location location : blocks) {
			moneyMade += PriceSettings.getPrice(location.getBlock());
			broken++;
		}

		Tuple<Integer, Double> tuple = map.containsKey(uuid) ? map.get(uuid) : new Tuple<>(0, 0D);
		int brokenOld = tuple.getKey();
		double moneyMadeOld = tuple.getValue();

		economy.depositPlayer(player, moneyMade);

		if (moneyMade != 0)
			PlayerListener.getBlocksBroken().put(uuid, new Tuple<>(brokenOld + broken, moneyMadeOld + moneyMade));
	}

	public static void sell(Player player, Block block) {
		double moneyMade = PriceSettings.getPrice(block);

		economy.depositPlayer(player, moneyMade);

		HashMap<UUID, Tuple<Integer, Double>> map = PlayerListener.getBlocksBroken();

		UUID uuid = player.getUniqueId();

		Tuple<Integer, Double> tuple = map.containsKey(uuid) ? map.get(uuid) : new Tuple<>(0, 0D);
		int brokenOld = tuple.getKey();
		double moneyMadeOld = tuple.getValue();

		if (moneyMadeOld != 0)
			PlayerListener.getBlocksBroken().put(uuid, new Tuple<>(brokenOld + 1, moneyMadeOld + moneyMade));
	}
}