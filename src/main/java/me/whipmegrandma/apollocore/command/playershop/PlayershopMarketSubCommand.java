package me.whipmegrandma.apollocore.command.playershop;

import me.whipmegrandma.apollocore.enums.SortedBy;
import me.whipmegrandma.apollocore.menu.PlayerShopMenu.PlayerShopMarketMenu;
import me.whipmegrandma.apollocore.model.ApolloPlayer;
import me.whipmegrandma.apollocore.util.PlayerShopUtil;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.List;

public class PlayershopMarketSubCommand extends SimpleSubCommand {

	protected PlayershopMarketSubCommand(SimpleCommandGroup parent) {
		super(parent, "market");

		this.setPermission("apollocore.command.playershop.market");
		this.setUsage("");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		List<ApolloPlayer> data = ApolloPlayer.getAllCached();

		PlayerShopUtil.filter(data);
		PlayerShopUtil.sortShops(SortedBy.NEWEST_ITEM, data);

		new PlayerShopMarketMenu(getPlayer(), data).displayTo(getPlayer());

	}

	@Override
	protected List<String> tabComplete() {
		return NO_COMPLETE;
	}
}
