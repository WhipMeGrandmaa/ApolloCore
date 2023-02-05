package me.whipmegrandma.apollocore;

import me.whipmegrandma.apollocore.database.Database;
import me.whipmegrandma.apollocore.hook.EffectLibHook;
import me.whipmegrandma.apollocore.hook.PapiHook;
import me.whipmegrandma.apollocore.hook.VaultHook;
import me.whipmegrandma.apollocore.listener.MineBombListener;
import me.whipmegrandma.apollocore.listener.PlayerListener;
import me.whipmegrandma.apollocore.manager.TaskManager;
import me.whipmegrandma.apollocore.menu.PersonalPickaxeEnchantsMenu;
import me.whipmegrandma.apollocore.model.ApolloPlayer;
import me.whipmegrandma.apollocore.model.MineBomb;
import me.whipmegrandma.apollocore.model.Rank;
import me.whipmegrandma.apollocore.settings.PriceSettings;
import me.whipmegrandma.apollocore.util.PersonalShopUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.model.HookManager;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.mineacademy.fo.remain.Remain;

public final class ApolloCore extends SimplePlugin {

	@Override
	protected void onPluginStart() {
	}

	@Override
	protected void onReloadablesStart() {
		TaskManager.restart();
		this.loadSettings();
		this.registerHooks();
		this.loadPlayerShops();
	}

	@Override
	protected void onPluginPreReload() {
	}

	@Override
	protected void onPluginStop() {
		MineBombListener.getCooldown().clear();
		EffectLibHook.disable();
		PlayerListener.getBlocksBroken().clear();

		for (Player player : Remain.getOnlinePlayers())
			Database.getInstance().save(player, ApolloPlayer::removeFromCache);
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

		EffectLibHook.restart();
	}

	private void loadPlayerShops() {
		Database.getInstance().loadAll(data -> {
			PersonalShopUtil.filter(data);
			PersonalShopUtil.addAllToCache(data);
		});
	}

	public static ApolloCore getInstance() {
		return (ApolloCore) SimplePlugin.getInstance();
	}
}
