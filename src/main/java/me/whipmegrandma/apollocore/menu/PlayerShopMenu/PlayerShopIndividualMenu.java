package me.whipmegrandma.apollocore.menu.PlayerShopMenu;

import me.whipmegrandma.apollocore.api.IntermediateMenuPagged;
import me.whipmegrandma.apollocore.database.Database;
import me.whipmegrandma.apollocore.enums.SortedBy;
import me.whipmegrandma.apollocore.hook.VaultHook;
import me.whipmegrandma.apollocore.model.ApolloPlayer;
import me.whipmegrandma.apollocore.model.ShopItem;
import me.whipmegrandma.apollocore.settings.PlayerShopMenuSettings;
import me.whipmegrandma.apollocore.util.PersonalShopUtil;
import me.whipmegrandma.apollocore.util.PlaceholderUtil;
import net.milkbowl.vault.economy.Economy;
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
import org.mineacademy.fo.remain.Remain;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class PlayerShopIndividualMenu extends IntermediateMenuPagged<ShopItem> {

	private ApolloPlayer seller;

	@Position(9 * 5)
	private final Button personalShop;

	@Position(9 * 5 + 1)
	private final Button refresh;

	@Position(9 * 5 + 7)
	private final Button sorter;

	@Position(9 * 5 + 8)
	private final Button market;

	private SortedBy.Individual sort = SortedBy.Individual.PRICE_DESCENDING;

	public PlayerShopIndividualMenu(Player player, ApolloPlayer seller) {
		super(9 * 5, seller.getShopItems());

		this.seller = seller;

		this.setViewer(player);
		this.setTitle(PlaceholderUtil.set(seller.getUsername(), PlayerShopMenuSettings.pShopMenuTitle));

		this.personalShop = !this.seller.getUuid().equals(player.getUniqueId()) ? new Button() {
			@Override
			public void onClickedInMenu(Player player, Menu menu, ClickType click) {
				List<ApolloPlayer> data = ApolloPlayer.getAllCached();

				for (ApolloPlayer personalData : data)
					if (player.getUniqueId().equals(personalData.getUuid()))
						new PlayerShopIndividualMenu(player, personalData).displayTo(player);
			}

			@Override
			public ItemStack getItem() {
				return ItemCreator.of(PlayerShopMenuSettings.personalShopMaterial, PlayerShopMenuSettings.personalShopTitle, PlayerShopMenuSettings.personalShopLore).make();
			}
		} : Button.makeSimple(ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE).name(" "), non -> {
		});

		this.refresh = new Button() {
			@Override
			public void onClickedInMenu(Player player, Menu menu, ClickType click) {
				List<ApolloPlayer> data = ApolloPlayer.getAllCached();

				for (ApolloPlayer personalData : data)
					if (seller.getUuid().equals(personalData.getUuid())) {
						Menu individualMenu = new PlayerShopIndividualMenu(player, personalData);

						individualMenu.displayTo(player);
						Common.runLater(1, () -> animateTitle("&aRefreshed listings"));
					}

			}

			@Override
			public ItemStack getItem() {
				return ItemCreator.of(PlayerShopMenuSettings.refreshMaterial, PlayerShopMenuSettings.refreshTitle, PlayerShopMenuSettings.refreshLore).make();
			}
		};

		this.sorter = new Button() {
			@Override
			public void onClickedInMenu(Player player, Menu menu, ClickType click) {
				List<SortedBy.Individual> values = Arrays.asList(SortedBy.Individual.values());

				int index = 0;
				for (SortedBy.Individual value : values) {
					if (value == sort) {
						sort = values.get(index + 2 > values.size() ? 0 : index + 1);

						break;
					}

					index++;
				}
				PersonalShopUtil.sortShops(sort, data);

				Menu marketMenu = new PlayerShopMarketMenu(player, data, sort);

				marketMenu.displayTo(player);
				Common.runLater(1, () -> marketMenu.animateTitle("&aSorted by " + ItemUtil.bountifyCapitalized(sort.toString())));
			}

			@Override
			public ItemStack getItem() {

				switch (sort) {
					case PRICE_DESCENDING:

						return ItemCreator.of(PlayerShopMenuSettings.sortedNewMaterial, PlayerShopMenuSettings.sortedNewTitle, PlayerShopMenuSettings.sortedNewLore).make();
					case PRICE_ASCENDING:

						return ItemCreator.of(PlayerShopMenuSettings.sortedMostMaterial, PlayerShopMenuSettings.sortedMostTitle, PlayerShopMenuSettings.sortedMostLore).make();
					case NEWEST:

						return ItemCreator.of(PlayerShopMenuSettings.sortedNewMaterial, PlayerShopMenuSettings.sortedNewTitle, PlayerShopMenuSettings.sortedNewLore).make();
					case OLDEST:

						return ItemCreator.of(PlayerShopMenuSettings.sortedMostMaterial, PlayerShopMenuSettings.sortedMostTitle, PlayerShopMenuSettings.sortedMostLore).make();
				}

				return NO_ITEM;
			}
		};
	}

		this.com.bekvon.bukkit.residence.commands.market =new

	Button() {
		@Override
		public void onClickedInMenu (Player player, Menu menu, ClickType click){
			List<ApolloPlayer> data = PersonalShopUtil.getAllDataFiltered();

			new PlayerShopMarketMenu(player, data).displayTo(player);
		}

		@Override
		public ItemStack getItem () {
			return ItemCreator.of(PlayerShopMenuSettings.marketMaterial, PlayerShopMenuSettings.marketTitle, PlayerShopMenuSettings.marketLore).make();
		}
	}

	;
}

	@Override
	protected ItemStack convertToItemStack(ShopItem item) {
		List<String> plainLore = this.getViewer().getUniqueId().equals(seller.getUuid()) ? PlayerShopMenuSettings.pShopIndividualYoursLore : PlayerShopMenuSettings.pShopIndividualOtherLore;

		List<String> addedLore = new ArrayList<>();

		for (String line : plainLore)
			addedLore.add(line
					.replace("%playershop_itemprice%", NumberFormat.getInstance().format(item.getPrice()))
					.replace("%playershop_itemtimemade%", new PrettyTime().format(new Date(item.getTimeMade()))));

		return ItemCreator.of(item.getItem())
				.lore(addedLore)
				.make();
	}

	@Override
	protected void onPageClick(Player player, ShopItem item, ClickType click) {
		Economy economy = VaultHook.getEconomy();
		double balance = economy.getBalance(player);

		ApolloPlayer seller = ApolloPlayer.from(this.seller.getUuid());

		if (player.getUniqueId().equals(seller.getUuid())) {
			if (!seller.getShopItems().contains(item)) {
				this.refreshAndRestart("&cItem has been sold");

				return;
			}

			item.give(player);
			seller.removeShopItem(item);
			this.refreshAndRestart("&aRemoved item from listing");

			return;
		}

		if (!seller.getShopItems().contains(item)) {
			PlayerShopIndividualMenu.this.refreshAndRestart("&cItem is no longer listed");

			return;
		}

		if (item.getPrice() > balance && !player.isOp()) {
			animateTitle("&cInsufficient balance");

			return;
		}

		new ConfirmMenu(player, item, this.seller).displayTo(player);
	}

	private void refreshAndRestart() {
		this.refreshAndRestart(null);
	}

	private void refreshAndRestart(String message) {
		List<ApolloPlayer> data = ApolloPlayer.getAllCached();

		for (ApolloPlayer personalData : data)
			if (seller.getUuid().equals(personalData.getUuid())) {
				Menu menu = new PlayerShopIndividualMenu(this.getViewer(), personalData);

				menu.displayTo(getViewer());

				if (message != null)
					Common.runLater(1, () -> animateTitle(message));
			}
	}

	@Override
	protected String[] getInfo() {
		return PlayerShopMenuSettings.infoLore.toArray(new String[0]);
	}

	@Override
	protected int getInfoButtonPosition() {
		return 9 * 5 + 4;
	}

