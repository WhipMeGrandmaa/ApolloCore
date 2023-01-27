package me.whipmegrandma.apollocore.model;

import lombok.Getter;
import me.whipmegrandma.apollocore.manager.EnchantsManager;
import org.bukkit.potion.PotionEffectType;
import org.mineacademy.fo.model.SimpleEnchantment;

@Getter
public abstract class IntermediateEnchant extends SimpleEnchantment {

	private PotionEffectType effectType;

	protected IntermediateEnchant(String name, String displayName, int maxLevel) {
		super(displayName, maxLevel);

		EnchantsManager.register(name, this);
	}

	protected IntermediateEnchant(String name, String displayName, int maxLevel, PotionEffectType effectType) {
		this(name, displayName, maxLevel);

		this.effectType = effectType;
	}
}
