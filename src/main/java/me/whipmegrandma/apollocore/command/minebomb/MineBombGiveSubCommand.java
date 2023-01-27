package me.whipmegrandma.apollocore.command.minebomb;

import me.whipmegrandma.apollocore.model.MineBomb;
import me.whipmegrandma.apollocore.util.MineBombUtil;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.List;

public class MineBombGiveSubCommand extends SimpleSubCommand {

	protected MineBombGiveSubCommand(SimpleCommandGroup parent) {
		super(parent, "give");

		this.setPermission("apollocore.command.minebomb.give");
		this.setUsage("[player] <mineBomb> <amount>");
		this.setMinArguments(2);
	}

	@Override
	protected void onCommand() {

		if (args.length == 2) {
			checkConsole();

			MineBomb bomb = MineBomb.findBomb(args[0]);
			int amount = findNumber(1, "The amount must be a number.");

			checkBoolean(amount > 0, "The amount must be a positive full number.");
			checkBoolean(bomb != null, args[0] + " is not a configured mine bomb. To check all available use '/minebomb list'.");

			MineBombUtil.give(getPlayer(), bomb, amount);

			tell("You have received a " + bomb.getRawName() + " mine bomb.");

			return;
		}

		Player target = findPlayer(args[0]);

		MineBomb bomb = MineBomb.findBomb(args[1]);
		int amount = findNumber(2, "The amount must be a number.");

		checkBoolean(amount > 0, "The amount must be a positive full number.");
		checkBoolean(bomb != null, args[1] + " is not a configured mine bomb. To check all available use '/minebomb list'.");

		MineBombUtil.give(target, bomb, amount);

		Common.tell(target, "You have received " + amount + "x " + bomb.getRawName() + " mine bombs.");

		if (!getPlayer().equals(target))
			tell("You gave " + target.getName() + " " + amount + "x " + bomb.getRawName() + " mine bombs.");
	}

	@Override
	protected List<String> tabComplete() {

		if (args.length == 1)
			return completeLastWord(completeLastWordPlayerNames(), MineBomb.bombNames());

		if (args.length == 2 && MineBomb.findBomb(args[0]) == null)
			return completeLastWord(MineBomb.bombNames());

		return NO_COMPLETE;
	}
}
