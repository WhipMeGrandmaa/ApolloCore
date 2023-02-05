package me.whipmegrandma.apollocore.command;

import me.whipmegrandma.apollocore.database.Database;
import me.whipmegrandma.apollocore.model.ApolloPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommand;

import java.util.List;

@AutoRegister
public final class BlocksCommand extends SimpleCommand {

	public BlocksCommand() {
		super("block|blocks");

		this.setPermission("apollocore.command.blocks");
	}

	@Override
	protected void onCommand() {

		if (args.length == 0) {
			checkConsole();

			int blocks = ApolloPlayer.from(getPlayer()).getBlocksBroken();
			this.blocksTopPosition(getPlayer(), "You've broken " + Common.plural(blocks, "block") + ".", "Blocktop Position: #%position%");

			return;
		}

		String username = args[0];

		Player target = Bukkit.getPlayerExact(username);

		if (target != null) {

			int blocks = ApolloPlayer.from(target).getBlocksBroken();

			if (target.equals(getPlayer())) {
				this.blocksTopPosition(getPlayer(), "You've broken " + Common.plural(blocks, "block") + ".", "Blocktop Position: #%position%");

				return;
			}

			this.blocksTopPosition(target, getPlayer(), target.getName() + " has broken " + Common.plural(blocks, "block") + ".", "Blocktop Position: #%position%");

		} else {

			Database.getInstance().load(username, cache -> {

				if (cache == null) {
					super.tell(username + " has never joined the server before.");

					return;
				}

				String name = cache.getUsername();
				int blocks = cache.getBlocksBroken();

				super.tell(name + " has broken " + Common.plural(blocks, "block") + ".");
				this.blocksTopPosition(name, getPlayer(), name + " has broken " + Common.plural(blocks, "block") + ".", "Blocktop Position: #%position%");

			});
		}
	}

	@Override
	protected List<String> tabComplete() {

		if (args.length == 1)
			return completeLastWordPlayerNames();

		return NO_COMPLETE;
	}

	private void blocksTopPosition(Player sender, String... message) {
		this.blocksTopPosition(sender, sender, message);
	}

	private void blocksTopPosition(Player target, Player sender, String... message) {
		this.blocksTopPosition(target.getName(), sender, message);
	}

	private void blocksTopPosition(String target, Player sender, String... message) {

		Database.getInstance().loadAll(allData -> {

			allData.sort((cache1, cache2) -> cache2.getBlocksBroken().compareTo(cache1.getBlocksBroken()));

			int position = 1;

			for (ApolloPlayer cache : allData) {
				if (cache.getUsername().equals(target))
					for (String line : message)
						Common.tell(sender, line.replace("%position%", String.valueOf(position)));

				++position;
			}
		});
	}
}