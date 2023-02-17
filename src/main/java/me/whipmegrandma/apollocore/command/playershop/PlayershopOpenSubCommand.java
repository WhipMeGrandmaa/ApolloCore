package me.whipmegrandma.apollocore.command.playershop;

import me.whipmegrandma.apollocore.database.Database;
import me.whipmegrandma.apollocore.enums.SortedBy;
import me.whipmegrandma.apollocore.menu.PlayerShopMenu.PlayerShopIndividualMenu;
import me.whipmegrandma.apollocore.model.ApolloPlayer;
import me.whipmegrandma.apollocore.model.ShopItem;
import me.whipmegrandma.apollocore.util.PlayerShopUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.ArrayList;
import java.util.List;

public class PlayershopOpenSubCommand extends SimpleSubCommand {

	protected PlayershopOpenSubCommand(SimpleCommandGroup parent) {
		super(parent, "open");

		this.setPermission("apollocore.command.playershop.open");
		this.setUsage("[player]");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		if (args.length == 0) {
			List<ShopItem> items = ApolloPlayer.from(getPlayer()).getShopItems();
			PlayerShopUtil.sortItems(SortedBy.Individual.NEWEST, items);

			new PlayerShopIndividualMenu(getPlayer(), ApolloPlayer.from(getPlayer()), items).displayTo(getPlayer());

			return;
		}

		String username = args[0];
		Player target = Bukkit.getPlayerExact(username);

		if (target != null) {
			ApolloPlayer seller = ApolloPlayer.from(username);

			checkBoolean(seller != null, username + " doesn't have anything in their player shop.");

			List<ShopItem> items = seller.getShopItems();
			PlayerShopUtil.sortItems(SortedBy.Individual.NEWEST, items);

			new PlayerShopIndividualMenu(getPlayer(), seller, items).displayTo(getPlayer());
			return;
		}

		Database.getInstance().load(username, cache -> {

			if (cache == null) {
				super.tell(username + " has never joined the server before.");

				return;
			}

			String name = cache.getUsername();
			super.tell(name + " doesn't have anything in their player shop.");
		});
	}


	@Override
	protected List<String> tabComplete() {

		if (args.length == 1) {
			List<String> names = new ArrayList<>();

			for (ApolloPlayer player : ApolloPlayer.getAllCached())
				names.add(player.getUsername());

			return completeLastWord(names);
		}

		return NO_COMPLETE;
	}
}
