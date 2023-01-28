package me.whipmegrandma.apollocore.task;

import me.whipmegrandma.apollocore.listener.PlayerListener;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.model.Tuple;
import org.mineacademy.fo.remain.Remain;

import java.util.HashMap;
import java.util.UUID;

public class BlocksBrokenTask extends BukkitRunnable {

	@Override
	public void run() {
		HashMap<UUID, Tuple<Integer, Double>> blocksMap = PlayerListener.getBlocksBroken();

		for (Player player : Remain.getOnlinePlayers())
			if (blocksMap.containsKey(player.getUniqueId())) {
				UUID uuid = player.getUniqueId();

				Tuple<Integer, Double> tuple = blocksMap.get(uuid);
				int broken = tuple.getKey();
				double moneyMade = tuple.getValue();
				
				Common.tell(player, "You have broken " + Common.plural(broken, "block") + " for $" + moneyMade + ".");

				PlayerListener.getBlocksBroken().clear();
			}
	}
}
