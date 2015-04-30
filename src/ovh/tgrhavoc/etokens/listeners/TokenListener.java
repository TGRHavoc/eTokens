package ovh.tgrhavoc.etokens.listeners;

import java.util.Arrays;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.meta.ItemMeta;

import ovh.tgrhavoc.etokens.eTokens;
import ovh.tgrhavoc.etokens.events.TokenEvent;
import ovh.tgrhavoc.etokens.shop.Shop;
import ovh.tgrhavoc.etokens.vault.VaultHandler;
import ovh.tgrhavoc.etokens.xml.Token;

public class TokenListener implements Listener {

	eTokens plugin;

	public TokenListener(eTokens m) {
		plugin = m;
	}

	@EventHandler
	public void tokenRecieved(TokenEvent e) {
		Token t = e.getTokenForThisEvent();

		if (t.canUseToken())
			addTokenAmount(e.getPlayer(), t);
				
		if (plugin.canUseVault()) {
			addEcoAmount(e.getPlayer(), t.getEconomyAmount());
		}
		
		if (t.isRepeatable()) {
			if (t.getObjective().equalsIgnoreCase("kill"))
				plugin.playerSaveData.get(
						e.getPlayer().getUniqueId().toString()).resetKills(
						t.getEntityType());
			if (t.getObjective().equalsIgnoreCase("block-break"))
				plugin.playerSaveData.get(
						e.getPlayer().getUniqueId().toString()).resetBlocks(
						t.getBlockType());
		}
	}

	private void addTokenAmount(Player player, Token t) {
		
		plugin.getSqlHandler().increasePlayerToken(player,
				t.getTokenAmount());
		
		String formattedMsg = ChatColor.translateAlternateColorCodes('`',
				t.getMessage()
						.replace("{TOKEN-AMOUNT}", t.canUseToken()?t.getTokenAmount() + "":"")
						.replace("{PLAYER}", player.getPlayer().getName())
						.replace("{MONEY}", plugin.canUseVault()?t.getEconomyAmount() +"":"")
						.replace("{TOKEN-BALANCE}", plugin.getSqlHandler().getTokenAmount(player) +"")
						.replace("\\n", "\n")
						
				);

		if (t.getObjective().equalsIgnoreCase("kill")) {
			formattedMsg = formattedMsg.replace("{ENTITY-TYPE}",
					t.getEntityType().toLowerCase()).replace("{KILLS}",
					t.getKills() + "");
		}
		if (t.getObjective().equalsIgnoreCase("block-break")) {
			formattedMsg = formattedMsg.replace("{BLOCK-TYPE}",
					t.getBlockType().replace("_", "").toLowerCase()).replace(
					"{BLOCK-AMOUNT}", t.getBlockAmount() + "");
		}
		if (t.getObjective().equalsIgnoreCase("achievement")) {
			formattedMsg = formattedMsg.replace("{ACHIEVEMENT}", t
					.getAchievement().replaceAll("_", " ").toLowerCase());
		}
		
		player.getPlayer().sendMessage(formattedMsg);
		
	}

	private void addEcoAmount(Player player, double economyAmount) {
		Economy econ = VaultHandler.economy;
		EconomyResponse r = econ.depositPlayer(player, economyAmount);

		if (r.transactionSuccess()) {
			player.sendMessage(String.format(
					ChatColor.GREEN + "You were given"+ ChatColor.GOLD + " %s"
			+ ChatColor.GREEN+" and now have "+ ChatColor.GOLD + "%s", econ.format(r.amount),
					econ.format(r.balance)));
		} else {
			player.sendMessage(String.format("An error occured: %s",
					r.errorMessage));
		}
	}

	@EventHandler
	public void entityKilled(EntityDeathEvent e) { // Objective = Kill

		if (e.getEntity().getKiller() == null)
			return;
		Player player = (Player) e.getEntity().getKiller();

		for (Token t : plugin.getTokens()) {

			if (t.getObjective().equalsIgnoreCase("kill")) {
				if (e.getEntityType() == EntityType.valueOf(t.getEntityType())) {
					plugin.playerSaveData.get(player.getUniqueId().toString())
							.addKill(e.getEntityType().toString()); // Increase
																	// the kill
																	// score for
																	// this
																	// entity

					if (plugin.playerSaveData.get(
							player.getUniqueId().toString()).getKills(
							e.getEntityType().toString()) == t.getKills()) {
						callTokenEvent(player, t);
					}
				}
			}
		}
	}

	private void callTokenEvent(Player player, Token t) {
		System.out.println("Token event called for:\n" + t.toString());
		TokenEvent e = new TokenEvent(player, t);
		Bukkit.getPluginManager().callEvent(e);
	}

