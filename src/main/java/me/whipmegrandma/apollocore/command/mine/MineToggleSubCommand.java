package me.whipmegrandma.apollocore.command.mine;

import me.whipmegrandma.apollocore.model.ApolloPlayer;
import me.whipmegrandma.apollocore.model.Mine;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

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

		tell("Toggled " + (canTeleport ? "off" : "on") + " allowing outsiders to teleport to your mine.");
	}

	@Override
	protected List<String> tabComplete() {
		return NO_COMPLETE;
	}
}
