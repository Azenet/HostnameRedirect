package me.azenet.HR;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class HostnameRedirect extends JavaPlugin {
	private Logger l = Bukkit.getLogger();
	private Connection db;
	private Statement dbStatement;
	private HashMap<String, HostnameRedirectLocation> locations;
	private File locationsFile = new File(this.getDataFolder() + File.separator + "locations.ser");
	private boolean debugMode = true;
	
	public void onEnable() {
		locations = new HashMap<String, HostnameRedirectLocation>();
		if (!locationsFile.exists()) {
			writeData();
		} else {
			locations = getDataFromLocationsFile();
		}
	}
	
	public void writeData() {
		try {
			FileOutputStream fos = new FileOutputStream(locationsFile);
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(locations);
			out.close();
			fos.close();
		} catch (Exception e) {
			l.severe("Can't write the Locations file. Your changes won't be saved.");
			e.printStackTrace();
		}		
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<String, HostnameRedirectLocation> getDataFromLocationsFile() {
		try {
			FileInputStream fis = new FileInputStream(locationsFile);
			ObjectInputStream in = new ObjectInputStream(fis);
			HashMap<String, HostnameRedirectLocation> toReturn = (HashMap<String, HostnameRedirectLocation>) in.readObject();
			in.close();
			fis.close();
			return toReturn;
		} catch (Exception e) {
			l.severe("Can't read the locations file. Please delete locations.ser");
			e.printStackTrace();
		}
		return null;
	}
	
	public void playerOnline(Player p, String hostname) {
		HostnameRedirectLocation hrl = getLocationFromHostname(hostname);
		if (!p.hasPlayedBefore() && hrl != null) {
			debug(p.getName()+" never played. Teleporting to defined location "+hrl.getName());
			teleportPlayerToLocation(p, hrl.getName());
			return;
		}
		if (hrl != null && p.getLocation().getWorld() != hrl.getLocation().getWorld()) {
			teleportPlayerToLocation(p, hrl.getName());
			return;
		}
		if (hrl != null && hrl.hasToTP() ) {
			teleportPlayerToLocation(p, hrl.getName());
			return;
		}
		if (!p.hasPlayedBefore() && isLocation("default")) {
			teleportPlayerToLocation(p, "default");
			return;
		}
	}
	
	public void teleportPlayerToLocation(Player p, String loc) {
		
	}
	
	public Boolean isLocation(String locationName) {
		return locations.containsKey(locationName);
	}
	
	public HostnameRedirectLocation getLocationFromName(String locationName) {
		return (isLocation(locationName)) ? locations.get(locationName) : null;
	}
	
	public HostnameRedirectLocation getLocationFromHostname(String hostname) {
		for (HostnameRedirectLocation hrl : locations.values()) {
			if (hostname.contains(hrl.getHostname())) return hrl;
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
	
	public void debug(String str) {
		if (debugMode) l.info(str);
	}
}
