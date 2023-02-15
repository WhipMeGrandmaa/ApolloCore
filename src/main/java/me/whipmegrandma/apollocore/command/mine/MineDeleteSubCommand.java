package me.whipmegrandma.apollocore.command.mine;

import me.whipmegrandma.apollocore.model.ApolloPlayer;
import me.whipmegrandma.apollocore.model.Mine;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.List;

public class MineDeleteSubCommand extends SimpleSubCommand {

	protected MineDeleteSubCommand(SimpleCommandGroup parent) {
		super(parent, "delete");

		this.setPermission("apollocore.command.mine.delete");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		ApolloPlayer cache = ApolloPlayer.from(getPlayer());
		Mine mine = cache.getMine();

		checkBoolean(mine != null, "You don't own a mine. Use '/mine create' to make one.");

		mine.delete();
		cache.setMine(null);

		tell("You have successfully deleted your mine.");
	}

	@Override
	protected List<String> tabComplete() {
		return NO_COMPLETE;
	}
}
