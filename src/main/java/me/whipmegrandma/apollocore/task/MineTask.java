package me.whipmegrandma.apollocore.task;

import me.whipmegrandma.apollocore.interfaces.Workload;
import me.whipmegrandma.apollocore.manager.TaskManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayDeque;
import java.util.Deque;

public class MineTask extends BukkitRunnable {

	private final static Deque<Workload> workloadDeque = new ArrayDeque<>();

	public static void addWorkload(Workload workload) {
		workloadDeque.add(workload);
	}

	@Override
	public void run() {
		Workload nextLoad = workloadDeque.poll();
		System.out.println("hey");
		if (nextLoad != null)
			nextLoad.compute();

		if (workloadDeque.isEmpty()) {
			TaskManager.removeMineTask();
			this.cancel();
		}
	}
}
