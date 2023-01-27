package me.whipmegrandma.apollocore.util;

import me.clip.placeholderapi.PlaceholderAPI;
import me.whipmegrandma.apollocore.enums.CompData;
import me.whipmegrandma.apollocore.model.PlayerCache;
import me.whipmegrandma.apollocore.settings.PersonalPickaxeSettings;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.model.HookManager;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompMetadata;

import java.util.List;
import java.util.Map;

public class PersonalPickaxeUtil {

	public static int has(Player player) {
		if (!player.getInventory().contains(PersonalPickaxeSettings.material.getMaterial()))
			return -1;

		for (ItemStack itemStack : player.getInventory().getContents())
			if (!CompMaterial.isAir(itemStack) && CompMetadata.hasMetadata(itemStack, "ApolloCore:Pickaxe"))
				return player.getInventory().first(itemStack);

		return -1;
	}

	public static void update(Player player) {
		int slot = has(player);

		String replacedName = HookManager.isPlaceholderAPILoaded() ? PlaceholderAPI.setPlaceholders(player, PersonalPickaxeSettings.name) : PersonalPickaxeSettings.name;
		List<String> replacedLore = HookManager.isPlaceholderAPILoaded() ? PlaceholderAPI.setPlaceholders(player, PersonalPickaxeSettings.lore) : PersonalPickaxeSettings.lore;

		ItemCreator unfinishedPickaxe = ItemCreator.of(PersonalPickaxeSettings.material)
				.glow(PersonalPickaxeSettings.glow)
				.tag(CompData.PICKAXE.toString(), CompData.PICKAXE.toString());

		ItemStack pickaxe = updateEnchants(unfinishedPickaxe, player)
				.name(replacedName)
				.lore(replacedLore)
				.make();

		if (slot == -1)
			PlayerUtil.addItemsOrDrop(player, pickaxe);
		else
			player.getInventory().setItem(slot, pickaxe);
	}

	private static ItemCreator updateEnchants(ItemCreator unfinishedPickaxe, Player player) {

		for (Map.Entry<Enchantment, Integer> entry : PlayerCache.from(player).getEnchantments().entrySet()) {
			Enchantment enchantment = entry.getKey();
			int level = entry.getValue();

			unfinishedPickaxe.enchant(enchantment, level);
		}

		return unfinishedPickaxe;
	}
}
