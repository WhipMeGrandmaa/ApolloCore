package me.whipmegrandma.apollocore.command.mine;

import me.whipmegrandma.apollocore.settings.MineSettings;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.List;

public class MineSetradiusSubCommand extends SimpleSubCommand {

	protected MineSetradiusSubCommand(SimpleCommandGroup parent) {
		super(parent, "setradius");

		this.setPermission("apollocore.command.mine.setradius");
		this.setMinArguments(1);
		this.setUsage("<radius>");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		int number = findNumber(0, "The radius must be a number.");
		checkBoolean(number > 0, "The radius must be greater than 0.");

		MineSettings.getInstance().setRadius(number);

		tell("You've set the border radius to " + number + ".");
	}

	@Override
	protected List<String> tabComplete() {
		return NO_COMPLETE;
	}
}
