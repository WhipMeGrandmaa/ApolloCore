package me.whipmegrandma.apollocore.command.minebomb;

import me.whipmegrandma.apollocore.model.MineBomb;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.List;

public class MinebombListSubCommand extends SimpleSubCommand {

	protected MinebombListSubCommand(SimpleCommandGroup parent) {
		super(parent, "list");

		this.setPermission("apollocore.command.minebomb.list");
	}

	@Override
	protected void onCommand() {
		tell("List of available mine bombs: " + Common.join(MineBomb.bombNames()) + ".");
	}

	@Override
	protected List<String> tabComplete() {
		return NO_COMPLETE;
	}
}
