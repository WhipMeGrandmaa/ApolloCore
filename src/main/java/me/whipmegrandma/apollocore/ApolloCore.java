package me.whipmegrandma.apollocore;

import com.github.zandy.playerborderapi.api.PlayerBorderAPI;
import me.whipmegrandma.apollocore.database.Database;
import me.whipmegrandma.apollocore.hook.EffectLibHook;
import me.whipmegrandma.apollocore.hook.PapiHook;
import me.whipmegrandma.apollocore.hook.VaultHook;
import me.whipmegrandma.apollocore.listener.MineBombListener;
import me.whipmegrandma.apollocore.listener.PlayerListener;
import me.whipmegrandma.apollocore.manager.MineWorldManager;
import me.whipmegrandma.apollocore.manager.TaskManager;
import me.whipmegrandma.apollocore.menu.PersonalPickaxeEnchantsMenu;
import me.whipmegrandma.apollocore.model.ApolloPlayer;
import me.whipmegrandma.apollocore.model.MineBomb;
import me.whipmegrandma.apollocore.model.Rank;
import me.whipmegrandma.apollocore.settings.MineSettings;
import me.whipmegrandma.apollocore.settings.PriceSettings;
import me.whipmegrandma.apollocore.util.PersonalPickaxeUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.model.HookManager;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.mineacademy.fo.remain.Remain;

import java.util.Iterator;

public final class ApolloCore extends SimplePlugin {

	@Override
	protected void onPluginStart() {
	}

	@Override
	protected void onReloadablesStart() {
		MineSettings.load();
		TaskManager.restart();
		MineWorldManager.load();

		this.loadSettings();
		this.registerHooks();
		this.loadData();
		this.updatePickaxe();
	}

	@Override
	protected void onPluginPreReload() {
	}

	@Override
	protected void onPluginStop() {
		MineBombListener.getCooldown().clear();
		EffectLibHook.disable();
		PlayerListener.getBlocksBroken().clear();
		PlayerBorderAPI.getInstance().removeBorders();

		for (Player player : Remain.getOnlinePlayers())
			Database.getInstance().save(player, ApolloPlayer::removeFromCache);
	}

	private void updatePickaxe() {
		for (Player player : Remain.getOnlinePlayers())
			PersonalPickaxeUtil.update(player);
	}

	private void loadSettings() {
		PersonalPickaxeEnchantsMenu.loadMenus();
		Rank.loadRanks();
		MineBomb.loadBombs();
		PriceSettings.loadPrices();
	}

	private void registerHooks() {
		if (HookManager.isPlaceholderAPILoaded())
			new PapiHook().register();
		else
			Common.log("PlaceholderAPI not loaded. Some features are disabled.");

		if (HookManager.isVaultLoaded())
			VaultHook.load();

		else {
			Common.log("Vault is not loaded. Disabling plugin.");

			Bukkit.getPluginManager().disablePlugin(ApolloCore.getInstance());
		}

		if (!HookManager.isWorldEditLoaded()) {
			Common.log("WorldEdit is not loaded. Disabling plugin.");

			Bukkit.getPluginManager().disablePlugin(ApolloCore.getInstance());
		}

		EffectLibHook.restart();
	}

	private void loadData() {
		Database.getInstance().loadAll(data -> {

			for (Iterator<ApolloPlayer> iterator = data.listIterator(); iterator.hasNext(); ) {
				ApolloPlayer player = iterator.next();

				if (player.getNumberOfShopItems() <= 0 && player.getMine() == null && Remain.getPlayerByUUID(player.getUuid()) == null)
					iterator.remove();
			}

			data.sort((playerOne, playerTwo) -> playerTwo.getNewestShopItemTime().compareTo(playerOne.getNewestShopItemTime()));

			ApolloPlayer.addAllToCache(data);
		});
	}

	public static ApolloCore getInstance() {
		return (ApolloCore) SimplePlugin.getInstance();
	}
}
