package me.whipmegrandma.apollocore.command.disenchanter;

import me.whipmegrandma.apollocore.database.Database;
import me.whipmegrandma.apollocore.manager.EnchantsManager;
import me.whipmegrandma.apollocore.model.ApolloPlayer;
import me.whipmegrandma.apollocore.util.PersonalPickaxeUtil;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.ItemUtil;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class DisenchanterSetSubCommand extends SimpleSubCommand {

	protected DisenchanterSetSubCommand(SimpleCommandGroup parent) {
		super(parent, "set");

		this.setPermission("apollocore.command.disenchanter.set");
		this.setMinArguments(3);
		this.setUsage("<player> <enchant> <level>");
	}

	@Override
	protected void onCommand() {

		String username = args[0];
		String enchantName = args[1];
		Enchantment enchant = EnchantsManager.getByName(enchantName);
		int level = this.findNumber(2, "The level must a number.");

		super.checkBoolean(enchant != null, enchantName.toUpperCase() + " is not a pickaxe enchant.");
		super.checkBoolean(level >= 0, "The level must be greater than 0.");

		Player target = Bukkit.getPlayerExact(username);

		if (target != null) {

			ApolloPlayer cache = ApolloPlayer.from(target);
			cache.setEnchantment(enchant, level);

			PersonalPickaxeUtil.update(target);

			Common.tell(target, "Your pickaxe enchantment " + ItemUtil.bountifyCapitalized(enchantName).toLowerCase() + " has been set to level " + NumberFormat.getInstance().format(level) + ".");

			if (!target.equals(getPlayer()))
				super.tell("The pickaxe enchantment " + ItemUtil.bountifyCapitalized(enchantName).toLowerCase() + " of " + target.getName() + " has been set to level " + NumberFormat.getInstance().format(level) + ".");

		} else {

			Database.getInstance().load(username, cache -> {

				if (cache == null) {
					super.tell(username + " has never joined the server before.");

					return;
				}

				String name = cache.getUsername();

				cache.setEnchantment(enchant, level);

				Database.getInstance().save(cache, non -> {
				});
				super.tell("The pickaxe enchantment " + ItemUtil.bountifyCapitalized(enchantName).toLowerCase() + " of " + name + " has been set to level " + NumberFormat.getInstance().format(level) + ".");
			});
		}
	}

	@Override
	protected List<String> tabComplete() {

		if (args.length == 1)
			return completeLastWordPlayerNames();

		if (args.length == 2) {
			List<String> enchantNames = new ArrayList<>();
			enchantNames.addAll(EnchantsManager.getAllNames());

			return enchantNames;
		}
		return NO_COMPLETE;
	}
}
