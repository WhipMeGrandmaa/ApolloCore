package me.whipmegrandma.apollocore.model.workloadtypes;

import lombok.AllArgsConstructor;
import me.whipmegrandma.apollocore.interfaces.Workload;
import me.whipmegrandma.apollocore.model.ApolloPlayer;
import me.whipmegrandma.apollocore.model.Mine;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;

@AllArgsConstructor
public class MineCreateWorkload implements Workload {

	private Player player;
	private ApolloPlayer cache;

	@Override
	public void compute() {
		Mine mine = Mine.create();
		mine.setOwner(cache);
		cache.setMine(mine);
		mine.resetMine();

		Common.tell(player, "You have successfully created a mine.");

		mine.teleportToHome(player);
	}
}
