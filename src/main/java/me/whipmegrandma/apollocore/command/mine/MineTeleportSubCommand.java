package me.whipmegrandma.apollocore.command.mine;

import me.whipmegrandma.apollocore.database.Database;
import me.whipmegrandma.apollocore.model.ApolloPlayer;
import me.whipmegrandma.apollocore.model.Mine;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.remain.Remain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MineTeleportSubCommand extends SimpleSubCommand {

	protected MineTeleportSubCommand(SimpleCommandGroup parent) {
		super(parent, "teleport|tp");

		this.setPermission("apollocore.command.mine.teleport");
		setUsage("[player]");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		if (args.length == 0) {

			Mine mine = ApolloPlayer.from(getPlayer()).getMine();
			checkBoolean(mine != null, "You don't own a mine. Use '/mine create' to make one.");

			mine.teleportToHome(getPlayer());
			tell("You teleported to your mine.");

			return;
		}

		String username = args[0];

		ApolloPlayer target = ApolloPlayer.from(username);

		if (target != null) {

			Mine mine = target.getMine();
			checkBoolean(mine != null, target.getUsername() + " doesn't own a mine.");

			if (!mine.isPlayerAllowed(getPlayer()) && !getPlayer().isOp())
				checkBoolean(mine.getCanTeleport(), target.getUsername() + " has mine teleportation toggled off to outsiders.");

			mine.teleportToHome(getPlayer());

			if (target.getUuid().equals(getPlayer().getUniqueId())) {
				tell("You teleported to your mine.");

				return;
			}

			tell("You teleported to the mine of " + target.getUsername() + ".");

			Player targetPlayer = Remain.getPlayerByUUID(target.getUuid());

			if (targetPlayer != null)
				Common.tell(targetPlayer, getPlayer().getName() + " has teleported to your mine.");

		} else {

			Database.getInstance().load(username, cache -> {

				if (cache == null) {
					super.tell(username + " has never joined the server before.");

					return;
				}

				String name = cache.getUsername();

				tell(name + " doesn't own a mine.");

			});
		}
	}

	@Override
	protected List<String> tabComplete() {

		if (args.length == 1) {
			List<ApolloPlayer> cached = ApolloPlayer.getAllCached();
			List<String> names = new ArrayList<>();

			for (Iterator<ApolloPlayer> iterator = cached.listIterator(); iterator.hasNext(); ) {
				ApolloPlayer player = iterator.next();
				Mine mine = player.getMine();

				if (mine == null || (mine != null && !mine.isPlayerAllowed(getPlayer()) && !mine.getCanTeleport() && !player.getUuid().equals(getPlayer().getUniqueId()) && !getPlayer().isOp()))
					iterator.remove();
				else
					names.add(player.getUsername());
			}

			return completeLastWord(names);
		}
		return NO_COMPLETE;
	}
}
