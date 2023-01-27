package me.whipmegrandma.apollocore.util;

import me.whipmegrandma.apollocore.enums.CompData;
import me.whipmegrandma.apollocore.model.MineBomb;
import org.bukkit.entity.Player;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.menu.model.ItemCreator;

public class MineBombUtil {

	public static void give(Player player, MineBomb bomb, int amount) {
		ItemCreator item = ItemCreator.of(bomb.getMaterial());

		if (bomb.getName() != null)
			item.name(bomb.getName());

		if (bomb.getLore() != null)
			item.lore(bomb.getLore());

		item.glow(bomb.getGlow());
		item.tag(CompData.MINEBOMB.toString(), bomb.getRawName());
		item.amount(amount);

		PlayerUtil.addItemsOrDrop(player, item.make());
	}
}
