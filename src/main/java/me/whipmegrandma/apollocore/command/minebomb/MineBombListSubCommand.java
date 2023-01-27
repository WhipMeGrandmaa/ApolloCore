package me.whipmegrandma.apollocore.command.minebomb;

import me.whipmegrandma.apollocore.model.MineBomb;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.List;

public class MineBombListSubCommand extends SimpleSubCommand {

	protected MineBombListSubCommand(SimpleCommandGroup parent) {
		super(parent, "list");

		this.setPermission("apollocore.command.minebomb.list");
	}

	@Override
	protected void onCommand() {
		System.out.println(PlayerUtil.getStatistic(getPlayer(), Statistic.MINE_BLOCK, Material.DIRT));
		tell("List of available mine bombs: " + Common.join(MineBomb.bombNames()) + ".");
	}

	@Override
	protected List<String> tabComplete() {
		return NO_COMPLETE;
	}
}
