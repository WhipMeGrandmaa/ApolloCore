package me.whipmegrandma.apollocore.model.enchant;

import lombok.Getter;
import me.whipmegrandma.apollocore.model.ApolloPlayer;
import me.whipmegrandma.apollocore.model.EnchantByLevelSettings;
import me.whipmegrandma.apollocore.model.IntermediateEnchant;
import me.whipmegrandma.apollocore.model.Mine;
import me.whipmegrandma.apollocore.settings.EnchantSettings;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.mineacademy.fo.RandomUtil;
import org.mineacademy.fo.remain.Remain;

import java.text.NumberFormat;

public final class TokenGreedEnchant extends IntermediateEnchant {

	@Getter
	private final static TokenGreedEnchant instance = new TokenGreedEnchant();

	private TokenGreedEnchant() {
		super("TOKEN_GREED", "Token Greed", Integer.MAX_VALUE);
	}

	@Override
	protected void onBreakBlock(int level, BlockBreakEvent event) {
		EnchantByLevelSettings settings = EnchantSettings.tokenGreedSettings.get(level);

		if (RandomUtil.chanceD(settings.getChance())) {
			Player player = event.getPlayer();
			ApolloPlayer apolloPlayer = ApolloPlayer.from(player);
			Block block = event.getBlock();
			Mine mine = Mine.getWithinMineRegion(block.getLocation());

			if (mine == null || !mine.isPlayerAllowed(player))
				return;

			Integer randomTokens = RandomUtil.nextBetween(settings.getMinimumTokens(), settings.getMaximumTokens());
			apolloPlayer.setTokens(randomTokens + apolloPlayer.getTokens());
			Remain.sendActionBar(player, "You found &d" + NumberFormat.getInstance().format(randomTokens) + " &ftokens!");

			super.applyEffects(settings, block);

		}
	}
}
