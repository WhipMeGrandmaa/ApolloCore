package me.whipmegrandma.apollocore.task;

import me.whipmegrandma.apollocore.model.enchant.BlackholeEnchant;
import me.whipmegrandma.apollocore.util.VaultEcoUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.model.SimpleSound;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.Remain;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class BlackholeSellTask extends BukkitRunnable {

	private Player player;
	private Block block;
	private UUID uuid;
	private List<FallingBlock> fallingBlocks;
	private World world;
	private Location temporaryBlockLocation;
	private SimpleSound onBlackholeDisappear;

	public BlackholeSellTask(Player player, Block block, List<FallingBlock> fallingBlocks, SimpleSound onBlackholeDisappear) {
		this.player = player;
		this.block = block;
		this.uuid = player.getUniqueId();
		this.fallingBlocks = fallingBlocks;
		this.world = player.getWorld();
		this.temporaryBlockLocation = new Location(this.world, 0, this.world.getMinHeight(), 0);
		this.onBlackholeDisappear = onBlackholeDisappear;
	}

	@Override
	public void run() {
		if (fallingBlocks.isEmpty()) {
			cancel();
			BlackholeEnchant.removeCancelledTasks(this.uuid, false);

			if (this.onBlackholeDisappear != null)
				this.onBlackholeDisappear.play(block.getLocation());
		}

		for (Iterator<FallingBlock> iterator = fallingBlocks.listIterator(); iterator.hasNext(); ) {
			FallingBlock fallingBlock = iterator.next();

			if (fallingBlock.getLocation().distance(block.getLocation()) < 2) {
				world.setType(temporaryBlockLocation, fallingBlock.getBlockData().getMaterial());

				Block blockTemporary = world.getBlockAt(temporaryBlockLocation);

				VaultEcoUtil.sell(player, blockTemporary);

				fallingBlock.remove();
				Remain.setTypeAndData(block, CompMaterial.AIR);
				iterator.remove();
			}
		}
	}
}
