package me.whipmegrandma.apollocore.manager;

import me.whipmegrandma.apollocore.settings.BlocksBrokenSettings;
import me.whipmegrandma.apollocore.task.BlocksBrokenTask;
import me.whipmegrandma.apollocore.task.EnchantTask;
import org.bukkit.scheduler.BukkitTask;
import org.mineacademy.fo.Common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TaskManager {

	private static Map<TaskType, BukkitTask> activeTasks = new HashMap<>();

	public static void start() {
		activeTasks.put(TaskType.ENCHANT, Common.runTimer(1 * 20, new EnchantTask()));
		activeTasks.put(TaskType.BLOCKS_BROKEN_INFO, Common.runTimer(BlocksBrokenSettings.time * 20, new BlocksBrokenTask()));
	}

	public static void stop() {
		for (Iterator<Map.Entry<TaskType, BukkitTask>> it = activeTasks.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<TaskType, BukkitTask> entry = it.next();
			TaskType type = entry.getKey();
			BukkitTask task = entry.getValue();

			if (!task.isCancelled())
				continue;

			task.cancel();
			it.remove();
		}
	}

	public static void restart() {
		stop();
		start();
	}

	private enum TaskType {
		ENCHANT,
		BLOCKS_BROKEN_INFO
	}

}
