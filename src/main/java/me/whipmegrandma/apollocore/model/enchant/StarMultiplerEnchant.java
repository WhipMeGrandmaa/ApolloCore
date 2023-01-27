package me.whipmegrandma.apollocore.model.enchant;

import lombok.Getter;
import me.whipmegrandma.apollocore.model.IntermediateEnchant;
import org.bukkit.event.block.BlockBreakEvent;

public final class StarMultiplerEnchant extends IntermediateEnchant {

	@Getter
	private final static StarMultiplerEnchant instance = new StarMultiplerEnchant();

	private StarMultiplerEnchant() {
		super("STAR_MULTIPLIER", "Star Multiplier", 1);
	}

	@Override
	protected void onBreakBlock(int level, BlockBreakEvent event) {

	}
}
