package me.whipmegrandma.apollocore.manager;

import me.whipmegrandma.apollocore.model.IntermediateEnchant;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class EnchantsManager {

	private static Map<String, Enchantment> byName = new HashMap<>();

	static {
		for (Enchantment enchantment : Enchantment.values())
			if (enchantment.getItemTarget() != null && enchantment.getItemTarget().toString().equals("TOOL"))
				register(enchantment.getKey().getKey().toUpperCase(), enchantment);
	}

	public static void register(String name, Enchantment enchantment) {
		byName.put(name, enchantment);
	}

	public static List<PotionEffectType> getAllPotionEffects() {
		List<PotionEffectType> list = new ArrayList<>();

		for (Enchantment enchant : getAllEnchants())
			if (enchant instanceof IntermediateEnchant && ((IntermediateEnchant) enchant).getEffectType() != null)
				list.add(((IntermediateEnchant) enchant).getEffectType());

		return list;
	}

	public static Collection<Enchantment> getAllEnchants() {
		return byName.values();
	}

	public static String getName(Enchantment enchantment) {
		for (Map.Entry<String, Enchantment> entry : byName.entrySet())
			if (enchantment.equals(entry.getValue()))
				return entry.getKey();

		return null;
	}

	public static Enchantment getByName(String name) {
		return byName.get(name.toUpperCase());
	}

	public static Set<String> getAllNames() {
		return byName.keySet();
	}
}
