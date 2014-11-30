package ovh.tgrhavoc.etokens.shop;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Shop {
	Inventory inv;
	HashMap<ItemStack, Integer> content = new HashMap<ItemStack, Integer>();

	public Shop(File shopFile) {
		YamlConfiguration conf = YamlConfiguration.loadConfiguration(shopFile);
		
		String name = ChatColor.translateAlternateColorCodes('`', conf.getString("name") + " Shop");
		
		for (int i=0; i< conf.getConfigurationSection("items").getValues(false).size(); i++){
			
			ItemStack addMe = (ItemStack) conf.get("items.item" + i + ".merch");
			//System.out.println("Iteration " + i +" item: "+ addMe.toString() );
			int price =conf.getInt("items.item" + i + ".price");
			content.put(addMe, price);
		}
		
		inv = Bukkit.createInventory(null, 9*6, name);
		for (Entry<ItemStack, Integer> e: content.entrySet()){
			//System.out.println(e.getKey().toString());
			ItemMeta m = e.getKey().getItemMeta();
			m.setLore(Arrays.asList("Price:" + e.getValue()));
			e.getKey().setItemMeta(m);
			inv.addItem(e.getKey());
		}
	}

	public Inventory getInv() {
		return inv;
	}

}
