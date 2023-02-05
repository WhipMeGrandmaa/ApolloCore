package me.whipmegrandma.apollocore.command.blockstats;

import me.whipmegrandma.apollocore.database.Database;
import me.whipmegrandma.apollocore.model.ApolloPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.text.NumberFormat;
import java.util.List;

public class BlockstatsSetSubCommand extends SimpleSubCommand {

	protected BlockstatsSetSubCommand(SimpleCommandGroup parent) {
		super(parent, "set");

		this.setPermission("apollocore.command.blockstats.set");
		this.setMinArguments(2);
		this.setUsage("<player> <amount>");
	}

	@Override
	protected void onCommand() {

		String username = args[0];
		Integer blocks = super.findNumber(1, "The amount must be a number.");

		super.checkBoolean(blocks > 0, "The amount to set must be a positive number.");

		Player target = Bukkit.getPlayerExact(username);

		if (target != null) {

			ApolloPlayer cache = ApolloPlayer.from(target);
			cache.setBlocksBroken(blocks);

			Common.tell(target, "The amount of blocks you've broken has been set to " + NumberFormat.getInstance().format(blocks) + ".");

			if (!target.equals(getPlayer()))
				super.tell("The amount of blocks " + target.getName() + " has broken has been set to " + NumberFormat.getInstance().format(blocks) + ".");

		} else {

			Database.getInstance().load(username, cache -> {

				if (cache == null) {
					super.tell(username + " has never joined the server before.");

					return;
				}

				String name = cache.getUsername();

				Database.getInstance().update(username, "Blocks_Broken", blocks);
				super.tell("The amount of blocks " + name + " has broken has been set to " + NumberFormat.getInstance().format(blocks) + ".");

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
