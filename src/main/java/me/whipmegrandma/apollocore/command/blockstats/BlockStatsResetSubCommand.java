package me.whipmegrandma.apollocore.command.blockstats;

import me.whipmegrandma.apollocore.database.Database;
import me.whipmegrandma.apollocore.model.PlayerCache;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.List;

public class BlockstatsResetSubCommand extends SimpleSubCommand {

	protected BlockstatsResetSubCommand(SimpleCommandGroup parent) {
		super(parent, "reset");

		this.setPermission("apollocore.command.blockstats.reset");
		this.setUsage("[player]");
	}

	@Override
	protected void onCommand() {

		if (args.length == 0) {
			checkConsole();

			PlayerCache.from(getPlayer()).setBlocksBroken(0);
			super.tell("The amount of blocks you've broken has been reset.");

			return;
		}

		String username = args[0];

		Player target = Bukkit.getPlayerExact(username);

		if (target != null) {

			PlayerCache cache = PlayerCache.from(target);
			cache.setBlocksBroken(0);

			Common.tell(target, "The amount of blocks you've broken has been reset.");

			if (!target.equals(getPlayer()))
				super.tell("You've reset the amount of blocks " + target.getName() + " has broken.");

		} else {

			Database.getInstance().load(username, cache -> {

				if (cache == null) {
					super.tell(username + " has never joined the server before.");

					return;
				}

				String name = cache.getUsername();

				Database.getInstance().update(username, "Blocks_Broken", 0);
				super.tell("You've reset the amount of blocks " + name + " has broken.");

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
