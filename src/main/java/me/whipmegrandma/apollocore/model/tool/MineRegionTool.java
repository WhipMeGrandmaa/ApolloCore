package me.whipmegrandma.apollocore.model.tool;

import lombok.Getter;
import me.whipmegrandma.apollocore.command.mine.MineEditSubCommand;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.visual.VisualTool;
import org.mineacademy.fo.visual.VisualizedRegion;

@AutoRegister
public final class MineRegionTool extends VisualTool {

	@Getter
	private final static MineRegionTool instance = new MineRegionTool();

	private MineRegionTool() {
	}

	@Override
	protected CompMaterial getBlockMask(Block block, Player player) {
		return CompMaterial.GOLD_BLOCK;
	}

	@Override
	public ItemStack getItem() {
		return ItemCreator
				.of(CompMaterial.NETHERITE_AXE, "&dMine Region Tool", "", "Select the region for", "the mine.")
				.glow(true)
				.make();
	}

	@Override
	protected VisualizedRegion getVisualizedRegion(Player player) {
		return MineEditSubCommand.getInstance().getMineRegion();
	}
}
