package me.whipmegrandma.apollocore.model.enchant;

import lombok.Getter;
import me.whipmegrandma.apollocore.model.IntermediateEnchant;
import org.bukkit.potion.PotionEffectType;

public final class SpeedEnchant extends IntermediateEnchant {

	@Getter
	private final static SpeedEnchant instance = new SpeedEnchant();

	private SpeedEnchant() {
		super("SPEED", "Speed", 5, PotionEffectType.SPEED);
	}
}
