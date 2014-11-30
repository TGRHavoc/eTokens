package ovh.tgrhavoc.etokens.sqlite;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.bukkit.entity.Player;

import ovh.tgrhavoc.etokens.eTokens;

public class SQLHandler {

	String dbName = "eTokens.db";
	eTokens plugin;

	public SQLHandler(eTokens mainRef) {
		plugin = mainRef;
		dbName = mainRef.getDataFolder() + File.separator + "eTokens.db";
	}

	// Create the DB if it doesn't exist
	public void init() {
		Connection c = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + dbName);
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		if (c != null) {
			System.out.println("Connection sucsesfully completed");
		}
		createTable();
		
	}

	public void createTable() {
		Connection c = null;
		Statement stmt = null;
		try {
			c = DriverManager.getConnection("jdbc:sqlite:" + dbName);
			stmt = c.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS eTokens "
					+ "(UUID VARCHARS NOT NULL," + " TOKENS INT )";
			stmt.executeUpdate(sql);
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			// System.exit(0);
		}
		System.out.println("Table created successfully");
	}

	public void setPlayerTokens(Player player, int tokenAmount) {
		Connection c = null;
		Statement stmt = null;
		try {
			c = DriverManager.getConnection("jdbc:sqlite:" + dbName);
			stmt = c.createStatement();
			String sql = "INSERT INTO eTokens (UUID, TOKENS) VALUES" + " ( '"
					+ player.getUniqueId().toString() + "' , " + tokenAmount
					+ ")";
			stmt.executeUpdate(sql);
			stmt.close();
			
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}
	
	public void increasePlayerToken(Player player, int amount){
		setPlayerTokens(player, getTokenAmount(player) + amount);
	}

	public int getTokenAmount(Player player) {
		Connection c = null;
		Statement stmt = null;
		int tokens = -1;
		try {
			c = DriverManager.getConnection("jdbc:sqlite:"+dbName);
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM eTokens WHERE UUID='" + player.getUniqueId().toString() + "';");
			while (rs.next()) {
				tokens = rs.getInt("TOKENS");
			}
			
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return tokens;
	}

}
