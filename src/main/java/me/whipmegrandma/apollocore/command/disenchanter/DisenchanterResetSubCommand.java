package me.whipmegrandma.apollocore.command.disenchanter;

import me.whipmegrandma.apollocore.database.Database;
import me.whipmegrandma.apollocore.model.ApolloPlayer;
import me.whipmegrandma.apollocore.util.PersonalPickaxeUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.HashMap;
import java.util.List;

public class DisenchanterResetSubCommand extends SimpleSubCommand {

	protected DisenchanterResetSubCommand(SimpleCommandGroup parent) {
		super(parent, "reset");

		this.setPermission("apollocore.command.disenchanter.reset");
		this.setUsage("[player]");
	}

	@Override
	protected void onCommand() {

		if (args.length == 0) {
			checkConsole();

			ApolloPlayer.from(getPlayer()).setEnchantments(new HashMap<>());
			super.tell("Your pickaxe enchants have been reset.");

			return;
		}

		String username = args[0];

		Player target = Bukkit.getPlayerExact(username);
		
		if (target != null) {

			ApolloPlayer cache = ApolloPlayer.from(target);
			cache.setEnchantments(new HashMap<>());

			PersonalPickaxeUtil.update(target);

			Common.tell(target, "Your pickaxe enchants have been reset.");

			if (!target.equals(getPlayer()))
				super.tell("You've reset the pickaxe enchants of " + target.getName() + ".");

		} else {

			Database.getInstance().load(username, cache -> {

				if (cache == null) {
					super.tell(username + " has never joined the server before.");

					return;
				}

				String name = cache.getUsername();

				cache.setEnchantments(new HashMap<>());

				Database.getInstance().save(cache, non -> {
				});
				super.tell("You've reset the pickaxe enchants of " + name + ".");
			});
		}
	}

	@Override
	protected List<String> tabComplete() {

		if (args.length == 1)
			return completeLastWordPlayerNames();

		return NO_COMPLETE;
	}
}
