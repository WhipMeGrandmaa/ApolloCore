package me.whipmegrandma.apollocore.task;

import me.whipmegrandma.apollocore.model.enchant.BlackholeEnchant;
import me.whipmegrandma.apollocore.util.VaultEcoUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.model.SimpleSound;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class BlackholeSellTask extends BukkitRunnable {

	private Player player;
	private Location blackholeLocation;
	private UUID uuid;
	private List<FallingBlock> fallingBlocks;
	private World world;
	private SimpleSound onBlackholeDisappear;

	public BlackholeSellTask(Player player, Location blackholeLocation, List<FallingBlock> fallingBlocks, SimpleSound onBlackholeDisappear) {
		this.player = player;
		this.blackholeLocation = blackholeLocation;
		this.uuid = player.getUniqueId();
		this.fallingBlocks = fallingBlocks;
		this.world = player.getWorld();
		this.onBlackholeDisappear = onBlackholeDisappear;
	}

	@Override
	public void run() {
		if (fallingBlocks.isEmpty()) {
			cancel();
			BlackholeEnchant.removeCancelledTasks(this.uuid, false);

			if (this.onBlackholeDisappear != null)
				this.onBlackholeDisappear.play(blackholeLocation);
		}

		for (Iterator<FallingBlock> iterator = fallingBlocks.listIterator(); iterator.hasNext(); ) {
			FallingBlock fallingBlock = iterator.next();

			if (fallingBlock.getLocation().distance(blackholeLocation) < 2) {
				Material type = fallingBlock.getBlockData().getMaterial();

				VaultEcoUtil.sell(player, type);

				fallingBlock.remove();
				iterator.remove();
			}
		}
	}
}
