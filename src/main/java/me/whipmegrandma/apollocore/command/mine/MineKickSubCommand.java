package me.whipmegrandma.apollocore.command.mine;

import me.whipmegrandma.apollocore.database.Database;
import me.whipmegrandma.apollocore.model.ApolloPlayer;
import me.whipmegrandma.apollocore.model.Mine;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.List;
import java.util.UUID;

public class MineKickSubCommand extends SimpleSubCommand {

	protected MineKickSubCommand(SimpleCommandGroup parent) {
		super(parent, "kick");

		this.setPermission("apollocore.command.mine.kick");
		this.setMinArguments(1);
		this.setUsage("<player>");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		String username = args[0];
		ApolloPlayer mineOwner = ApolloPlayer.from(getPlayer());

		Mine mine = mineOwner.getMine();
		checkBoolean(mine != null, "You don't own a mine. Use '/mine create' to make one.");

		Player target = Bukkit.getPlayerExact(username);

		if (target != null) {
			checkBoolean(!target.getUniqueId().equals(getPlayer().getUniqueId()), "You cannot kick yourself from your own mine. To delete your mine use '/mine delete'.");
			checkBoolean(mine.isPlayerAllowed(target), target.getName() + " isn't a member of your mine.");

			mine.removeAllowedPlayer(target);

			tell("You kicked " + target.getName() + " from your mine.");
			Common.tell(target, "You have been kicked from the mine of " + mineOwner.getUsername() + ".");

		} else {

			Database.getInstance().load(username, cache -> {

				if (cache == null) {
					super.tell(username + " has never joined the server before.");

					return;
				}

				String name = cache.getUsername();
				UUID uuid = cache.getUuid();

				checkBoolean(mine.isPlayerAllowed(uuid), name + " isn't a member of your mine.");

				mine.removeAllowedPlayer(uuid);

				tell("You kicked " + name + " from your mine.");
			});
		}
	}

	@Override
	protected List<String> tabComplete() {
		Mine mine = ApolloPlayer.from(getPlayer()).getMine();

		return mine != null ? mine.getAllowedPlayerNames() : null;
	}
}
