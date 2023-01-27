package me.whipmegrandma.apollocore.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.whipmegrandma.apollocore.model.PlayerCache;
import me.whipmegrandma.apollocore.model.Rank;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PapiHook extends PlaceholderExpansion {

	@Override
	public @NotNull String getIdentifier() {
		return "apollocore";
	}

	@Override
	public @NotNull String getAuthor() {
		return "WhipMeGrandma";
	}

	@Override
	public @NotNull String getVersion() {
		return "1.0.0";
	}

	@Override
	public boolean canRegister() {
		return true;
	}

	@Override
	public boolean persist() {
		return true;
	}

	@Override
	public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {

		if ("rank".equals(params))
			return PlayerCache.from(player).getRank() != null ? PlayerCache.from(player).getRank().getRankFormatted() : Rank.getFirstRank() != null ? Rank.getFirstRank().getRankFormatted() : "";

		if ("rankraw".equals(params))
			return PlayerCache.from(player).getRank() != null ? PlayerCache.from(player).getRank().getName() : Rank.getFirstRank() != null ? Rank.getFirstRank().getName() : "";

		if ("tokens".equals(params))
			return PlayerCache.from(player).getTokens() != null ? PlayerCache.from(player).getTokens().toString() : "0";

		return null;
	}
}
