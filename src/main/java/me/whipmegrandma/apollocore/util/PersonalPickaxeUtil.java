package me.whipmegrandma.apollocore.util;

import me.whipmegrandma.apollocore.enums.CompMetadataTags;
import me.whipmegrandma.apollocore.model.ApolloPlayer;
import me.whipmegrandma.apollocore.settings.PersonalPickaxeSettings;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.List;
import java.util.Map;

public class PersonalPickaxeUtil {

	public static void update(Player player) {

		String replacedName = PlaceholderUtil.set(player, PersonalPickaxeSettings.name);
		List<String> replacedLore = PlaceholderUtil.set(player, PersonalPickaxeSettings.lore);

		ItemCreator unfinishedPickaxe = ItemCreator.of(PersonalPickaxeSettings.material)
				.glow(PersonalPickaxeSettings.glow)
				.tag(CompMetadataTags.PICKAXE.toString(), CompMetadataTags.PICKAXE.toString());

		ItemStack pickaxe = updateEnchants(unfinishedPickaxe, player)
				.name(replacedName)
				.lore(replacedLore)
				.make();

		removeOldPickaxe(player);

		player.getInventory().setItem(PersonalPickaxeSettings.hotbarSlot, pickaxe);
	}

	private static ItemCreator updateEnchants(ItemCreator unfinishedPickaxe, Player player) {

		for (Map.Entry<Enchantment, Integer> entry : ApolloPlayer.from(player).getEnchantments().entrySet()) {
			Enchantment enchantment = entry.getKey();
			int level = entry.getValue();

			unfinishedPickaxe.enchant(enchantment, level);
		}

		return unfinishedPickaxe;
	}

	private static void removeOldPickaxe(Player player) {
		Inventory inventory = player.getInventory();

		for (int i = 0; i < inventory.getSize(); i++)
			if (i != PersonalPickaxeSettings.hotbarSlot)
				inventory.setItem(i, ItemCreator.of(CompMaterial.AIR).make());
	}
}
