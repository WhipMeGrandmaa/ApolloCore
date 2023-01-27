package me.whipmegrandma.apollocore.model.enchant;

import lombok.Getter;
import me.whipmegrandma.apollocore.model.IntermediateEnchant;
import org.bukkit.event.block.BlockBreakEvent;

public final class WrathEnchant extends IntermediateEnchant {

	@Getter
	private final static WrathEnchant instance = new WrathEnchant();

	private WrathEnchant() {
		super("WRATH", "Wrath", 1);
	}

	@Override
	protected void onBreakBlock(int level, BlockBreakEvent event) {

	}
}
