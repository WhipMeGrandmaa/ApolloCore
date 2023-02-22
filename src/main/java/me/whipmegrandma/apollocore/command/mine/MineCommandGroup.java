package me.whipmegrandma.apollocore.command.mine;

import org.mineacademy.fo.Common;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.model.SimpleComponent;

import java.util.Arrays;
import java.util.List;

@AutoRegister
public final class MineCommandGroup extends SimpleCommandGroup {

	public MineCommandGroup() {
		super("mine");
	}

	@Override
	protected void registerSubcommands() {
		registerSubcommand(new MineCreateSubCommand(this));
		registerSubcommand(new MineEditSubCommand(this));
		registerSubcommand(new MineSethomeSubCommand(this));
		registerSubcommand(new MineSetcenterSubCommand(this));
		registerSubcommand(new MineDeleteSubCommand(this));
		registerSubcommand(new MineTeleportSubCommand(this));
		registerSubcommand(new MineSetradiusSubCommand(this));
		registerSubcommand(new MineToggleSubCommand(this));
		registerSubcommand(new MineAddSubCommand(this));
		registerSubcommand(new MineKickSubCommand(this));
		registerSubcommand(new MineInfoSubCommand(this));
		registerSubcommand(new MineResetSubCommand(this));
		registerSubcommand(new MineResetallSubCommand(this));
	}

	@Override
	protected String[] getHelpHeader() {
		return new String[]{Common.colorize("&d&lMINE HELP MENU")};
	}

	@Override
	protected List<SimpleComponent> getNoParamsHeader() {
		return Arrays.asList(SimpleComponent.of("{prefix} Use /mine ? to list the commands."));
	}
}
