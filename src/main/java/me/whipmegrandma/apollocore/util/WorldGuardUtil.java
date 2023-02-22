package me.whipmegrandma.apollocore.util;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.*;
import me.whipmegrandma.apollocore.manager.MineWorldManager;
import me.whipmegrandma.apollocore.model.ApolloPlayer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.mineacademy.fo.model.HookManager;
import org.mineacademy.fo.region.Region;

import java.util.UUID;

public class WorldGuardUtil {

	private static final String regionPrefix = "Mine_Region_";

	public static boolean testBuild(Location block, Player player) {

		if (HookManager.isWorldGuardLoaded()) {

			LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
			com.sk89q.worldedit.util.Location locationAdapted = BukkitAdapter.adapt(block);
			RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
			RegionQuery query = regionContainer.createQuery();

			if (player.isOp())
				return true;

			return query.testBuild(locationAdapted, localPlayer, Flags.BLOCK_BREAK);
		} else
			return true;
	}

	public static void createMineRegion(ApolloPlayer owner, Region mineRegion) {
		RegionManager regions = getRegions(mineRegion.getWorld());

		BlockVector3 primary = WorldEditUtil.toWorldEditVector(mineRegion.getPrimary());
		BlockVector3 secondary = WorldEditUtil.toWorldEditVector(mineRegion.getSecondary());

		ProtectedRegion regionWorldGuard = new ProtectedCuboidRegion(regionPrefix + owner.getUuid(), primary, secondary);

		regionWorldGuard.setPriority(1);
		regions.addRegion(regionWorldGuard);

		addMemberToMineRegion(owner, owner.getUuid());
	}

	public static void deleteMineRegion(ApolloPlayer owner, Region mineRegion) {
		RegionManager regions = getRegions(mineRegion.getWorld());

		regions.removeRegion(regionPrefix + owner.getUuid());
	}

	public static void addMemberToMineRegion(ApolloPlayer owner, UUID uuid) {
		RegionManager regions = getRegions(MineWorldManager.getMineWorld());
		ProtectedRegion region = regions.getRegion(regionPrefix + owner.getUuid());

		DefaultDomain defaultDomain = region.getMembers();
		defaultDomain.addPlayer(uuid);
	}

	public static void kickMemberFromMineRegion(ApolloPlayer owner, UUID uuid) {
		RegionManager regions = getRegions(MineWorldManager.getMineWorld());
		ProtectedRegion region = regions.getRegion(regionPrefix + owner.getUuid());

		DefaultDomain defaultDomain = region.getMembers();
		defaultDomain.removePlayer(uuid);
	}

	public static void setGlobalRegion(World world) {
		RegionManager regions = getRegions(world);

		GlobalProtectedRegion globalRegion = new GlobalProtectedRegion("__global__");

		globalRegion.setFlag(Flags.ICE_FORM, StateFlag.State.DENY);
		globalRegion.setFlag(Flags.ICE_MELT, StateFlag.State.DENY);
		globalRegion.setFlag(Flags.INVINCIBILITY, StateFlag.State.ALLOW);
		globalRegion.setFlag(Flags.FEED_AMOUNT, 20);
		globalRegion.setFlag(Flags.LEAF_DECAY, StateFlag.State.DENY);
		globalRegion.setFlag(Flags.INTERACT, StateFlag.State.ALLOW);
		globalRegion.setFlag(Flags.CHEST_ACCESS, StateFlag.State.DENY);
		globalRegion.setFlag(Flags.PISTONS, StateFlag.State.DENY);
		globalRegion.setFlag(Flags.PVP, StateFlag.State.DENY);
		globalRegion.setFlag(Flags.MOB_SPAWNING, StateFlag.State.DENY);
		globalRegion.setFlag(Flags.MOB_DAMAGE, StateFlag.State.DENY);
		globalRegion.setFlag(Flags.FIRE_SPREAD, StateFlag.State.DENY);
		globalRegion.setFlag(Flags.ENDERPEARL, StateFlag.State.DENY);
		globalRegion.setFlag(Flags.ITEM_DROP, StateFlag.State.ALLOW);
		globalRegion.setFlag(Flags.BLOCK_PLACE, StateFlag.State.DENY);
		globalRegion.setFlag(Flags.BUILD, StateFlag.State.DENY);
		globalRegion.setFlag(Flags.FALL_DAMAGE, StateFlag.State.DENY);
		globalRegion.setFlag(Flags.TNT, StateFlag.State.DENY);

		globalRegion.setPriority(0);
		regions.addRegion(globalRegion);
	}

	private static RegionManager getRegions(World world) {
		BukkitWorld bukkitWorld = new BukkitWorld(world);
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

		return container.get(bukkitWorld);
	}
}
