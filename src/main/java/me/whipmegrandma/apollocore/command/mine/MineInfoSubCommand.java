package me.whipmegrandma.apollocore.command.mine;

import me.whipmegrandma.apollocore.database.Database;
import me.whipmegrandma.apollocore.model.ApolloPlayer;
import me.whipmegrandma.apollocore.model.Mine;
import me.whipmegrandma.apollocore.settings.MineSettings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mineacademy.fo.ChatUtil;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.ItemUtil;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.model.Tuple;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MineInfoSubCommand extends SimpleSubCommand {

	protected MineInfoSubCommand(SimpleCommandGroup parent) {
		super(parent, "info");

		this.setPermission("apollocore.command.mine.info");
		setUsage("[player]");
	}

	@Override
	protected void onCommand() {
		if (args.length > 0)
			checkConsole();

		Player player = getPlayer();
		ApolloPlayer target = ApolloPlayer.from(args.length == 0 ? player.getName() : args[0]);

		if (target != null) {
			Mine mine = target.getMine();

			checkBoolean(mine != null, args.length == 0 ? "You don't own a mine. Use '/mine create' to make one." : target.getUsername() + " doesn't own a mine.");

			List<String> info = new ArrayList<>();
			String username = target.getUsername();

			info.add("&8" + Common.chatLineSmooth());
			info.add(ChatUtil.center("&5 Mine of " + username, '-', 175));
			info.add("&8" + Common.chatLineSmooth());

			List<String> onlineMembers = new ArrayList<>();
			int onlineAmount = 0;

			List<String> allowedPlayerNames = mine.getAllowedPlayerNames();
			allowedPlayerNames.add(username);

			for (String name : allowedPlayerNames)
				if (Bukkit.getPlayerExact(name) != null) {
					onlineMembers.add("&a" + name);
					onlineAmount++;
				} else
					onlineMembers.add("&c" + name);

			info.add("&5&l* &dMembers: " + "&8[&7" + onlineAmount + "&8/&7" + MineSettings.getInstance().getMaxMinePlayerSize() + "&8] " + Common.join(onlineMembers));
			info.add("&5&l* &dTeleportation: &7" + (mine.getCanTeleport() ? "&bPublic" : "&6Members Only"));
			info.add("&5&l*");

			Tuple<Integer, Integer> sizeNow = mine.getDimensionsMine();
			Tuple<Integer, Integer> sizeNext = Mine.getDimensionsMine(target.getRank().getNextRank());

			info.add("&5&l* &dMine Size: &7" + sizeNow.getKey() + "x" + sizeNow.getValue());
			info.add("&5&l* &dNext: &7" + sizeNext.getKey() + "x" + sizeNext.getValue());
			info.add("&5&l*");

			List<Tuple<CompMaterial, Double>> blocksNow = mine.getMineBlocks();
			List<String> blocksNowString = new ArrayList<>();

			for (Tuple<CompMaterial, Double> line : blocksNow)
				blocksNowString.add(ItemUtil.bountifyCapitalized(line.getKey().name()) + " (" + line.getValue() + "%)");

			List<Tuple<CompMaterial, Double>> blocksNext = mine.getMineBlocks(target.getRank().getNextRank());
			List<String> blocksNextString = new ArrayList<>();

			for (Tuple<CompMaterial, Double> line : blocksNext)
				blocksNextString.add(ItemUtil.bountifyCapitalized(line.getKey().name()) + " (" + line.getValue() + "%)");

			info.add("&5&l* &dMine Blocks: &7" + Common.join(blocksNowString));
			info.add("&5&l* &dNext: &7" + Common.join(blocksNextString));
			info.add("&8" + Common.chatLineSmooth());

			Common.tellNoPrefix(getPlayer(), info);
		} else

			Database.getInstance().load(args[0], cache -> {

				if (cache == null) {
					super.tell(args[0] + " has never joined the server before.");

					return;
				}

				String name = cache.getUsername();

				tell(name + " doesn't own a mine.");

			});
	}

	@Override
	protected List<String> tabComplete() {

		if (args.length == 1) {
			List<ApolloPlayer> cached = ApolloPlayer.getAllCached();
			List<String> names = new ArrayList<>();

			for (Iterator<ApolloPlayer> iterator = cached.listIterator(); iterator.hasNext(); ) {
				ApolloPlayer player = iterator.next();

				if (player.getMine() == null)
					iterator.remove();
				else
					names.add(player.getUsername());
			}

			return completeLastWord(names);
		}
		return NO_COMPLETE;
	}
}
