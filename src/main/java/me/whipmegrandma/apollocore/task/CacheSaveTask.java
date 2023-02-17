package me.whipmegrandma.apollocore.task;

import me.whipmegrandma.apollocore.database.Database;
import me.whipmegrandma.apollocore.model.ApolloPlayer;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.Common;

public class CacheSaveTask extends BukkitRunnable {

	private Long millisOnStartSave;

	@Override
	public void run() {

		this.millisOnStartSave = System.currentTimeMillis();
		Common.broadcast("Saving all player data...");

		for (ApolloPlayer player : ApolloPlayer.getAllCached())
			Database.getInstance().save(player, non -> {
			});

		ApolloPlayer.removeUselessFromCache();

		Common.broadcast("Finished in " + (System.currentTimeMillis() - this.millisOnStartSave) + "ms.");
	}
}
