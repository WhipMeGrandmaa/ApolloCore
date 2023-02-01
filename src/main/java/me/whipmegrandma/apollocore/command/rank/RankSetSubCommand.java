package me.whipmegrandma.apollocore.command.rank;

import me.whipmegrandma.apollocore.database.Database;
import me.whipmegrandma.apollocore.model.PlayerCache;
import me.whipmegrandma.apollocore.model.Rank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.List;

public class RankSetSubCommand extends SimpleSubCommand {

	protected RankSetSubCommand(SimpleCommandGroup parent) {
		super(parent, "set");

		this.setPermission("apollocore.command.rank.set");
		this.setMinArguments(2);
		this.setUsage("<player> <rank>");
	}

	@Override
	protected void onCommand() {

		String username = args[0];
		Rank rank = Rank.getByName(args[1]);

		super.checkBoolean(rank != null, args[1] + " is not a configured rank. To check all available use '/rank list'.");

		Player target = Bukkit.getPlayerExact(username);

		if (target != null) {

			PlayerCache cache = PlayerCache.from(target);
			cache.setRank(rank);

			Common.tell(target, "Your rank has been set to " + rank.getName() + ".");

			if (!target.equals(getPlayer()))
				super.tell("The rank of " + target.getName() + " has been set to " + rank.getName() + ".");

		} else {

			Database.getInstance().load(username, cache -> {

				if (cache == null) {
					super.tell(username + " has never joined the server before.");

					return;
				}

				String name = cache.getUsername();

				Database.getInstance().update(username, "Rank", rank.getName());
				super.tell("The rank of " + name + " has been set to " + rank.getName() + ".");

			});
		}
	}

	@Override
	protected List<String> tabComplete() {

		if (args.length == 1)
			return completeLastWordPlayerNames();

		if (args.length == 2)
			return completeLastWord(Rank.getRankNames());

		return NO_COMPLETE;
	}
}
