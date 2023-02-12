package me.whipmegrandma.apollocore.command.mine;

import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.List;

public class MineSetcenterSubCommand extends SimpleSubCommand {

	protected MineSetcenterSubCommand(SimpleCommandGroup parent) {
		super(parent, "setcenter");

		this.setPermission("apollocore.command.mine.setcenter");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		MineEditSubCommand instance = MineEditSubCommand.getInstance();

		checkBoolean(getPlayer().getUniqueId().equals(instance.getEditor()), "You must be in mine edit mode to use this command.");

		instance.setCenterLocation(getPlayer().getLocation());

		tell("You've set the mine center location.");
	}

	@Override
	protected List<String> tabComplete() {
		return NO_COMPLETE;
	}
}
