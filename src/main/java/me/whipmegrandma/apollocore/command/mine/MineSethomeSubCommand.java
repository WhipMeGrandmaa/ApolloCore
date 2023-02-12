package me.whipmegrandma.apollocore.command.mine;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.List;

public class MineSethomeSubCommand extends SimpleSubCommand {

	protected MineSethomeSubCommand(SimpleCommandGroup parent) {
		super(parent, "sethome");

		this.setPermission("apollocore.command.mine.sethome");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		Player player = getPlayer();
		MineEditSubCommand instance = MineEditSubCommand.getInstance();

		checkBoolean(player.getUniqueId().equals(instance.getEditor()), "You must be in mine edit mode to use this command.");

		Location home = player.getLocation();
		
		instance.setHomeLocation(home);

		tell("You've set the mine home location.");
	}

	@Override
	protected List<String> tabComplete() {
		return NO_COMPLETE;
	}
}
