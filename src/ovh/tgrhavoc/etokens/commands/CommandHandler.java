package ovh.tgrhavoc.etokens.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ovh.tgrhavoc.etokens.eTokens;

public class CommandHandler implements CommandExecutor{
	eTokens plugin;
	
	public CommandHandler(eTokens t){
		plugin = t;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0){
			sendHelp(sender);
			return true;
		}
		
		if (args[0].equals("help")){
			sendHelp(sender);
			return true;
		}
		
		if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("take")){
			if (!sender.hasPermission("etokens.admin")){
				sender.sendMessage(ChatColor.RED + "You don't have sufficient permissions to run this command!");
				return true;
			}
			if (args.length == 1 || args.length == 2){
				sender.sendMessage(ChatColor.RED + "You must specify a player and amount\n: e.g. /" + args[0] + " TGRHavoc <amount>");
				return true;
			}
			String operation = args[0];
			OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
			int amount = 0;
			try{
				amount =  Integer.parseInt(args[2]);
			}catch(NumberFormatException e){
				sender.sendMessage(ChatColor.RED +"You see what you have done?? You didn't enter a number now look at this error:\n" + e.getMessage() );
				return true;
			}
			if (operation.equalsIgnoreCase("set")){
				plugin.getSqlHandler().setPlayerTokens(player.getPlayer(), amount);
				sender.sendMessage(ChatColor.GREEN + "You have sucessfully set " + player.getName() +"'s tokens to " + ChatColor.GOLD + amount);
			}
			if (operation.equalsIgnoreCase("add")){
				plugin.getSqlHandler().increasePlayerToken(player.getPlayer(), amount);
				sender.sendMessage(ChatColor.GREEN + "You have sucessfully increased " + player.getName() +"'s tokens by " + ChatColor.GOLD + amount);
			}
			if (operation.equalsIgnoreCase("take") || operation.equalsIgnoreCase("remove")){
				plugin.getSqlHandler().increasePlayerToken(player.getPlayer(), -amount);
				sender.sendMessage(ChatColor.GREEN + "You have sucessfully decreased " + player.getName() +"'s tokens by " + ChatColor.GOLD + amount);
			}
			return true;
						
		}
		
		if (args[0].equalsIgnoreCase("give")){
			if (!sender.hasPermission("etokens.give")){
				sender.sendMessage(ChatColor.RED + "Sorry, you don't have permission to do this :(");
				return false;
			}
			if ( !(sender instanceof Player)){
				sender.sendMessage(ChatColor.RED + "Sorry, console only a player can issue this command!");
				return false;
			}
			if (args.length == 1 || args.length == 2){
				sender.sendMessage(ChatColor.RED + "Please make sure that you specify a player and the amount:\ne.g. /give TGRHavoc <AMOUNT>");
				return false;
			}
			Player giver = (Player)sender;
			OfflinePlayer reciever = Bukkit.getOfflinePlayer(args[1]);
			int amount = 0;
			try{
				amount =  Integer.parseInt(args[2]);
			}catch(NumberFormatException e){
				sender.sendMessage(ChatColor.RED +"You see what you have done?? You didn't enter a number now look at this error:\n" + e.getMessage() );
				return true;
			}
			plugin.getSqlHandler().increasePlayerToken(giver, -amount);
			plugin.getSqlHandler().increasePlayerToken(reciever.getPlayer(), amount);
			giver.sendMessage(ChatColor.GREEN +"You have sent "+ChatColor.GOLD + amount+ ChatColor.GREEN+" tokens to " + reciever.getName());
			giver.sendMessage(ChatColor.AQUA + "Your new balance is "+ ChatColor.GOLD + plugin.getSqlHandler().getTokenAmount(giver));
			return true;
		}
		
		if ( (args[0].equalsIgnoreCase("amount") || args[0].equalsIgnoreCase("see") || args[0].equalsIgnoreCase("balanace") ||
				args[0].equalsIgnoreCase("bal")) && sender instanceof Player){
			if (args.length == 1){
				sender.sendMessage(ChatColor.AQUA + "Your current balance is "+ ChatColor.GOLD + plugin.getSqlHandler().getTokenAmount((Player)sender) + ChatColor.AQUA+" tokens");
			}else if(args.length == 2){
				OfflinePlayer toGet = Bukkit.getOfflinePlayer(args[1]);
				if (!toGet.hasPlayedBefore()){
					sender.sendMessage(ChatColor.RED + "Sorry, this player hasn't played on this server before. They have no tokens.");
				}
				sender.sendMessage(ChatColor.GREEN + toGet.getName() +ChatColor.AQUA + " has " + ChatColor.GOLD + plugin.getSqlHandler().getTokenAmount(toGet.getPlayer()) + ChatColor.AQUA + " tokens!");
			}else{
				sender.sendMessage(ChatColor.RED + "I don't know what you're trying to do but, you've specified too many arguments!\nTry /see <Player> to see a player's token amount");
			}
		}
		
		return false;
	}

	private void sendHelp(CommandSender sender) {
		sender.sendMessage(ChatColor.AQUA + "/tokens [help] -" + ChatColor.GOLD+" Shows this message");
		sender.sendMessage(ChatColor.AQUA + "/tokens see -" + ChatColor.GOLD+" See your eToken balance");
		sender.sendMessage(ChatColor.AQUA + "/tokens add <Player> <Amount> -" + ChatColor.GOLD+" Add some tokens to this player's account");
		sender.sendMessage(ChatColor.AQUA + "/tokens remove <Player> <Amount> -" + ChatColor.GOLD+" Remove some tokens from this player's account");
		sender.sendMessage(ChatColor.AQUA + "/tokens set <Player> <Amount> -" + ChatColor.GOLD+" Set this players token count");
		sender.sendMessage(ChatColor.AQUA + "/tokens give <Player> <Amount> -" + ChatColor.GOLD+" Give this player some of your tokens");
	}

}
