package me.whipmegrandma.apollocore.task;

import me.whipmegrandma.apollocore.model.ApolloPlayer;
import me.whipmegrandma.apollocore.model.Mine;
import me.whipmegrandma.apollocore.settings.MineSettings;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.Common;

public class MineResetTask extends BukkitRunnable {

	private Long secondsLast;

	@Override
	public void run() {
		Long secondsNow = System.currentTimeMillis() / 1000;
		
		if (this.secondsLast == null || secondsNow - secondsLast >= MineSettings.getInstance().getResetMineSeconds()) {
			Common.broadcast("Resetting all mines with at least 1 member online.");
			this.secondsLast = System.currentTimeMillis() / 1000;

			for (ApolloPlayer player : ApolloPlayer.getAllCached()) {
				Mine mine = player.getMine();

				if (mine != null && mine.getAmountAllowedPlayersOnline() + 1 > 0)
					mine.resetMine();
			}
		} else {
			Long seconds = secondsNow - this.secondsLast;

			Common.broadcast("Resetting all mines with at least 1 member online in " + Common.plural(MineSettings.getInstance().getResetMineSeconds() - seconds, "second") + ".");
		}
	}
}
