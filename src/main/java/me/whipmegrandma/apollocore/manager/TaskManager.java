package me.whipmegrandma.apollocore.manager;

import me.whipmegrandma.apollocore.task.EnchantTask;
import org.bukkit.scheduler.BukkitTask;
import org.mineacademy.fo.Common;

import java.util.HashMap;
import java.util.Map;

public class TaskManager {

	private static Map<TaskType, BukkitTask> activeTasks = new HashMap<>();

	public static void start() {
		activeTasks.put(TaskType.ENCHANT, Common.runTimer(20, new EnchantTask()));
	}

	public static void stop() {
		for (Map.Entry<TaskType, BukkitTask> entry : activeTasks.entrySet()) {
			TaskType type = entry.getKey();
			BukkitTask task = entry.getValue();

			if (!task.isCancelled())
				continue;

			task.cancel();
			activeTasks.remove(type);
		}
	}

	public static void restart() {
		stop();
		start();
	}

	private enum TaskType {
		ENCHANT
	}

}
