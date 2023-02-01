package me.whipmegrandma.apollocore.command.tokens;

import me.clip.placeholderapi.PlaceholderAPI;
import me.whipmegrandma.apollocore.enums.CompMetadataTags;
import me.whipmegrandma.apollocore.model.PlayerCache;
import me.whipmegrandma.apollocore.settings.TokensSettings;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.model.HookManager;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class TokensWithdrawSubCommand extends SimpleSubCommand {

	protected TokensWithdrawSubCommand(SimpleCommandGroup parent) {
		super(parent, "withdraw");

		this.setPermission("apollocore.command.tokens.withdraw");
		this.setMinArguments(1);
		this.setUsage("<tokens>");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		int amount = findNumber(0, "The amount to withdraw must be a number.");

		checkBoolean(amount > 0, "The amount to withdraw must be a positive number.");

		PlayerCache cache = PlayerCache.from(getPlayer());
		int tokens = cache.getTokens();

		checkBoolean(tokens >= amount, "Insufficient tokens!");
		checkBoolean(isInventoryFull(), "Your inventory is full!");

		String tokensString = NumberFormat.getInstance().format(amount);

		String replacePlaceholdersName = HookManager.isPlaceholderAPILoaded() ? PlaceholderAPI.setPlaceholders(getPlayer(), TokensSettings.name) : TokensSettings.name;
		List<String> replacePlaceholdersLore = HookManager.isPlaceholderAPILoaded() ? PlaceholderAPI.setPlaceholders(getPlayer(), TokensSettings.lore) : TokensSettings.lore;

		String formattedName = replacePlaceholdersName.replace("%tokens%", tokensString);
		List<String> formattedLore = new ArrayList<>();

		for (String old : replacePlaceholdersLore)
			formattedLore.add(old.replace("%tokens%", tokensString));

		cache.setTokens(tokens - amount);

		ItemCreator.of(TokensSettings.material, formattedName, formattedLore)
				.glow(TokensSettings.glow)
				.tag(CompMetadataTags.WITHDRAW.toString(), String.valueOf(amount))
				.give(getPlayer());

		super.tell("You withdrew " + NumberFormat.getInstance().format(amount) + " tokens.");
	}

	private boolean isInventoryFull() {

		int freeSlots = 36;

		for (ItemStack stack : getPlayer().getInventory().getContents())
			if (stack != null)
				freeSlots -= 1;

		return freeSlots > 0;
	}

	@Override
	protected List<String> tabComplete() {
		return NO_COMPLETE;
	}
}
