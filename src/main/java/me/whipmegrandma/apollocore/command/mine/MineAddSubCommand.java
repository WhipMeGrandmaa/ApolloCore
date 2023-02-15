package me.whipmegrandma.apollocore.command.mine;

import me.whipmegrandma.apollocore.model.ApolloPlayer;
import me.whipmegrandma.apollocore.model.Mine;
import me.whipmegrandma.apollocore.settings.MineSettings;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.List;

public class MineAddSubCommand extends SimpleSubCommand {

	protected MineAddSubCommand(SimpleCommandGroup parent) {
		super(parent, "add");

		this.setPermission("apollocore.command.mine.add");
		this.setMinArguments(1);
		this.setUsage("<player>");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		ApolloPlayer cache = ApolloPlayer.from(getPlayer());
		Mine mine = cache.getMine();

		checkBoolean(mine != null, "You don't own a mine. Use '/mine create' to make one.");

		Player player = findPlayer(args[0], args[0] + " isn't online.");
		int allowedSize = MineSettings.getInstance().getMaxMinePlayerSize();
		int size = mine.getAllowedPlayersAmount();

		checkBoolean(!getPlayer().equals(player), "You cannot add yourself to your own mine.");
		checkBoolean(!mine.isPlayerAllowed(player), player.getName() + " is already added to your mine.");
		checkBoolean(size < allowedSize, "You can only add " + Common.plural(allowedSize, "player") + " to your mine.");

		mine.addAllowedPlayer(player);
		tell("You've added " + player.getName() + " to your mine.");
		Common.tell(player, "You've been added to the mine of " + getPlayer().getName() + ".");
	}

	@Override
	protected List<String> tabComplete() {
		List<String> playerNames = completeLastWordPlayerNames();
		playerNames.remove(getPlayer().getName());

		return playerNames;
	}
}
