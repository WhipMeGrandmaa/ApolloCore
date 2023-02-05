package me.whipmegrandma.apollocore.command.playershop;

import me.whipmegrandma.apollocore.database.Database;
import me.whipmegrandma.apollocore.model.ApolloPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayershopResetSubCommand extends SimpleSubCommand {

	protected PlayershopResetSubCommand(SimpleCommandGroup parent) {
		super(parent, "reset");

		this.setPermission("apollocore.command.playershop.reset");
		this.setUsage("[player]");
	}

	@Override
	protected void onCommand() {

		if (args.length == 0) {
			checkConsole();

			ApolloPlayer.from(getPlayer()).setShopItems(new ArrayList<>());
			super.tell("Your player shop has been reset.");

			return;
		}

		String username = args[0];

		Player target = Bukkit.getPlayerExact(username);

		if (target != null) {

			ApolloPlayer cache = ApolloPlayer.from(target);
			cache.setShopItems(new ArrayList<>());

			Common.tell(target, "Your player shop has been reset.");

			if (!target.equals(getPlayer()))
				super.tell("You've reset the player shop of " + target.getName() + ".");

		} else {

			Database.getInstance().load(username, cache -> {

				if (cache == null) {
					super.tell(username + " has never joined the server before.");

					return;
				}

				String name = cache.getUsername();
				UUID uuid = cache.getUuid();

				ApolloPlayer player = ApolloPlayer.from(name);
				player.setShopItems(new ArrayList<>());

				Database.getInstance().save(player, non -> {
				});
				super.tell("You've reset the player shop of " + name + ".");

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