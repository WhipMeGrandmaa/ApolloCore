package me.whipmegrandma.apollocore.hook;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {
	@Getter
	private static Economy economy;

	public static void load() {
		RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
		
		if (rsp == null)
			return;

		economy = rsp.getProvider();
	}
}
