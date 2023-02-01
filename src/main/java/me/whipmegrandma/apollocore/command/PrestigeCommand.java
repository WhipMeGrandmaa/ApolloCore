package me.whipmegrandma.apollocore.command;

import me.whipmegrandma.apollocore.hook.VaultHook;
import me.whipmegrandma.apollocore.model.PlayerCache;
import me.whipmegrandma.apollocore.model.Rank;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommand;

import java.text.NumberFormat;
import java.util.List;

@AutoRegister
public final class PrestigeCommand extends SimpleCommand {

	public PrestigeCommand() {
		super("prestige");

		this.setPermission("apollocore.command.prestige");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		Player player = getPlayer();
		PlayerCache cache = PlayerCache.from(player);
		Rank rank = cache.getRank();

		checkBoolean(rank.getUpgradeType() == Rank.UpgradeType.PRESTIGE, "You must use '/rankup'.");

		Rank.UpgradeResult result = rank.upgrade(player);

		switch (result) {
			case INSUFFICIENT_BALANCE:
				Economy economy = VaultHook.getEconomy();
				double balance = economy.getBalance(player);
				double difference = rank.getUpgradePrice() - balance;

				tell("Insufficient funds. Missing: $" + NumberFormat.getInstance().format(difference));

				break;
			case MAX_RANK:
				tell("You've already reached the max rank!");

				break;
			case SUCCESS:
				tell("Successfully prestiged to " + cache.getRank().getName() + " for $" + NumberFormat.getInstance().format(rank.getUpgradePrice()) + "!");
		}
	}

	@Override
	protected List<String> tabComplete() {
		return NO_COMPLETE;
	}
}
