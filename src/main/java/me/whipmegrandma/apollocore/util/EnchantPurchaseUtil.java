package me.whipmegrandma.apollocore.util;

import me.whipmegrandma.apollocore.model.IntermediateEnchant;
import me.whipmegrandma.apollocore.model.PlayerCache;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.mineacademy.fo.remain.CompSound;

public class EnchantPurchaseUtil {

	public static PurchaseEnum buy(Player player, Enchantment enchantment, int price, int maxLevelDefaultEnchant) {
		PlayerCache cache = PlayerCache.from(player);
		int balance = cache.getTokens();
		int level = cache.getEnchantLevel(enchantment);

		if (price > balance) {
			CompSound.FIRE.play(player);

			return PurchaseEnum.INSUFFICIENT_BALANCE;
		}

		if (enchantment instanceof IntermediateEnchant && level >= enchantment.getMaxLevel()) {
			CompSound.FIRE.play(player);

			return PurchaseEnum.MAX_LEVEL;
		}

		if (!(enchantment instanceof IntermediateEnchant) && level >= maxLevelDefaultEnchant) {
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
