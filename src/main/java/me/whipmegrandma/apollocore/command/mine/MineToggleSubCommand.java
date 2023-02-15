package me.whipmegrandma.apollocore.command.mine;

import me.whipmegrandma.apollocore.model.ApolloPlayer;
import me.whipmegrandma.apollocore.model.Mine;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.remain.Remain;

import java.util.List;

public class MineToggleSubCommand extends SimpleSubCommand {

	protected MineToggleSubCommand(SimpleCommandGroup parent) {
		super(parent, "toggle");

		this.setPermission("apollocore.command.mine.toggle");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		ApolloPlayer cache = ApolloPlayer.from(getPlayer());
		Mine mine = cache.getMine();

		checkBoolean(mine != null, "You don't own a mine. Use '/mine create' to make one.");

		boolean canTeleport = mine.getCanTeleport();
		mine.setCanTeleport(!canTeleport);

		if (canTeleport)
			for (Player player : Remain.getOnlinePlayers())
				if (mine.isWithin(player.getLocation()) && !cache.getUuid().equals(player.getUniqueId())) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/spawn " + player.getName());
					Common.tell(player, cache.getUsername() + " has mine teleportation toggled off to outsiders.");
				}

		tell("Toggled " + (canTeleport ? "off" : "on") + " allowing outsiders to teleport to your mine.");
	}

	@Override
	protected List<String> tabComplete() {
		return NO_COMPLETE;
	}
}
