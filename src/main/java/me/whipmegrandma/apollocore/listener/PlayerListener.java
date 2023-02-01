package me.whipmegrandma.apollocore.listener;

import lombok.Getter;
import me.whipmegrandma.apollocore.database.Database;
import me.whipmegrandma.apollocore.model.PlayerCache;
import me.whipmegrandma.apollocore.util.PersonalPickaxeUtil;
import me.whipmegrandma.apollocore.util.VaultEcoUtil;
import me.whipmegrandma.apollocore.util.WorldGuardUtil;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.model.Tuple;

import java.util.HashMap;
import java.util.UUID;

@AutoRegister
public final class PlayerListener implements Listener {
	@Getter
	private static HashMap<UUID, Tuple<Integer, Double>> blocksBroken = new HashMap<>();

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		Database.getInstance().load(player, cache -> {

			if (cache == null) {
				PlayerCache.from(player).addToCache();
				Database.getInstance().save(player, non -> {
				});

				PersonalPickaxeUtil.update(player);

				return;
			}

			cache.addToCache();

			PersonalPickaxeUtil.update(player);
		});
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		Database.getInstance().save(player, cache -> PlayerCache.from(player).removeFromCache());
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		
		if (WorldGuardUtil.testBuild(block.getLocation(), player)) {
			PlayerCache.from(player).increaseBlocksBroken();
			VaultEcoUtil.sell(player, block);
		}
	}
}
