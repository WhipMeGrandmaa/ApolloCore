package me.whipmegrandma.apollocore.model.enchant;

import lombok.Getter;
import me.whipmegrandma.apollocore.model.IntermediateEnchant;
import org.bukkit.event.block.BlockBreakEvent;

public final class TokenGreedEnchant extends IntermediateEnchant {

	@Getter
	private final static TokenGreedEnchant instance = new TokenGreedEnchant();

	private TokenGreedEnchant() {
		super("TOKEN_GREED", "Token Greed", 1);
	}

	@Override
	protected void onBreakBlock(int level, BlockBreakEvent event) {

	}
}
