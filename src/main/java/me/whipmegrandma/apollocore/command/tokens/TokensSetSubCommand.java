package me.whipmegrandma.apollocore.command.tokens;

import me.whipmegrandma.apollocore.database.Database;
import me.whipmegrandma.apollocore.model.ApolloPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.text.NumberFormat;
import java.util.List;

public class TokensSetSubCommand extends SimpleSubCommand {

	protected TokensSetSubCommand(SimpleCommandGroup parent) {
		super(parent, "set");

		this.setPermission("apollocore.command.tokens.set");
		this.setMinArguments(2);
		this.setUsage("<player> <amount>");
	}

	@Override
	protected void onCommand() {

		String username = args[0];
		Integer amount = super.findNumber(1, "The amount must be a number.");

		super.checkBoolean(amount > 0, "The amount to set must be a positive number.");

		Player target = Bukkit.getPlayerExact(username);

		if (target != null) {

			ApolloPlayer cache = ApolloPlayer.from(target);
			cache.setTokens(amount);

			Common.tell(target, "Your tokens has been set to " + NumberFormat.getInstance().format(amount) + ".");

			if (!target.equals(getPlayer()))
				super.tell("The tokens of " + target.getName() + " has been set to " + NumberFormat.getInstance().format(amount) + ".");

		} else {

			Database.getInstance().load(username, cache -> {

				if (cache == null) {
					super.tell(username + " has never joined the server before.");

					return;
				}

				String name = cache.getUsername();

				Database.getInstance().update(username, "Tokens", amount);
				super.tell("The tokens of " + name + " has been set to " + NumberFormat.getInstance().format(amount) + ".");

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
