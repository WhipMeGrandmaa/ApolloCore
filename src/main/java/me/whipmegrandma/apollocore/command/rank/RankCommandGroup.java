package me.whipmegrandma.apollocore.command.rank;

import org.mineacademy.fo.Common;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.model.SimpleComponent;

import java.util.Arrays;
import java.util.List;

@AutoRegister
public final class RankCommandGroup extends SimpleCommandGroup {

	public RankCommandGroup() {
		super("rank");
	}

	@Override
	protected void registerSubcommands() {
		this.registerSubcommand(new RankResetSubCommand(this));
		this.registerSubcommand(new RankSetSubCommand(this));
		this.registerSubcommand(new RankListSubCommand(this));
	}

	@Override
	protected String[] getHelpHeader() {
		return new String[]{Common.colorize("", Common.colorize("&d&lRANK HELP MENU"))};
	}

	@Override
	protected List<SimpleComponent> getNoParamsHeader() {
		return Arrays.asList(SimpleComponent.of("{prefix} Use /rank ? to list the commands."));
	}
}
