package ovh.tgrhavoc.etokens;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import ovh.tgrhavoc.etokens.commands.CommandHandler;
import ovh.tgrhavoc.etokens.commands.ShopCommand;
import ovh.tgrhavoc.etokens.listeners.PlayerSave;
import ovh.tgrhavoc.etokens.listeners.TokenListener;
import ovh.tgrhavoc.etokens.sqlite.SQLHandler;
import ovh.tgrhavoc.etokens.vault.VaultHandler;
import ovh.tgrhavoc.etokens.xml.Token;
import ovh.tgrhavoc.etokens.xml.XMLHandler;

public class eTokens extends JavaPlugin {

	SQLHandler sqlHandler = new SQLHandler(this);
	XMLHandler xmlHandler;
	
	VaultHandler vaultHandler = null;

	public HashMap<String, PlayerSave> playerSaveData = new HashMap<String, PlayerSave>();
	
	boolean vaultUsed;

	public boolean canUseVault(){
		return vaultUsed;
	}
	
	public VaultHandler getVaultHandler(){
		return vaultHandler;
	}

	public void onEnable() {
		File hashmapFile = new File(getDataFolder(), "playerdata"
				+ File.separator + "data.dat");
		if (!hashmapFile.exists()){
			hashmapFile.getParentFile().mkdirs();
			try {
				hashmapFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		getDataFolder().mkdirs();
		
		
		vaultHandler = new VaultHandler();
		
		vaultUsed = VaultHandler.isAllowed;
		
		getCommand("token").setExecutor(new CommandHandler(this));
		getCommand("shop").setExecutor(new ShopCommand(this));

		Bukkit.getPluginManager().registerEvents(new TokenListener(this), this);
		
		sqlHandler.init();
		
		try {
			loadData();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		xmlHandler = new XMLHandler(this);
	}
	public SQLHandler getSqlHandler() {
		return sqlHandler;
	}

	public XMLHandler getXmlHandler() {
		return xmlHandler;
	}

	@SuppressWarnings("unchecked")
	private void loadData() throws IOException, ClassNotFoundException {
		File hashmapFile = new File(getDataFolder(), "playerdata"
			+ File.separator + "data.dat");
		FileInputStream fis = new FileInputStream(hashmapFile);
		ObjectInputStream ois = new ObjectInputStream(fis);
		playerSaveData = (HashMap<String, PlayerSave>) ois.readObject();
		ois.close();
		fis.close();
	}

	public void onDisable() {
		try {
			saveData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveData() throws IOException {
		File hashmapFile = new File(getDataFolder(), "playerdata"
				+ File.separator + "data.dat");
		FileOutputStream fos = new FileOutputStream(hashmapFile);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(playerSaveData);
		oos.close();
		fos.close();
	}

	public List<Token> getTokens() {
		return xmlHandler.getTokens();
	}
}
