package me.whipmegrandma.apollocore.command;

import me.whipmegrandma.apollocore.hook.VaultHook;
import me.whipmegrandma.apollocore.model.ApolloPlayer;
import me.whipmegrandma.apollocore.model.Rank;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommand;
import org.mineacademy.fo.model.Triple;

import java.text.NumberFormat;
import java.util.List;

@AutoRegister
public final class MaxrankupCommand extends SimpleCommand {

	public MaxrankupCommand() {
		super("maxrankup|maxr");

		this.setPermission("apollocore.command.maxrankup");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		Player player = getPlayer();
		ApolloPlayer cache = ApolloPlayer.from(player);
		Rank rank = cache.getRank();

		checkBoolean(rank.getUpgradeType() == Rank.UpgradeType.RANKUP, "You must use '/maxprestige'.");

		Triple<Rank.UpgradeResult, Double, Integer> data = Rank.upgradeMax(player, Rank.UpgradeType.RANKUP);
		Rank.UpgradeResult result = data.getFirst();
		double spent = data.getSecond();
		int times = data.getThird();

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
				tell("Successfully ranked up " + NumberFormat.getInstance().format(times) + " time(s) to " + cache.getRank().getName() + " for $" + NumberFormat.getInstance().format(spent) + "!");
		}
	}

	@Override
	protected List<String> tabComplete() {
		return NO_COMPLETE;
	}
}
