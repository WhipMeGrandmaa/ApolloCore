package me.whipmegrandma.apollocore.util;

import me.whipmegrandma.apollocore.model.ApolloPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.mineacademy.fo.remain.CompSound;

public class EnchantPurchaseUtil {

	public static PurchaseEnum buy(Player player, Enchantment enchantment, int price, int maxLevel) {
		ApolloPlayer cache = ApolloPlayer.from(player);
		int balance = cache.getTokens();
		int level = cache.getEnchantLevel(enchantment);

		if (price > balance) {
			CompSound.FIRE.play(player);

			return PurchaseEnum.INSUFFICIENT_BALANCE;
		}

		if (level >= maxLevel) {
			CompSound.FIRE.play(player);

			return PurchaseEnum.MAX_LEVEL;
		}

		CompSound.ANVIL_USE.play(player);

		cache.setEnchantment(enchantment, level + 1);
		cache.setTokens(balance - price);

		PersonalPickaxeUtil.update(player);

		return PurchaseEnum.SUCCESS;
	}

	public enum PurchaseEnum {
		INSUFFICIENT_BALANCE,
		MAX_LEVEL,
		SUCCESS
	}
}
