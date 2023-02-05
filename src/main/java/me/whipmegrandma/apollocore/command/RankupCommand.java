package me.whipmegrandma.apollocore.command;

import me.whipmegrandma.apollocore.hook.VaultHook;
import me.whipmegrandma.apollocore.model.ApolloPlayer;
import me.whipmegrandma.apollocore.model.Rank;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommand;

import java.util.List;

@AutoRegister
public final class RankupCommand extends SimpleCommand {

	public RankupCommand() {
		super("rankup");

		this.setPermission("apollocore.command.rankup");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		Player player = getPlayer();
		ApolloPlayer cache = ApolloPlayer.from(player);
		Rank rank = cache.getRank();

		checkBoolean(rank.getUpgradeType() == Rank.UpgradeType.RANKUP, "You must use the '/prestige'.");

		Rank.UpgradeResult result = rank.upgrade(player);

		switch (result) {
			case INSUFFICIENT_BALANCE:
				Economy economy = VaultHook.getEconomy();
				double balance = economy.getBalance(player);
				double difference = rank.getUpgradePrice() - balance;

				tell("Insufficient funds. Missing: $" + difference);

				break;
			case MAX_RANK:
				tell("You've already reached the max rank!");

				break;
			case SUCCESS:
				tell("You successfully ranked up to " + cache.getRank().getName() + " for $" + rank.getUpgradePrice() + "!");
		}
	}

	@Override
	protected List<String> tabComplete() {
		return NO_COMPLETE;
	}
}
