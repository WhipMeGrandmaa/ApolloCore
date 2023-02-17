package me.whipmegrandma.apollocore.command.apollocore;

import org.mineacademy.fo.Common;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.ReloadCommand;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.model.SimpleComponent;

import java.util.Arrays;
import java.util.List;

@AutoRegister
public final class ApollocoreCommandGroup extends SimpleCommandGroup {

	public ApollocoreCommandGroup() {
		super("apollocore");
	}

	@Override
	protected void registerSubcommands() {
		this.registerSubcommand(new ReloadCommand());
	}

	@Override
	protected String[] getHelpHeader() {
		return new String[]{"", Common.colorize("&d&lAPOLLOCORE HELP MENU")};
	}

	@Override
	protected List<SimpleComponent> getNoParamsHeader() {
		return Arrays.asList(SimpleComponent.of("{prefix} Use /apollocore ? to list the commands."));
	}
}
