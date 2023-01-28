package me.whipmegrandma.apollocore.command.rank;

import me.whipmegrandma.apollocore.model.Rank;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.List;

public class RankListSubCommand extends SimpleSubCommand {

	protected RankListSubCommand(SimpleCommandGroup parent) {
		super(parent, "list");

		this.setPermission("apollocore.command.rank.list");
	}

	@Override
	protected void onCommand() {
		tell("List of available ranks: " + Common.join(Rank.getRankNames()) + ".");
	}

	@Override
	protected List<String> tabComplete() {
		return NO_COMPLETE;
	}
}
