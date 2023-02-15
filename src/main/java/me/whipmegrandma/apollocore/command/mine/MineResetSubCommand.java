package me.whipmegrandma.apollocore.command.mine;

import me.whipmegrandma.apollocore.database.Database;
import me.whipmegrandma.apollocore.model.ApolloPlayer;
import me.whipmegrandma.apollocore.model.Mine;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.remain.Remain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MineResetSubCommand extends SimpleSubCommand {

	protected MineResetSubCommand(SimpleCommandGroup parent) {
		super(parent, "reset");

		this.setPermission("apollocore.command.mine.reset");
		setUsage("[player]");
	}

	@Override
	protected void onCommand() {
		Player player = getPlayer();
		ApolloPlayer target = ApolloPlayer.from(args.length == 0 ? player.getName() : args[0]);

		if (target != null) {
			Mine mine = target.getMine();

			checkBoolean(mine != null, args.length == 0 ? "You don't own a mine. Use '/mine create' to make one." : target.getUsername() + " doesn't own a mine.");

			mine.resetMine();

			Player targetPlayer = Remain.getPlayerByUUID(target.getUuid());

			if (targetPlayer != null)
				Common.tell(targetPlayer, "Your mine has been reset.");

			if (!getPlayer().equals(targetPlayer))
				super.tell("The mine of " + target.getUsername() + " has been reset.");
		} else

			Database.getInstance().load(args[0], cache -> {

				if (cache == null) {
					super.tell(args[0] + " has never joined the server before.");

					return;
				}

				String name = cache.getUsername();

				tell(name + " doesn't own a mine.");

			});
	}

	@Override
	protected List<String> tabComplete() {

		if (args.length == 1) {
			List<ApolloPlayer> cached = ApolloPlayer.getAllCached();
			List<String> names = new ArrayList<>();

			for (Iterator<ApolloPlayer> iterator = cached.listIterator(); iterator.hasNext(); ) {
				ApolloPlayer player = iterator.next();

				if (player.getMine() == null)
					iterator.remove();
				else
					names.add(player.getUsername());
			}

			return completeLastWord(names);
		}
		return NO_COMPLETE;
	}
}
