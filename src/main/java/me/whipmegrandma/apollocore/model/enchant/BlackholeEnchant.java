package me.whipmegrandma.apollocore.model.enchant;

import lombok.Getter;
import me.whipmegrandma.apollocore.model.EnchantByLevelSettings;
import me.whipmegrandma.apollocore.model.IntermediateEnchant;
import me.whipmegrandma.apollocore.model.Mine;
import me.whipmegrandma.apollocore.settings.EnchantSettings;
import me.whipmegrandma.apollocore.task.BlackholeSellTask;
import me.whipmegrandma.apollocore.util.WorldGuardUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.mineacademy.fo.BlockUtil;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.RandomUtil;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.Remain;

import java.util.*;

public final class BlackholeEnchant extends IntermediateEnchant {

	@Getter
	private final static BlackholeEnchant instance = new BlackholeEnchant();

	@Getter
	private static HashMap<UUID, List<BukkitTask>> tasks = new HashMap<>();

	private BlackholeEnchant() {
		super("BLACK_HOLE", "Black Hole", Integer.MAX_VALUE);
	}

	@Override
	protected void onBreakBlock(int level, BlockBreakEvent event) {
		EnchantByLevelSettings settings = EnchantSettings.blackHoleSettings.get(level);

		if (RandomUtil.chanceD(settings.getChance())) {
			Player player = event.getPlayer();
			UUID uuid = player.getUniqueId();
			Block block = event.getBlock();
			Location blockLocation = block.getLocation();
			Mine mine = Mine.getWithinMineRegion(block.getLocation());

			if (mine == null || !mine.isPlayerAllowed(player))
				return;

			super.applyEffects(settings, block);

			Set<Location> sphereBlocks = BlockUtil.getSphere(blockLocation, settings.getCraterRadius(), false);

			sphereBlocks.removeIf(location -> block.getLocation().equals(location) || !WorldGuardUtil.testBuild(location, player) || CompMaterial.isAir(location.getBlock()) || Mine.getWithinMineRegion(location) == null || !Mine.getWithinMineRegion(location).isPlayerAllowed(player));

			List<FallingBlock> fallingBlockList = new ArrayList<>();

			for (Iterator<Location> iterator = sphereBlocks.iterator(); iterator.hasNext(); ) {
				Location individualLocation = iterator.next();
				Block individualBlock = individualLocation.getBlock();

				FallingBlock fallingBlock = Remain.spawnFallingBlock(individualBlock);
				fallingBlock.setDropItem(false);
				fallingBlock.setGravity(false);
				Remain.setTypeAndData(individualBlock, CompMaterial.AIR);

				Vector velocity = blockLocation.clone().subtract(individualLocation).toVector().multiply(settings.getVelocityTowardsBlackhole());

				fallingBlock.setVelocity(velocity);
				fallingBlockList.add(fallingBlock);
			}

			BukkitTask runnable = Common.runTimer(10, new BlackholeSellTask(player, block, fallingBlockList, settings.getSoundOnBlackholeDisappear()));

			List<BukkitTask> currentTasks = tasks.containsKey(uuid) ? tasks.get(uuid) : new ArrayList<>();
			currentTasks.add(runnable);
			tasks.put(uuid, currentTasks);
		}
	}

	public static void removeTasks(UUID uuid) {
		removeCancelledTasks(uuid, true);
	}

	public static void removeCancelledTasks(UUID uuid, boolean all) {
		List<BukkitTask> currentTasks = tasks.get(uuid);

		if (currentTasks == null)
			return;

		for (Iterator<BukkitTask> iterator = currentTasks.iterator(); iterator.hasNext(); ) {
			BukkitTask next = iterator.next();

			if (!next.isCancelled() && all)
				next.cancel();

			if (next.isCancelled())
				iterator.remove();
		}
		tasks.put(uuid, currentTasks);
	}
}
