package ovh.tgrhavoc.etokens.commands;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ovh.tgrhavoc.etokens.eTokens;
import ovh.tgrhavoc.etokens.shop.Shop;

public class ShopCommand implements CommandExecutor{
	eTokens plugin;
	
	File shopsFolder;
	
	public ShopCommand(eTokens t){
		plugin = t;
		shopsFolder = new File(t.getDataFolder() + File.separator + "shops");
		
		if (! shopsFolder.exists() )
			shopsFolder.mkdirs();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (! (sender instanceof Player) ){
			sender.sendMessage(ChatColor.RED + "Sorry, only players can open shops!");
			return true;
		}
		if (args.length == 0){
			sendHelp(sender);
			return true;
		}
		
		if (args[0].equals("help")){
			sendHelp(sender);
			return true;
		}
		
		if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("command")){
			if (!sender.hasPermission("etokens.shop.create")){
				sender.sendMessage(ChatColor.RED +"You don't have permission to execute this command :(");
				return true;
			}			
			if (args.length == 2 && args[0].equalsIgnoreCase("create")){
				try {
					createShopFile(args[1]);
				} catch (IOException e) {
					e.printStackTrace();
				}
				sender.sendMessage(ChatColor.GREEN +"Created the shop '" + args[1] + "'\nTo add items to it do \"/shop add <SHOP> <PRICE>\" with the item selected in hand.");
				return true;
			}
			if (args.length == 3 && args[0].equalsIgnoreCase("add")){
				String name = args[1];
				int amount = 0;
				try{
					amount =  Integer.parseInt(args[2]);
				}catch(NumberFormatException e){
					sender.sendMessage(ChatColor.RED +"You see what you have done?? You didn't enter a number now look at this error:\n" + e.getMessage() );
					return true;
				}
				try {
					addItem((Player)sender, name, amount);
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Great, you somehow broke the plugin! Well done...\n"
							+ "Are you happy now? Here's the horrible mess you made:\n"+e.getMessage());
					e.printStackTrace();
				}
				return true;
			}

			if (args.length >= 3 && args[0].equalsIgnoreCase("command")){
				String name = args[1]; // Shop name
				int price = 0;
				
				try{
					price =  Integer.parseInt(args[2]);
				}catch(NumberFormatException e){
					sender.sendMessage(ChatColor.RED +"You see what you have done?? You didn't enter a number now look at this error:\n" + e.getMessage() );
					return true;
				}
				StringBuffer command = new StringBuffer();
				for (int i=3; i<args.length; i++){
					command.append(args[i] + " ");
				}
				
				addCommand(sender, name, price, command.toString());
				
				return true;
			}
		}
		
		String shopName = args[0];
		
		File xmlFile = new File(shopsFolder + File.separator + shopName + ".yml");
		if (!xmlFile.exists()){
			sender.sendMessage(ChatColor.RED + "Sorry but the shop '" + shopName + "' doesn't exsist.\n"
					+ChatColor.GOLD + "Here are the available shops:" );
			for (String file: shopsFolder.list()){
				sender.sendMessage(ChatColor.GREEN+ file.replace(".yml", ""));
			}
			return true;
		}
		if (sender.hasPermission("etokens.shop." + shopName) && !sender.isOp()){
			sender.sendMessage(ChatColor.RED +"You need the permission to access " +shopName);
			return true;
		}
		
		openShop((Player)sender, shopName);
		
		
		return false;
	}

	private void addCommand(CommandSender sender, String name, int price,
			String cmd) {
		File xmlFile = new File(shopsFolder + File.separator + name + ".yml");
		
		ItemStack itemToAdd = new ItemStack(Material.BEACON);
		
		ItemMeta m = itemToAdd.getItemMeta();
		m.setDisplayName(cmd);
		
		itemToAdd.setItemMeta(m);
		
		YamlConfiguration yamlConf = YamlConfiguration.loadConfiguration(xmlFile);
		
		int num = (yamlConf.getConfigurationSection("items").getValues(false)).size();
		yamlConf.set("items.item" + num +".merch", itemToAdd);
		yamlConf.set("items.item"+num+".price", price);
		yamlConf.set("items.item"+num + ".isCommand", true);
		
		
		try {
			yamlConf.save(xmlFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void openShop(Player sender, String shopName) {
		File ymlFile = new File(shopsFolder + File.separator + shopName + ".yml");
		Shop shop = new Shop(ymlFile);
		sender.openInventory(shop.getInv());
	}

	private void addItem(Player sender, String name, int amount) {
		File xmlFile = new File(shopsFolder + File.separator + name + ".yml");
		
		ItemStack itemToAdd = sender.getItemInHand();
		if (itemToAdd.getType() == Material.AIR){
			sender.sendMessage(ChatColor.RED + "Sorry, you cannot add air!");
			return;
		}
		YamlConfiguration yamlConf = YamlConfiguration.loadConfiguration(xmlFile);
		
		int num = (yamlConf.getConfigurationSection("items").getValues(false)).size();
		yamlConf.set("items.item" + num +".merch", itemToAdd);
		yamlConf.set("items.item"+num+".price", amount);
		
		try {
			yamlConf.save(xmlFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createShopFile(String string) throws IOException {		
		for (File f: shopsFolder.listFiles())
			if (f.getName().equalsIgnoreCase(string + ".yml"))
				return;
		
		File xmlFile = new File(shopsFolder + File.separator + string + ".yml");
		xmlFile.createNewFile();
		
		YamlConfiguration yamlConf = YamlConfiguration.loadConfiguration(xmlFile);
		yamlConf.set("items.item0", null);
		yamlConf.set("name", "`" + (new Random().nextInt(8)) +string);
		yamlConf.save(xmlFile);

	}
	
	private void sendHelp(CommandSender sender) {
		sender.sendMessage(ChatColor.AQUA + "/shop [help] -" + ChatColor.GOLD+" Shows this message");
		
		sender.sendMessage(ChatColor.AQUA + "/shop create <ShopName> -" + ChatColor.GOLD+" Creates a shop with the specified name (Case sensitive)");
		sender.sendMessage(ChatColor.AQUA + "/shop add <ShopName> <Price> -" + ChatColor.GOLD+" Add the currently held item to the specified shop");
		sender.sendMessage(ChatColor.AQUA + "/shop <ShopName> -" + ChatColor.GOLD+" Open the specified shop");
		sender.sendMessage(ChatColor.AQUA + "/shop command <ShopName> <Price> <Command> -" + ChatColor.GOLD+" Add a command to the shop with the specified price.");
	}
	
	
}
