package me.whipmegrandma.apollocore.command.disenchanter;

import org.mineacademy.fo.Common;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.model.SimpleComponent;

import java.util.Arrays;
import java.util.List;

@AutoRegister
public final class DisenchanterCommandGroup extends SimpleCommandGroup {

	public DisenchanterCommandGroup() {
		super("disenchanter|denchanter");
	}

	@Override
	protected void registerSubcommands() {
		registerSubcommand(new DisenchanterResetSubCommand(this));
		registerSubcommand(new DisenchanterListSubCommand(this));
		registerSubcommand(new DisenchanterSetSubCommand(this));
	}

	@Override
	protected String[] getHelpHeader() {
		return new String[]{"", Common.colorize("&d&lDISENCHANTER HELP MENU")};
	}

	@Override
	protected List<SimpleComponent> getNoParamsHeader() {
		return Arrays.asList(SimpleComponent.of("{prefix} Use /disenchanter ? to list the commands."));
	}
}
