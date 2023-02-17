package me.whipmegrandma.apollocore.model.enchant;

import lombok.Getter;
import me.whipmegrandma.apollocore.model.EnchantByLevelSettings;
import me.whipmegrandma.apollocore.model.IntermediateEnchant;
import me.whipmegrandma.apollocore.model.Mine;
import me.whipmegrandma.apollocore.settings.EnchantSettings;
import me.whipmegrandma.apollocore.util.VaultEcoUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.mineacademy.fo.RandomUtil;
import org.mineacademy.fo.region.Region;

import java.util.List;

public final class JackHammerEnchant extends IntermediateEnchant {

	@Getter
	private final static JackHammerEnchant instance = new JackHammerEnchant();

	private JackHammerEnchant() {
		super("JACK_HAMMER", "Jack Hammer", Integer.MAX_VALUE);
	}

	@Override
	protected void onBreakBlock(int level, BlockBreakEvent event) {
		EnchantByLevelSettings settings = EnchantSettings.jackHammerSettings.get(level);

		if (RandomUtil.chanceD(settings.getChance())) {
			Player player = event.getPlayer();
			Block block = event.getBlock();
			Mine mine = Mine.getWithinMineRegion(block.getLocation());

			if (mine == null || !mine.isPlayerAllowed(player))
				return;

			Region mineRegion = mine.getMineRegion();
			Location primary = mineRegion.getPrimary();
			Location secondary = mineRegion.getSecondary();

			primary.setY(block.getY());
			secondary.setY(block.getY());

			Region region = new Region(primary, secondary);
			List<Block> blocks = region.getBlocks();

			blocks.remove(block);

			VaultEcoUtil.sell(player, blocks);

			super.applyEffects(settings, block, blocks);
		}
	}
}
