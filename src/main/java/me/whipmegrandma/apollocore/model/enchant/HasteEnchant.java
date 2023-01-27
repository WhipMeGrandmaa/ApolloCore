package me.whipmegrandma.apollocore.model.enchant;

import lombok.Getter;
import me.whipmegrandma.apollocore.model.IntermediateEnchant;
import org.bukkit.potion.PotionEffectType;

public final class HasteEnchant extends IntermediateEnchant {

	@Getter
	private final static HasteEnchant instance = new HasteEnchant();

	private HasteEnchant() {
		super("HASTE", "Haste", 5, PotionEffectType.FAST_DIGGING);
	}
}
