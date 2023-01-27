package me.whipmegrandma.apollocore.command.tokens;

import me.whipmegrandma.apollocore.database.Database;
import me.whipmegrandma.apollocore.model.PlayerCache;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.List;

public class TokensBalanceSubCommand extends SimpleSubCommand {

	protected TokensBalanceSubCommand(SimpleCommandGroup parent) {
		super(parent, "balance|bal");

		this.setPermission("apollocore.command.balance");
		this.setUsage("[player]");
	}

	@Override
	protected void onCommand() {

		if (args.length == 0) {
			checkConsole();

			int tokens = PlayerCache.from(getPlayer()).getTokens();
			super.tell("Tokens: " + tokens);

			return;
		}

		String username = args[0];

		Player target = Bukkit.getPlayerExact(username);

		if (target != null) {

			int tokens = PlayerCache.from(target).getTokens();

			if (target.equals(getPlayer())) {
				super.tell("Tokens: " + tokens);

				return;
			}

			Common.tell(target, "Tokens of " + target.getName() + ": " + tokens);

		} else {

			Database.getInstance().load(username, cache -> {

				if (cache == null) {
					super.tell(username + " has never joined the server before.");

					return;
				}

				String name = cache.getUsername();
				int tokens = cache.getTokens();

				super.tell("Tokens of " + name + ": " + tokens);

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
