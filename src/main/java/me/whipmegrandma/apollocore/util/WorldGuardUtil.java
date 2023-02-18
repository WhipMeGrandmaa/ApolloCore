package me.whipmegrandma.apollocore.util;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.mineacademy.fo.model.HookManager;

public class WorldGuardUtil {

	private static WorldGuardPlugin instance;

	public static boolean testBuild(Location block, Player player) {

		if (HookManager.isWorldGuardLoaded()) {

			LocalPlayer localPlayer = instance.wrapPlayer(player);
			com.sk89q.worldedit.util.Location locationAdapted = BukkitAdapter.adapt(block);
			RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
			RegionQuery query = regionContainer.createQuery();

			if (player.isOp())
				return true;

			return query.testBuild(locationAdapted, localPlayer, Flags.BLOCK_BREAK);
		} else
			return true;
	}

	public static void load() {
		instance = WorldGuardPlugin.inst();
	}
}
