package me.whipmegrandma.apollocore.command.rank;

import me.whipmegrandma.apollocore.database.Database;
import me.whipmegrandma.apollocore.model.ApolloPlayer;
import me.whipmegrandma.apollocore.model.Rank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.List;

public class RankResetSubCommand extends SimpleSubCommand {

	protected RankResetSubCommand(SimpleCommandGroup parent) {
		super(parent, "reset");

		this.setPermission("apollocore.command.rank.reset");
		this.setUsage("[player]");
	}

	@Override
	protected void onCommand() {

		if (args.length == 0) {
			checkConsole();

			ApolloPlayer.from(getPlayer()).setRank(Rank.getFirstRank());
			super.tell("Your rank has been reset to " + Rank.getFirstRank().getName() + ".");

			return;
		}

		String username = args[0];

		Player target = Bukkit.getPlayerExact(username);

		if (target != null) {

			ApolloPlayer cache = ApolloPlayer.from(target);
			cache.setRank(Rank.getFirstRank());

			Common.tell(target, "Your rank has been reset to " + Rank.getFirstRank().getName() + ".");

			if (!target.equals(getPlayer()))
				super.tell("The rank of " + target.getName() + " has been reset to " + Rank.getFirstRank().getName() + ".");

		} else {

			Database.getInstance().load(username, cache -> {

				if (cache == null) {
					super.tell(username + " has never joined the server before.");

					return;
				}

				String name = cache.getUsername();

				Database.getInstance().update(username, "Rank", Rank.getFirstRank().getName());
				super.tell("The rank of " + name + " has been reset to " + Rank.getFirstRank().getName() + ".");

			});
		}
	}

	@Override
	protected List<String> tabComplete() {

		if (args.length == 1)
			return completeLastWordPlayerNames();

		return NO_COMPLETE;
	}
}
