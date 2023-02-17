package me.whipmegrandma.apollocore.model.enchant;

import lombok.Getter;
import me.whipmegrandma.apollocore.model.EnchantByLevelSettings;
import me.whipmegrandma.apollocore.model.IntermediateEnchant;
import me.whipmegrandma.apollocore.model.Mine;
import me.whipmegrandma.apollocore.settings.EnchantSettings;
import me.whipmegrandma.apollocore.util.VaultEcoUtil;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.mineacademy.fo.RandomUtil;
import org.mineacademy.fo.region.Region;

import java.util.List;

public final class WrathEnchant extends IntermediateEnchant {

	@Getter
	private final static WrathEnchant instance = new WrathEnchant();

	private WrathEnchant() {
		super("WRATH", "Wrath", Integer.MAX_VALUE);
	}

	@Override
	protected void onBreakBlock(int level, BlockBreakEvent event) {
		EnchantByLevelSettings settings = EnchantSettings.wrathSettings.get(level);

		if (RandomUtil.chanceD(settings.getChance())) {
			Player player = event.getPlayer();
			Block block = event.getBlock();
			Mine mine = Mine.getWithinMineRegion(block.getLocation());

			if (mine == null || !mine.isPlayerAllowed(player))
				return;

			Region mineRegion = mine.getMineRegion();

			List<Block> blocks = mineRegion.getBlocks();

			blocks.remove(block);

			VaultEcoUtil.sell(player, blocks, 0.7);

			super.applyEffects(settings, block, blocks);
		}
	}
}
