package me.whipmegrandma.apollocore.model.enchant;

import lombok.Getter;
import me.whipmegrandma.apollocore.model.IntermediateEnchant;
import me.whipmegrandma.apollocore.model.Mine;
import me.whipmegrandma.apollocore.util.VaultEcoUtil;
import me.whipmegrandma.apollocore.util.WorldGuardUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.mineacademy.fo.region.Region;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.Remain;

import java.util.List;

public final class JackHammerEnchant extends IntermediateEnchant {

	@Getter
	private final static JackHammerEnchant instance = new JackHammerEnchant();

	private JackHammerEnchant() {
		super("JACK_HAMMER", "Jack Hammer", 1);
	}

	@Override
	protected void onBreakBlock(int level, BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		Mine mine = Mine.getWithinMineRegion(block.getLocation());

		if (mine != null && mine.isPlayerAllowed(player)) {
			Region mineRegion = mine.getMineRegion();
			Location primary = mineRegion.getPrimary();
			Location secondary = mineRegion.getSecondary();

			primary.setY(block.getY());
			secondary.setY(block.getY());

			Region region = new Region(primary, secondary);
			List<Block> blocks = region.getBlocks();

			blocks.remove(block);

			for (Block individualBlock : blocks)
				if (WorldGuardUtil.testBuild(individualBlock.getLocation(), player))
					Remain.setTypeAndData(individualBlock, CompMaterial.AIR);

			VaultEcoUtil.sell(player, blocks);
		}
	}
}
