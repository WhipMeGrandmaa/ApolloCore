package me.whipmegrandma.apollocore.command;

import me.whipmegrandma.apollocore.database.Database;
import me.whipmegrandma.apollocore.model.PlayerCache;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommand;
import org.mineacademy.fo.model.ChatPaginator;
import org.mineacademy.fo.model.SimpleComponent;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

@AutoRegister
public final class PrestigetopCommand extends SimpleCommand {

	public PrestigetopCommand() {
		super("prestigetop|ptop");

		this.setPermission("apollocore.command.prestigetop");
	}

	@Override
	protected void onCommand() {
		Database.getInstance().loadAll(allData -> {

			allData.sort((cache1, cache2) -> cache2.getPrestige().compareTo(cache1.getPrestige()));

			int position = 1;
			List<SimpleComponent> lines = new ArrayList<>();

			for (PlayerCache cache : allData) {
				Player target = Bukkit.getPlayerExact(cache.getUsername());

				if (target != null)
					cache = PlayerCache.from(target);

				lines.add(SimpleComponent.of("&f#" + position++ + " &7- &f" + cache.getUsername() + " &7(" + NumberFormat.getInstance().format(cache.getPrestige()) + ")"));
			}

			new ChatPaginator(10, ChatColor.DARK_GRAY)
					.setHeader(
							"&8" + Common.chatLineSmooth(),
							"<center>&6Prestige Top",
							"&8" + Common.chatLineSmooth()
					).setPages(lines)
					.send(getSender());
		});
	}

	@Override
	protected List<String> tabComplete() {
		return NO_COMPLETE;
	}
}
