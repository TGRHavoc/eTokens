package ovh.tgrhavoc.etokens.listeners;

import java.io.Serializable;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerSave implements Serializable{

	private static final long serialVersionUID = 8089425461029684307L;
	
	String playerUUID;
	HashMap<String, Integer> kills = new HashMap<String, Integer>();
	HashMap<String, Integer> blocks = new HashMap<String, Integer>();
	
	public PlayerSave(Player player) {
		this.playerUUID = player.getUniqueId().toString();
	}

	public String getPlayer() {
		return playerUUID;
	}

	public void setPlayer(Player player) {
		this.playerUUID = player.getUniqueId().toString();
	}

	public HashMap<String, Integer> getKills() {
		return kills;
	}

	public HashMap<String, Integer> getBlocks() {
		return blocks;
	}
	
	public void addBlock(String blockMat){
		if (blocks.get(blockMat) == null){
			blocks.put(blockMat, 1);
			return;
		}
		int currentBlockBreaks = blocks.get(blockMat);
		blocks.put(blockMat, currentBlockBreaks + 1);
	}
	public void addKill(String entType){
		if (kills.get(entType) == null){
			kills.put(entType, 1);
			return;
		}
		int currentKills = kills.get(entType);
		kills.put(entType, currentKills + 1);
	}
	
	public int getKills(String ent){
		return kills.get(ent);
	}
	
	public int getBlocks(String mat){
		return blocks.get(mat.toLowerCase());
	}

	public void resetKills(String entityType) {
		kills.put(entityType, 0);		
	}
	
	public void resetBlocks(String material){
		blocks.put(material, 0);
	}
}
