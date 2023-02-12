package me.whipmegrandma.apollocore.listener;

import com.github.zandy.playerborderapi.api.PlayerBorderAPI;
import me.whipmegrandma.apollocore.command.mine.MineEditSubCommand;
import me.whipmegrandma.apollocore.model.Mine;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.visual.VisualizedRegion;

import java.util.UUID;

@AutoRegister
public final class MineListener implements Listener {

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		String command = event.getMessage();

		if (uuid.equals(MineEditSubCommand.getInstance().getEditor()) && !command.equalsIgnoreCase("/mine edit") && !command.equalsIgnoreCase("/mine sethome") && !command.equalsIgnoreCase("/mine setcenter")) {
			event.setCancelled(true);

			Common.tell(player, "You must exit the mine editor to use commands!");
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		MineEditSubCommand instance = MineEditSubCommand.getInstance();

		if (uuid.equals(instance.getEditor())) {
			instance.setEditor(null);
			instance.setMineRegion(new VisualizedRegion());

			player.getInventory().setContents(instance.getPreEditInventory());
			player.teleport(instance.getPreEditLocation());
		}
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		Location location = player.getLocation();

		Common.runLater(4, () -> PlayerBorderAPI.getInstance().removeBorder(player));

		if (Mine.isWithinRegion(location))
			Mine.getWithinRegion(location).showBorder(player);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Location location = player.getLocation();

		if (Mine.isWithinRegion(location))
			Mine.getWithinRegion(location).showBorder(player);
	}
}
