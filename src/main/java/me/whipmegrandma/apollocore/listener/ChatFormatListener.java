package me.whipmegrandma.apollocore.listener;

import me.clip.placeholderapi.PlaceholderAPI;
import me.whipmegrandma.apollocore.settings.ChatSettings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.model.HookManager;
import org.mineacademy.fo.remain.Remain;

@AutoRegister
public final class ChatFormatListener implements Listener {

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		event.setCancelled(true);

		Player player = event.getPlayer();
		String message = event.getMessage();

		String replacePlaceholders = HookManager.isPlaceholderAPILoaded() ? PlaceholderAPI.setPlaceholders(player, Common.colorize(ChatSettings.chatFormat)) : ChatSettings.chatFormat;
		String formatted = replacePlaceholders.replace("%player_chat%", message);

		for (Player onlinePlayer : Remain.getOnlinePlayers())
			Common.tellNoPrefix(onlinePlayer, formatted);

		Common.logNoPrefix(formatted);
	}
}
