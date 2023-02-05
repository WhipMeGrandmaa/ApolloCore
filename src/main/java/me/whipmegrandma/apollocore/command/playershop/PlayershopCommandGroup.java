package me.whipmegrandma.apollocore.command.playershop;

import org.mineacademy.fo.Common;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.model.SimpleComponent;

import java.util.Arrays;
import java.util.List;

@AutoRegister
public final class PlayershopCommandGroup extends SimpleCommandGroup {

	public PlayershopCommandGroup() {
		super("playershop|pshop");
	}

	@Override
	protected void registerSubcommands() {
		registerSubcommand(new PlayershopAddSubCommand(this));
		registerSubcommand(new PlayershopOpenSubCommand(this));
		registerSubcommand(new PlayershopMarketSubCommand(this));
	}

	@Override
	protected String[] getHelpHeader() {
		return new String[]{Common.colorize("{prefix} The following commands are available:")};
	}

	@Override
	protected List<SimpleComponent> getNoParamsHeader() {
		return Arrays.asList(SimpleComponent.of("{prefix} Use /pshop ? to list the commands."));
	}
}
