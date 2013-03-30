package me.azenet.HR;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class HostnameRedirect extends JavaPlugin {
	private Logger l = Bukkit.getLogger();
	private Connection db;
	private Statement dbStatement;
	private HashMap<String, HashMap<String, Object>> locations; 
	private HashMap<String, HashMap<String, Object>> onlinePlayers;
	
	public void onEnable() {
		locations = new HashMap<String, HashMap<String, Object>>();
		try {
			Class.forName("org.h2.Driver");
			db = DriverManager.getConnection("jdbc:h2:"+this.getDataFolder().getAbsolutePath()+File.separator+"db.h2");
			dbStatement = db.createStatement();
			ResultSet rs = db.getMetaData().getTables(db.getCatalog(), null, null, null);
			Boolean tableExists = false;
			while (rs.next()) {
				if (rs.getString("TABLE_NAME") == "points") tableExists = true;
			}
			if (!tableExists) {
				dbStatement.execute("CREATE TABLE points(name varchar(255) primary key, hostname varchar(255), worldname varchar(255), x int, y int, z int, tpAnyway int)");
			}
			this.readDB();
		} catch (Exception e) {
			l.severe("You need H2 for this to run.");
			e.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(this);
		}
	}
	
	public void readDB() {
		try {
			ResultSet rs = dbStatement.executeQuery("SELECT * FROM points");
			while (rs.next()) {
				World w = Bukkit.getServer().getWorld(rs.getString("worldname"));
				if (w != null) {
					Location l = new Location(w, rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
					HashMap<String, Object> toPut = new HashMap<String, Object>();
					toPut.put("location", l);
					toPut.put("tpAnyway", (Integer.valueOf(rs.getString("tpAnyway")) > 0) ? true : false);
					toPut.put("name", rs.getString("name"));
					locations.put(rs.getString("hostname"), toPut);
				} else {
					l.warning("World "+rs.getString("worldname")+" does not exist. Please delete record "+rs.getString("name")+" using /hr delete "+rs.getString("name"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void playerOnline(Player p) {
		if (!p.hasPlayedBefore() && isLocation("default")) {
			teleportPlayerToLocation(p, "default");
		}
	}
	
	public void teleportPlayerToLocation(Player p, String loc) {
		
	}
	
	public Boolean isLocation(String locationName) {
		for (HashMap<String, Object> hm : locations.values()) {
			if (((String)hm.get("name")).equalsIgnoreCase(locationName)) return true;
		}
		return false;
	}
	
	public HashMap<String, Object> getLocationFromName(String locationName) {
		for (HashMap<String, Object> hm : locations.values()) {
			if (((String)hm.get("name")).equalsIgnoreCase(locationName)) return hm;
		}
		return null;
	}

	public void onDisable() {
		try {
			dbStatement.close();
			db.close();
		} catch (Exception e) {
		}
	}
}
