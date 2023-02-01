package me.whipmegrandma.apollocore.command.blockstats;

import org.mineacademy.fo.Common;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.model.SimpleComponent;

import java.util.Arrays;
import java.util.List;

@AutoRegister
public final class BlockstatsCommandGroup extends SimpleCommandGroup {

	public BlockstatsCommandGroup() {
		super("blockstats");
	}

	@Override
	protected void registerSubcommands() {
		registerSubcommand(new BlockstatsResetSubCommand(this));
		registerSubcommand(new BlockstatsSetSubCommand(this));
	}

	@Override
	protected String[] getHelpHeader() {
		return new String[]{Common.colorize("{prefix} The following commands are available:")};
	}

	@Override
	protected List<SimpleComponent> getNoParamsHeader() {
		return Arrays.asList(SimpleComponent.of("{prefix} Use /blockstats ? to list the commands."));
	}
}