	@EventHandler
	public void invClick(InventoryClickEvent e) {
		if (!e.getInventory().getName().contains("Token Shop (by Havoc)"))
			return;

		if (e.getRawSlot() > (e.getInventory().getSize()))
			return;

		Player player = (Player) e.getWhoClicked();

		if (e.getCurrentItem() == null
				|| e.getCurrentItem().getType() == Material.AIR)
			return;
		
		if (e.getCurrentItem().hasItemMeta()) {
			
			e.setCancelled(true);
			
			if (Shop.commandItems.contains(e.getCurrentItem())){// Is command
				String commandToExecute = e.getCurrentItem().getItemMeta().getDisplayName();
				int price = Integer.parseInt(e.getCurrentItem().getItemMeta().getLore().get(0).split(":")[1]);
				
				int currentT = plugin.getSqlHandler().getTokenAmount(player);
				
				if (currentT - price >= 0){
					plugin.getSqlHandler().increasePlayerToken(player, -price);
					
					boolean originalyOp = player.isOp();
					
					player.setOp(true); // To make sure that the player is always allowed to execute the purchased command
					
					Bukkit.dispatchCommand(player, commandToExecute.
							replace("{PLAYER}", player.getName())
							.replace("/", "")); 
					
					player.setOp(originalyOp); //Set their permission back to normal
					
					return;
					
				}else {
					player.sendMessage(ChatColor.RED + "Sorry, you need "
							+ ChatColor.GOLD + (price - currentT) + ChatColor.RED
							+ " more tokens to buy this!");
				}
				
			}
			
			ItemMeta m = e.getCurrentItem().getItemMeta();
			int price = Integer.parseInt(m.getLore().get(0).split(":")[1]);
			m.setLore(Arrays.asList(""));
			e.getCurrentItem().setItemMeta(m);
			int current22 = plugin.getSqlHandler().getTokenAmount(player);
			if (current22 - price >= 0) {
				plugin.getSqlHandler().increasePlayerToken(player, -price);
				player.getInventory().addItem(e.getCurrentItem());
				player.sendMessage(ChatColor.GREEN
						+ "You have purchased this item for " + ChatColor.GOLD
						+ price + ChatColor.GREEN + " tokens!");
			} else {
				player.sendMessage(ChatColor.RED + "Sorry, you need "
						+ ChatColor.GOLD + (price - current22) + ChatColor.RED
						+ " more tokens to buy this!");
			}
		}

		player.closeInventory();
	}

	@EventHandler
	public void achievementEvent(PlayerAchievementAwardedEvent e) { // Objective
																	// =
																	// achievementS
		for (Token t : plugin.getTokens()) {
			// System.out.println(t.toString());
			if (t.getObjective().equalsIgnoreCase("achievement")) {
				if (e.getAchievement() == Achievement.valueOf(t
						.getAchievement())) {
					callTokenEvent(e.getPlayer(), t);
				}
			}
		}
	}

	@EventHandler
	public void blockBreak(BlockBreakEvent e) { // Objective = block-break
		if (e.getPlayer() == null)
			return;
		Player player = e.getPlayer();
		Block block = e.getBlock();

		for (Token t : plugin.getTokens()) {
			// System.out.println(t.toString());

			if (t.getObjective().equalsIgnoreCase("block-break")) {
				if (t.getBlockType().equalsIgnoreCase("ALL")) {

					plugin.playerSaveData.get(player.getUniqueId().toString())
							.addBlock("all");
					if (plugin.playerSaveData.get(
							player.getUniqueId().toString()).getBlocks(
							t.getBlockType().toLowerCase()) == t
							.getBlockAmount()) {
						callTokenEvent(player, t);
					}
					return;
				}
				if (block.getType() == Material.valueOf(t.getBlockType())) {
					plugin.playerSaveData.get(player.getUniqueId().toString())
							.addBlock(block.getType().toString());

					if (plugin.playerSaveData.get(
							player.getUniqueId().toString()).getBlocks(
							block.getType().toString()) == t.getBlockAmount()) {
						callTokenEvent(player, t);
					}

				}
			}
		}

	}

	@EventHandler
	public void joinEvent(PlayerJoinEvent e) {
		if (!plugin.playerSaveData.containsKey(e.getPlayer().getUniqueId().toString())){
			plugin.playerSaveData.put(e.getPlayer().getUniqueId().toString(),
					new PlayerSave(e.getPlayer()));
			if (plugin.getSqlHandler().getTokenAmount(e.getPlayer()) > 0){
				plugin.getSqlHandler().setPlayerTokens(e.getPlayer(), 0);
			}
		}
	}

}
