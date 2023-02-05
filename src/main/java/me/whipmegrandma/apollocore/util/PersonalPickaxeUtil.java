package me.whipmegrandma.apollocore.util;

import me.whipmegrandma.apollocore.enums.CompMetadataTags;
import me.whipmegrandma.apollocore.model.ApolloPlayer;
import me.whipmegrandma.apollocore.settings.PersonalPickaxeSettings;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.menu.model.ItemCreator;
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

		String replacedName = PlaceholderUtil.set(player, PersonalPickaxeSettings.name);
		List<String> replacedLore = PlaceholderUtil.set(player, PersonalPickaxeSettings.lore);

		ItemCreator unfinishedPickaxe = ItemCreator.of(PersonalPickaxeSettings.material)
				.glow(PersonalPickaxeSettings.glow)
				.tag(CompMetadataTags.PICKAXE.toString(), CompMetadataTags.PICKAXE.toString());

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

		for (Map.Entry<Enchantment, Integer> entry : ApolloPlayer.from(player).getEnchantments().entrySet()) {
			Enchantment enchantment = entry.getKey();
			int level = entry.getValue();

			unfinishedPickaxe.enchant(enchantment, level);
		}

		return unfinishedPickaxe;
	}
}
