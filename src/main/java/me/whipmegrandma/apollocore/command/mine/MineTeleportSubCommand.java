package me.whipmegrandma.apollocore.command.mine;

import me.whipmegrandma.apollocore.model.ApolloPlayer;
import me.whipmegrandma.apollocore.model.Mine;
import org.bukkit.entity.Player;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

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

		Player player = getPlayer();
		ApolloPlayer cache = ApolloPlayer.from(args.length == 0 ? player.getName() : args[0]);

		if (args.length > 0)
			checkBoolean(cache != null, args[0] + " has never joined the server before.");

		Mine mine = cache.getMine();

		checkBoolean(mine != null, args.length == 0 ? "You don't own a mine. Use '/mine create' to make one." : cache.getUsername() + " doesn't own a mine.");

		if (!mine.equals(ApolloPlayer.from(player).getMine()) && !mine.getCanTeleport() && (mine.getAllowedPlayers() != null ? !mine.getAllowedPlayers().contains(player.getUniqueId()) : true) && !player.isOp())
			returnTell(cache.getUsername() + " has visitation toggled off.");

		mine.teleport(player);

		if (args.length == 0)
			tell("You have teleported to your mine.");
		else if (args.length == 1) {
			tell(player.getName().equalsIgnoreCase(args[0]) ? "You have teleported to your mine." : "You have teleported to the mine of " + cache.getUsername() + ".");
		}
	}

	@Override
	protected List<String> tabComplete() {

		if (args.length == 1) {
			List<ApolloPlayer> cached = ApolloPlayer.getAllCached();
			List<String> names = new ArrayList<>();

			for (Iterator<ApolloPlayer> iterator = cached.listIterator(); iterator.hasNext(); ) {
				ApolloPlayer player = iterator.next();

				if (player.getMine() == null)
					iterator.remove();
				else
					names.add(player.getUsername());
			}

			return completeLastWord(names);
		}
		return NO_COMPLETE;
	}
}
