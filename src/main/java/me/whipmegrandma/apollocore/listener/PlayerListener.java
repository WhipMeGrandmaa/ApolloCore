package me.whipmegrandma.apollocore.listener;

import me.whipmegrandma.apollocore.database.Database;
import me.whipmegrandma.apollocore.model.PlayerCache;
import me.whipmegrandma.apollocore.util.PersonalPickaxeUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.mineacademy.fo.annotation.AutoRegister;

@AutoRegister
public final class PlayerListener implements Listener {

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
}
