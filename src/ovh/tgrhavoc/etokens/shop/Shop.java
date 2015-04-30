package ovh.tgrhavoc.etokens.shop;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
	
	public static List<ItemStack> commandItems = new ArrayList<ItemStack>();

	public Shop(File shopFile) {
		YamlConfiguration conf = YamlConfiguration.loadConfiguration(shopFile);
		
		String name = ChatColor.translateAlternateColorCodes('`', conf.getString("name") + " Token Shop (by Havoc) ");
		
		for (int i=0; i< conf.getConfigurationSection("items").getValues(false).size(); i++){
			
			ItemStack addMe = (ItemStack) conf.get("items.item" + i + ".merch");
			if (conf.getBoolean("items.item" + i +".isCommand")){
				commandItems.add(addMe);
			}
			int price =conf.getInt("items.item" + i + ".price");
			content.put(addMe, price);
		}
		
		inv = Bukkit.createInventory(null, 9*6, name);
		for (Entry<ItemStack, Integer> e: content.entrySet()){
			
			
			
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
