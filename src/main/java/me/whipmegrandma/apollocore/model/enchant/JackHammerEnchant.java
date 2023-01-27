package me.whipmegrandma.apollocore.model.enchant;

import lombok.Getter;
import me.whipmegrandma.apollocore.model.IntermediateEnchant;
import org.bukkit.event.block.BlockBreakEvent;

public final class JackHammerEnchant extends IntermediateEnchant {

	@Getter
	private final static JackHammerEnchant instance = new JackHammerEnchant();

	private JackHammerEnchant() {
		super("JACK_HAMMER", "Jack Hammer", 1);
	}

	@Override
	protected void onBreakBlock(int level, BlockBreakEvent event) {

	}
}
