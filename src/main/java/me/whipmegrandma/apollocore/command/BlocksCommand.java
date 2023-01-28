package me.whipmegrandma.apollocore.command;

import me.whipmegrandma.apollocore.database.Database;
import me.whipmegrandma.apollocore.model.PlayerCache;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommand;

import java.util.List;

@AutoRegister
public final class BlocksCommand extends SimpleCommand {

	public BlocksCommand() {
		super("block|blocks");

		this.setPermission("apollocore.command.blocks");
	}

	@Override
	protected void onCommand() {

		if (args.length == 0) {
			checkConsole();

			int blocks = PlayerCache.from(getPlayer()).getBlocksBroken();
			super.tell("You've broken " + Common.plural(blocks, "block") + ".");

			return;
		}

		String username = args[0];

		Player target = Bukkit.getPlayerExact(username);

		if (target != null) {

			int blocks = PlayerCache.from(target).getBlocksBroken();

			if (target.equals(getPlayer())) {
				super.tell("You've broken " + Common.plural(blocks, "block") + ".");

				return;
			}

			super.tell(target.getName() + " has broken " + Common.plural(blocks, "block") + ".");

		} else {

			Database.getInstance().load(username, cache -> {

				if (cache == null) {
					super.tell(username + " has never joined the server before.");

					return;
				}

				String name = cache.getUsername();
				int blocks = cache.getBlocksBroken();

				super.tell(name + " has broken " + Common.plural(blocks, "block") + ".");

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