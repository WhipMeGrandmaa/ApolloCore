package me.whipmegrandma.apollocore.hook;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {
	@Getter
	private static Economy economy;

	public static void load() {
		RegisteredServiceProvider<Economy> provider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);

		if (provider == null)
			return;

		economy = provider.getProvider();
	}
}