private class ConfirmMenu extends Menu {

	private ShopItem shopItem;
	private ApolloPlayer data;

	@Position(9 * 0 + 2)
	private final Button confirm;

	@Position(9 * 0 + 4)
	private final Button item;

	@Position(9 * 0 + 6)
	private final Button deny;

	private ConfirmMenu(Player player, ShopItem item, ApolloPlayer data) {
		this.setSize(9);
		this.setViewer(player);
		this.setTitle(PlayerShopMenuSettings.confirmMenuTitle);

		this.shopItem = item;
		this.data = data;

		this.confirm = new Button() {
			@Override
			public void onClickedInMenu(Player player, Menu menu, ClickType click) {

				ApolloPlayer seller = ApolloPlayer.from(PlayerShopIndividualMenu.this.seller.getUuid());
				Economy economy = VaultHook.getEconomy();
				double balance = economy.getBalance(player);

				if (item.getPrice() > balance && !player.isOp()) {
					PlayerShopIndividualMenu.this.refreshAndRestart("&cInsufficient balance");

					return;
				}

				if (!seller.getShopItems().contains(shopItem)) {
					PlayerShopIndividualMenu.this.refreshAndRestart("&cItem is no longer listed");

					return;
				}

				shopItem.give(player);
				seller.removeShopItem(shopItem);

				Player sellerPlayer = Remain.getPlayerByUUID(seller.getUuid());

				economy.withdrawPlayer(player, item.getPrice());
				economy.depositPlayer(sellerPlayer, item.getPrice());

				if (sellerPlayer == null) {
					Database.getInstance().update(seller.getUsername(), "Player_Shop", data.shopItemsToMap().toJson().replace("'", "''"), username -> {
						PlayerShopIndividualMenu.this.refreshAndRestart("&aSuccessfully purchased item");
					});

					return;
				}

				ItemStack shopItemStack = shopItem.getItem();
				ItemMeta itemMeta = shopItemStack.getItemMeta();

				Common.tell(sellerPlayer, player.getName() + " has purchased " + shopItemStack.getAmount() + " " + (!itemMeta.getDisplayName().isEmpty() ? itemMeta.getDisplayName() : ItemUtil.bountifyCapitalized(shopItemStack.getType()).toLowerCase()) + " &ffor $" + NumberFormat.getInstance().format(shopItem.getPrice()) + " from your player shop!");
				PlayerShopIndividualMenu.this.refreshAndRestart("&aSuccessfully purchased item");
			}

			@Override
			public ItemStack getItem() {
				return ItemCreator.of(PlayerShopMenuSettings.confirmMaterial, PlayerShopMenuSettings.confirmTitle).make();
			}
		};

		this.item = Button.makeDummy(ItemCreator.of(item.getItem()).lore(getLoreItem()));

		this.deny = new Button() {
			@Override
			public void onClickedInMenu(Player player, Menu menu, ClickType click) {
				PlayerShopIndividualMenu.this.refreshAndRestart("&cBacked out of purchase");
			}

			@Override
			public ItemStack getItem() {
				return ItemCreator.of(PlayerShopMenuSettings.denyMaterial, PlayerShopMenuSettings.denyTitle).make();
			}
		};
	}

	private List<String> getLoreItem() {
		List<String> addedLore = new ArrayList<>();

		for (String line : PlayerShopMenuSettings.confirmMenuLore)
			addedLore.add(line
					.replace("%playershop_itemprice%", NumberFormat.getInstance().format(shopItem.getPrice())));

		return addedLore;
	}

	@Override
	public ItemStack getItemAt(int slot) {
		return ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE, " ").make();
	}
}
}