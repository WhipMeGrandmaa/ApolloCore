package me.whipmegrandma.apollocore.task;

import me.whipmegrandma.apollocore.manager.EnchantsManager;
import me.whipmegrandma.apollocore.model.IntermediateEnchant;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.Remain;

import java.util.Map;

public class EnchantTask extends BukkitRunnable {

	@Override
	public void run() {

		for (Player player : Remain.getOnlinePlayers()) {
			ItemStack hand = player.getInventory().getItemInMainHand();

			this.removePotionEffects(player);

			if (!CompMaterial.isAir(hand))
				for (Map.Entry<Enchantment, Integer> enchant : hand.getItemMeta().getEnchants().entrySet())
					if (enchant.getKey() instanceof IntermediateEnchant && ((IntermediateEnchant) enchant.getKey()).getEffectType() != null)
						player.addPotionEffect(new PotionEffect(((IntermediateEnchant) enchant.getKey()).getEffectType(), Integer.MAX_VALUE, enchant.getValue() - 1));
		}
	}

	private void removePotionEffects(Player player) {

		for (PotionEffectType effectType : EnchantsManager.getAllPotionEffects())
			if (player.hasPotionEffect(effectType))
				player.removePotionEffect(effectType);
	}
}
