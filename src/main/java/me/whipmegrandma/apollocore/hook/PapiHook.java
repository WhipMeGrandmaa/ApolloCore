package me.whipmegrandma.apollocore.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.whipmegrandma.apollocore.model.ApolloPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;

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
			return ApolloPlayer.from(player).getRank() != null ? ApolloPlayer.from(player).getRank().getRankFormatted() : "";

		if ("rankraw".equals(params))
			return ApolloPlayer.from(player).getRank() != null ? ApolloPlayer.from(player).getRank().getName() : "";

		if ("tokens".equals(params))
			return ApolloPlayer.from(player).getTokens().toString();

		if ("tokensformatted".equals(params))
			return NumberFormat.getInstance().format(ApolloPlayer.from(player).getTokens());

		if ("blocksbroken".equals(params))
			return ApolloPlayer.from(player).getBlocksBroken().toString();

		return null;
	}
}
