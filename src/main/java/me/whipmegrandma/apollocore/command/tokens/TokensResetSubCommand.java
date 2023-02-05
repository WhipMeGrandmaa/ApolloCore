package me.whipmegrandma.apollocore.command.tokens;

import me.whipmegrandma.apollocore.database.Database;
import me.whipmegrandma.apollocore.model.ApolloPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.List;

public class TokensResetSubCommand extends SimpleSubCommand {

	protected TokensResetSubCommand(SimpleCommandGroup parent) {
		super(parent, "reset");

		this.setPermission("apollocore.command.tokens.reset");
		this.setUsage("[player]");
	}

	@Override
	protected void onCommand() {

		if (args.length == 0) {
			checkConsole();

			ApolloPlayer.from(getPlayer()).setTokens(0);
			super.tell("Your tokens has been reset.");

			return;
		}

		String username = args[0];

		Player target = Bukkit.getPlayerExact(username);

		if (target != null) {

			ApolloPlayer cache = ApolloPlayer.from(target);
			cache.setTokens(0);

			Common.tell(target, "Your tokens has been reset.");

			if (!target.equals(getPlayer()))
				super.tell("The tokens of " + target.getName() + " has been reset.");

		} else {

			Database.getInstance().load(username, cache -> {

				if (cache == null) {
					super.tell(username + " has never joined the server before.");

					return;
				}

				String name = cache.getUsername();

				Database.getInstance().update(username, "Tokens", 0);
				super.tell("The tokens of " + name + " has been reset.");

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
