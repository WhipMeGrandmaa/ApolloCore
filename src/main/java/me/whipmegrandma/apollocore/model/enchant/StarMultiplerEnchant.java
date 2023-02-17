package me.whipmegrandma.apollocore.model.enchant;

import lombok.Getter;
import me.whipmegrandma.apollocore.model.IntermediateEnchant;

public final class StarMultiplerEnchant extends IntermediateEnchant {

	@Getter
	private final static StarMultiplerEnchant instance = new StarMultiplerEnchant();

	private StarMultiplerEnchant() {
		super("STAR_MULTIPLIER", "Star Multiplier", Integer.MAX_VALUE);
	}
}
