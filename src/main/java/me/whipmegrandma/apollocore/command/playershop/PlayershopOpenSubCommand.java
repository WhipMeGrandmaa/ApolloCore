package me.whipmegrandma.apollocore.command.playershop;

import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.List;

public class PlayershopOpenSubCommand extends SimpleSubCommand {

	protected PlayershopOpenSubCommand(SimpleCommandGroup parent) {
		super(parent, "view");

		this.setPermission("apollocore.command.minebomb.open");
		this.setUsage("[player]");
	}

	@Override
	protected void onCommand() {
	}

	@Override
	protected List<String> tabComplete() {

		if (args.length == 1)
			return completeLastWordPlayerNames();

		return NO_COMPLETE;
	}
}
