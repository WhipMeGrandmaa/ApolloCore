package me.whipmegrandma.apollocore.model.enchant;

import lombok.Getter;
import me.whipmegrandma.apollocore.model.IntermediateEnchant;
import org.bukkit.event.block.BlockBreakEvent;

public final class SpaceDropletsEnchant extends IntermediateEnchant {

	@Getter
	private final static SpaceDropletsEnchant instance = new SpaceDropletsEnchant();

	private SpaceDropletsEnchant() {
		super("SPACE_DROPLETS", "Space Droplets", 1);
	}

	@Override
	protected void onBreakBlock(int level, BlockBreakEvent event) {

	}
}
