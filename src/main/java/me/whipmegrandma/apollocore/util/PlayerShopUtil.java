package me.whipmegrandma.apollocore.util;

import me.whipmegrandma.apollocore.enums.SortedBy;
import me.whipmegrandma.apollocore.model.ApolloPlayer;
import me.whipmegrandma.apollocore.model.ShopItem;

import java.util.Iterator;
import java.util.List;

public class PlayerShopUtil {

	public static void sortShops(SortedBy sort, List<ApolloPlayer> data) {
		switch (sort) {
			case NEWEST_ITEM:

				data.sort((playerOne, playerTwo) -> playerTwo.getNewestShopItemTime().compareTo(playerOne.getNewestShopItemTime()));
				break;
			case MOST_ITEMS:

				data.sort((playerOne, playerTwo) -> playerTwo.getNumberOfShopItems().compareTo(playerOne.getNumberOfShopItems()));
				break;
		}
	}

	public static void sortItems(SortedBy.Individual sort, List<ShopItem> data) {
		switch (sort) {
			case PRICE_DESCENDING:

				data.sort((shopItemOne, shopItemTwo) -> shopItemTwo.getPrice().compareTo(shopItemOne.getPrice()));
				break;
			case PRICE_ASCENDING:

				data.sort((shopItemOne, shopItemTwo) -> shopItemOne.getPrice().compareTo(shopItemTwo.getPrice()));
				break;
			case NEWEST:

				data.sort((shopItemOne, shopItemTwo) -> shopItemTwo.getTimeMade().compareTo(shopItemOne.getTimeMade()));
				break;
			case OLDEST:

				data.sort((shopItemOne, shopItemTwo) -> shopItemOne.getTimeMade().compareTo(shopItemTwo.getTimeMade()));
				break;
		}
	}

	public static void filter(List<ApolloPlayer> data) {
		for (Iterator<ApolloPlayer> iterator = data.listIterator(); iterator.hasNext(); ) {
			ApolloPlayer player = iterator.next();

			if (player.getNumberOfShopItems() <= 0)
				iterator.remove();
		}

		data.sort((playerOne, playerTwo) -> playerTwo.getNewestShopItemTime().compareTo(playerOne.getNewestShopItemTime()));
	}

	public static List<ApolloPlayer> getAllDataFiltered() {
		List<ApolloPlayer> data = ApolloPlayer.getAllCached();

		PlayerShopUtil.filter(data);

		return data;
	}
}
