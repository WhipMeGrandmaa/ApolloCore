package me.whipmegrandma.apollocore.command.disenchanter;

import me.whipmegrandma.apollocore.database.Database;
import me.whipmegrandma.apollocore.manager.EnchantsManager;
import me.whipmegrandma.apollocore.model.ApolloPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DisenchanterListSubCommand extends SimpleSubCommand {

	protected DisenchanterListSubCommand(SimpleCommandGroup parent) {
		super(parent, "list");

		this.setPermission("apollocore.command.disenchanter.list");
		setUsage("[player]");
	}

	@Override
	protected void onCommand() {

		Player player = getPlayer();
		ApolloPlayer target = ApolloPlayer.from(args.length == 0 ? player.getName() : args[0]);

		if (target != null)
			tellInfo(target);

		else

			Database.getInstance().load(args[0], cache -> {

				if (cache == null) {
					super.tell(args[0] + " has never joined the server before.");

					return;
				}

				tellInfo(cache);
			});
	}

	@Override
	protected List<String> tabComplete() {

		if (args.length == 1)
			return completeLastWordPlayerNames();

		return NO_COMPLETE;
	}

	private void tellInfo(ApolloPlayer target) {
		List<String> info = new ArrayList<>();

		if (target.getEnchantments().isEmpty()) {
			returnTell(target.getUsername() + " has no pickaxe enchantments.");
		}

		info.add("&d&lENCHANTS OF " + target.getUsername());

		for (Map.Entry<Enchantment, Integer> entry : target.getEnchantments().entrySet()) {
			String enchant = EnchantsManager.getName(entry.getKey());
			int level = entry.getValue();

			info.add("&8- &5" + enchant + "&7 level " + level);
		}

		Common.tellNoPrefix(getPlayer(), info);
	}
}
