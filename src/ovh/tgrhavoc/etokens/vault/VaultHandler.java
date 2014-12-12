package ovh.tgrhavoc.etokens.vault;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHandler {
	
	public static boolean isAllowed = false;
	
	public VaultHandler() {
		if (!setupEconomy()){
			Bukkit.getLogger().severe(String.format("\n=============================\n"
					+ "[%s] Error: Could not load vault economy..\n"
					+ "Vault Intergraction failed!!\n"
					+ "If you want to use eTokens with your economy then please make sure that vault is installed\n"
					+ "==============================", "eTokens" ));
			return;
		}
		isAllowed = true;
	}

	public static Economy economy = null;

	private boolean setupEconomy() {
		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = Bukkit.getServer()
				.getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		economy = rsp.getProvider();
		return economy != null;
	}
}
