package me.whipmegrandma.apollocore.command.tokens;

import me.whipmegrandma.apollocore.database.Database;
import me.whipmegrandma.apollocore.model.ApolloPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.text.NumberFormat;
import java.util.List;

public class TokensBalanceSubCommand extends SimpleSubCommand {

	protected TokensBalanceSubCommand(SimpleCommandGroup parent) {
		super(parent, "balance|bal");

		this.setPermission("apollocore.command.tokens.balance");
		this.setUsage("[player]");
	}

	@Override
	protected void onCommand() {

		if (args.length == 0) {
			checkConsole();

			int tokens = ApolloPlayer.from(getPlayer()).getTokens();
			super.tell("Tokens: " + NumberFormat.getInstance().format(tokens));

			return;
		}

		String username = args[0];

		Player target = Bukkit.getPlayerExact(username);

		if (target != null) {

			int tokens = ApolloPlayer.from(target).getTokens();

			if (target.equals(getPlayer())) {
				super.tell("Tokens: " + NumberFormat.getInstance().format(tokens));

				return;
			}

			super.tell("Tokens of " + target.getName() + ": " + NumberFormat.getInstance().format(tokens));

		} else {

			Database.getInstance().load(username, cache -> {

				if (cache == null) {
					super.tell(username + " has never joined the server before.");

					return;
				}

				String name = cache.getUsername();
				int tokens = cache.getTokens();

				super.tell("Tokens of " + name + ": " + NumberFormat.getInstance().format(tokens));

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
