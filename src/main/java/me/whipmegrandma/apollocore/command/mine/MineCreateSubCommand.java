package me.whipmegrandma.apollocore.command.mine;

import me.whipmegrandma.apollocore.model.ApolloPlayer;
import me.whipmegrandma.apollocore.model.Mine;
import me.whipmegrandma.apollocore.settings.MineSettings;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.List;

public class MineCreateSubCommand extends SimpleSubCommand {

	protected MineCreateSubCommand(SimpleCommandGroup parent) {
		super(parent, "create");

		this.setPermission("apollocore.command.mine.create");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		Mine defaultMine = MineSettings.getInstance().getDefaultMine();

		if (defaultMine.getCenter() == null || defaultMine.getMineRegion() == null || defaultMine.getHome() == null || MineSettings.getInstance().getSchematic() == null)
			returnTell("Mine settings aren't set up. Contact an administrator to fix the issue.");

		ApolloPlayer cache = ApolloPlayer.from(getPlayer());

		checkBoolean(cache.getMine() == null, "You already own a mine!");

		Mine mine = Mine.create();
		cache.setMine(mine);

		tell("You have successfully created a mine.");

		mine.teleport(getPlayer());
	}

	@Override
	protected List<String> tabComplete() {
		return NO_COMPLETE;
	}
}
