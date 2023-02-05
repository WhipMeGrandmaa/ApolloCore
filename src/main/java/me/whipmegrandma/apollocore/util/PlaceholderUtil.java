package me.whipmegrandma.apollocore.util;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.mineacademy.fo.model.HookManager;

import java.util.List;

public class PlaceholderUtil {

	public static String set(Player player, String message) {
		return HookManager.isPlaceholderAPILoaded() ? PlaceholderAPI.setPlaceholders(player, message) : message;
	}

	public static List<String> set(Player player, List<String> message) {
		return HookManager.isPlaceholderAPILoaded() ? PlaceholderAPI.setPlaceholders(player, message) : message;
	}

	public static String set(String username, String message) {
		return message.replace("%player_name%", username);
	}
}
