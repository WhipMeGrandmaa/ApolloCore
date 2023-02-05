package me.whipmegrandma.apollocore.menu.PlayerShopMenu;

import me.whipmegrandma.apollocore.api.IntermediateMenuPagged;
import me.whipmegrandma.apollocore.enums.SortedBy;
import me.whipmegrandma.apollocore.model.ApolloPlayer;
import me.whipmegrandma.apollocore.model.ShopItem;
import me.whipmegrandma.apollocore.settings.PlayerShopMenuSettings;
import me.whipmegrandma.apollocore.util.PlaceholderUtil;
import me.whipmegrandma.apollocore.util.PlayerShopUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.ItemUtil;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.button.annotation.Position;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class PlayerShopMarketMenu extends IntermediateMenuPagged<ApolloPlayer> {

	private List<ApolloPlayer> data;

	@Position(9 * 5)
	private final Button personalShop;

	@Position(9 * 5 + 1)
	private final Button refresh;

	@Position(9 * 5 + 7)
	private final Button sorter;

	private SortedBy sort = SortedBy.NEWEST_ITEM;

	private PlayerShopMarketMenu(Player player, List<ApolloPlayer> data, SortedBy sort) {
		this(player, data);

		this.sort = sort;
	}

	public PlayerShopMarketMenu(Player player, List<ApolloPlayer> data) {
		super(9 * 5, data);

		this.setViewer(player);
		this.data = data;

		this.setTitle(PlaceholderUtil.set(player, PlayerShopMenuSettings.marketPlaceMenuTitle));

		this.personalShop = new Button() {
			@Override
			public void onClickedInMenu(Player player, Menu menu, ClickType click) {
				List<ApolloPlayer> data = ApolloPlayer.getAllCached();

				for (ApolloPlayer personalData : data) {
					if (player.getUniqueId().equals(personalData.getUuid())) {
						List<ShopItem> items = personalData.getShopItems();
						PlayerShopUtil.sortItems(SortedBy.Individual.NEWEST, items);

						new PlayerShopIndividualMenu(player, personalData, items).displayTo(player);
					}
				}
			}

			@Override
			public ItemStack getItem() {
				return ItemCreator.of(PlayerShopMenuSettings.personalShopMaterial, PlayerShopMenuSettings.personalShopTitle, PlayerShopMenuSettings.personalShopLore).make();
			}
		};

		this.refresh = new Button() {
			@Override
			public void onClickedInMenu(Player player, Menu menu, ClickType click) {
				List<ApolloPlayer> data = PlayerShopUtil.getAllDataFiltered();

				PlayerShopUtil.sortShops(sort, data);

				Menu marketMenu = new PlayerShopMarketMenu(player, data, sort);

				marketMenu.displayTo(player);
				Common.runLater(1, () -> marketMenu.animateTitle("&aRefreshed listings"));
			}

			@Override
			public ItemStack getItem() {
				return ItemCreator.of(PlayerShopMenuSettings.refreshMaterial, PlayerShopMenuSettings.refreshTitle, PlayerShopMenuSettings.refreshLore).make();
			}
		};

		this.sorter = new Button() {
			@Override
			public void onClickedInMenu(Player player, Menu menu, ClickType click) {
				List<SortedBy> values = Arrays.asList(SortedBy.values());

				int index = 0;
				for (SortedBy value : values) {
					if (value == sort) {
						sort = values.get(index + 2 > values.size() ? 0 : index + 1);

						break;
					}

					index++;
				}
				PlayerShopUtil.sortShops(sort, data);

				Menu marketMenu = new PlayerShopMarketMenu(player, data, sort);

				marketMenu.displayTo(player);
				Common.runLater(1, () -> marketMenu.animateTitle("&aSorted by " + ItemUtil.bountifyCapitalized(sort.toString())));
			}

			@Override
			public ItemStack getItem() {

				switch (sort) {
					case NEWEST_ITEM:

						return ItemCreator.of(PlayerShopMenuSettings.sortedNewMaterial, PlayerShopMenuSettings.sortedNewTitle, PlayerShopMenuSettings.sortedNewLore).make();
					case MOST_ITEMS:

						return ItemCreator.of(PlayerShopMenuSettings.sortedMostMaterial, PlayerShopMenuSettings.sortedMostTitle, PlayerShopMenuSettings.sortedMostLore).make();
				}

				return NO_ITEM;
			}
		};
	}

	@Override
	protected ItemStack convertToItemStack(ApolloPlayer seller) {
		ShopItem newestShopItem = !seller.getShopItems().isEmpty() ? seller.getShopItems().get(seller.getShopItems().size() - 1) : null;
		ItemStack itemStack = newestShopItem != null ? newestShopItem.getItem() : null;
		ItemMeta itemMeta = newestShopItem != null ? itemStack.getItemMeta() : null;
		String timeMade = newestShopItem != null ? new PrettyTime().format(new Date(newestShopItem.getTimeMade())) : null;

		String newestItemName = itemMeta != null ? !itemMeta.getDisplayName().isEmpty() ? itemMeta.getDisplayName() : ItemUtil.bountifyCapitalized(itemStack.getType()) : "No Item";

		String title = PlayerShopMenuSettings.marketPlaceIndividualTitle
				.replace("%player_name%", seller.getUsername())
				.replace("%playershop_itemsize%", NumberFormat.getInstance().format(seller.getShopItems().size()))
				.replace("%playershop_newestitemname%", newestItemName)
				.replace("%playershop_newestitemtime%", timeMade != null ? timeMade : "Never");

		List<String> lore = new ArrayList<>();

		for (String line : PlayerShopMenuSettings.marketPlaceIndividualLore)
			lore.add(line
					.replace("%player_name%", seller.getUsername())
					.replace("%playershop_itemsize%", NumberFormat.getInstance().format(seller.getShopItems().size()))
					.replace("%playershop_newestitemname%", newestItemName)
					.replace("%playershop_newestitemtimemade%", timeMade != null ? timeMade : "Never"));

		return ItemCreator.of(CompMaterial.PLAYER_HEAD)
				.name(title)
				.lore(lore)
				.skullOwner(seller.getUsername())
				.make();
	}

	@Override
	protected void onPageClick(Player player, ApolloPlayer seller, ClickType click) {
		List<ShopItem> items = seller.getShopItems();
		PlayerShopUtil.sortItems(SortedBy.Individual.NEWEST, items);

		new PlayerShopIndividualMenu(player, seller, items).displayTo(player);
	}

	@Override
	protected String[] getInfo() {
		return PlayerShopMenuSettings.infoLore.toArray(new String[0]);
	}

	@Override
	protected int getInfoButtonPosition() {
		return 9 * 5 + 4;
	}

	@Override
	public Menu newInstance() {
		return new PlayerShopMarketMenu(this.getViewer(), this.data, this.sort);
	}
}
