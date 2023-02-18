package me.whipmegrandma.apollocore.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.FallingBlock;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class BlackholeTaskWrapper {

	private BukkitTask bukkitTask;
	private List<FallingBlock> fallingBlocks;

	public void clearFallingBlocks() {
		for (FallingBlock fallingBlock : fallingBlocks)
			fallingBlock.remove();
	}

	public static BlackholeTaskWrapper of(BukkitTask bukkitTask, List<FallingBlock> fallingBlocks) {
		return new BlackholeTaskWrapper(bukkitTask, fallingBlocks);
	}
}
