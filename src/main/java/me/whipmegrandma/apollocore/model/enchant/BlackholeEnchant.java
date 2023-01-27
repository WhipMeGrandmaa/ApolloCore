package me.whipmegrandma.apollocore.model.enchant;

import lombok.Getter;
import me.whipmegrandma.apollocore.model.IntermediateEnchant;
import org.bukkit.event.block.BlockBreakEvent;

public final class BlackholeEnchant extends IntermediateEnchant {

	@Getter
	private final static BlackholeEnchant instance = new BlackholeEnchant();

	private BlackholeEnchant() {
		super("BLACK_HOLE", "Black Hole", 1);
	}

	@Override
	protected void onBreakBlock(int level, BlockBreakEvent event) {

	}
}
