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

public class TokensAddSubCommand extends SimpleSubCommand {

	protected TokensAddSubCommand(SimpleCommandGroup parent) {
		super(parent, "add");

		this.setPermission("apollocore.command.tokens.add");
		this.setMinArguments(2);
		this.setUsage("<player> <amount>");
	}

	@Override
	protected void onCommand() {

		String username = args[0];
		Integer amount = super.findNumber(1, "The amount must be a number.");

		super.checkBoolean(amount > 0, "The amount to add must be a positive number.");

		Player target = Bukkit.getPlayerExact(username);

		if (target != null) {

			ApolloPlayer cache = ApolloPlayer.from(target);
			cache.setTokens(amount + cache.getTokens());

			Common.tell(target, NumberFormat.getInstance().format(amount) + " tokens have been given to " + target.getName() + ".");

			if (!target.equals(getPlayer()))
				super.tell(NumberFormat.getInstance().format(amount) + " tokens have been given to you.");

		} else {

			Database.getInstance().load(username, cache -> {

				if (cache == null) {
					super.tell(username + " has never joined the server before.");

					return;
				}

				String name = cache.getUsername();
				int tokens = cache.getTokens();

				Database.getInstance().update(username, "Tokens", amount + tokens);
				super.tell(NumberFormat.getInstance().format(amount) + " tokens have been given to " + name + ".");

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
