package me.whipmegrandma.apollocore.task;

import me.whipmegrandma.apollocore.model.ApolloPlayer;
import me.whipmegrandma.apollocore.model.Mine;
import me.whipmegrandma.apollocore.settings.MineSettings;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.Common;

public class MineResetTask extends BukkitRunnable {

	private Long millis;

	@Override
	public void run() {

		if (this.millis == null || (System.currentTimeMillis() - this.millis) / 1000 >= MineSettings.getInstance().getResetMineSeconds()) {
			Common.broadcast("Resetting all mines with at least 1 member online.");
			this.millis = System.currentTimeMillis();

			for (ApolloPlayer player : ApolloPlayer.getAllCached()) {
				Mine mine = player.getMine();

				if (mine != null && mine.getAmountAllowedPlayersOnline() + 1 > 0)
					mine.resetMine();
			}
		} else
			Common.broadcast("Resetting all mines with at least 1 member online in " + Common.plural(MineSettings.getInstance().getResetMineSeconds() - MineSettings.getInstance().getResetMineReminderSeconds(), "second") + ".");
	}
}
