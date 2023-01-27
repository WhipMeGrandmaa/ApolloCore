package me.whipmegrandma.apollocore.command.minebomb;

import org.mineacademy.fo.Common;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.model.SimpleComponent;

import java.util.Arrays;
import java.util.List;

@AutoRegister
public final class MineBombCommandGroup extends SimpleCommandGroup {

	public MineBombCommandGroup() {
		super("minebomb|mb");
	}

	@Override
	protected void registerSubcommands() {
		registerSubcommand(new MineBombGiveSubCommand(this));
		registerSubcommand(new MineBombListSubCommand(this));
	}

	@Override
	protected String[] getHelpHeader() {
		return new String[]{Common.colorize("{prefix} The following commands are available:")};
	}


	@Override
	protected List<SimpleComponent> getNoParamsHeader() {
		return Arrays.asList(SimpleComponent.of("{prefix} Use /minebomb ? to list the commands."));
	}
}
