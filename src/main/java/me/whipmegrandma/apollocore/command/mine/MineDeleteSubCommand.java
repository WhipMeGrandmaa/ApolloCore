package me.whipmegrandma.apollocore.command.mine;

import me.whipmegrandma.apollocore.model.ApolloPlayer;
import me.whipmegrandma.apollocore.model.Mine;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.remain.Remain;

import java.util.List;

public class MineDeleteSubCommand extends SimpleSubCommand {

	protected MineDeleteSubCommand(SimpleCommandGroup parent) {
		super(parent, "delete");

		this.setPermission("apollocore.command.mine.delete");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		ApolloPlayer cache = ApolloPlayer.from(getPlayer());
		Mine mine = cache.getMine();

		checkBoolean(mine != null, "You don't own a mine. Use '/mine create' to make one.");

		for (Player player : Remain.getOnlinePlayers())
			if (mine.isWithin(player.getLocation()))
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/spawn " + player.getName());

		mine.delete();
		cache.setMine(null);

		tell("You have successfully deleted your mine.");
	}

	@Override
	protected List<String> tabComplete() {
		return NO_COMPLETE;
	}
}
