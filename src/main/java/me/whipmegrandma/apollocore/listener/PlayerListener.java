package me.whipmegrandma.apollocore.listener;

import lombok.Getter;
import me.whipmegrandma.apollocore.database.Database;
import me.whipmegrandma.apollocore.model.ApolloPlayer;
import me.whipmegrandma.apollocore.model.Mine;
import me.whipmegrandma.apollocore.model.enchant.BlackholeEnchant;
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
				ApolloPlayer.from(player).addToCache();
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
		UUID uuid = player.getUniqueId();

		Database.getInstance().save(player, cache -> {

			if (cache.getNumberOfShopItems() < 1 && cache.getMine() == null)
				ApolloPlayer.from(player).removeFromCache();
		});

		BlackholeEnchant.removeFallingBlocks(uuid);
		BlackholeEnchant.removeTasks(uuid);
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();

		if (WorldGuardUtil.testBuild(block.getLocation(), player)) {
			ApolloPlayer.from(player).increaseBlocksBroken();

			Mine mine = Mine.getWithinMineRegion(block.getLocation());

			if (mine != null && mine.isPlayerAllowed(player)) {
				VaultEcoUtil.sell(player, block);
				event.setDropItems(false);
			}
		}
	}
}
