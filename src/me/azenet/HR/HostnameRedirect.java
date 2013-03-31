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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class HostnameRedirect extends JavaPlugin {
	private Logger l = Bukkit.getLogger();
	private Connection db;
	private Statement dbStatement;
	private HashMap<String, HostnameRedirectLocation> locations;
	private File locationsFile = new File(this.getDataFolder() + File.separator + "locations.ser");
	private boolean debugMode = true;
	private HRMessageUtils mU;
	
	public void onEnable() {
		mU = new HRMessageUtils(this);
		locations = new HashMap<String, HostnameRedirectLocation>();
		if (!locationsFile.exists()) {
			try {
				locationsFile.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
			writeData();
		} else {
			locations = getDataFromLocationsFile();
		}
	}
	
	public void writeData() {
		try {
			FileOutputStream fos = new FileOutputStream(locationsFile, false);
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
		if (hrl != null && hrl.hasToTP()) {
			debug(p.getName()+" : gotta teleport, mate!");
			teleportPlayerToLocation(p, hrl.getName());
			return;
		}
		if (hrl != null && hrl.getLocation().getWorld() != p.getLocation().getWorld()) {
			debug(p.getName()+" : not the good world");
			teleportPlayerToLocation(p, hrl.getName());
			return;
		}
		if (isLocation("default")) {
			if (getLocationFromHostname("default").hasToTP() || !p.hasPlayedBefore()) {
				debug("Teleporting "+p.getName()+" to default location");
				teleportPlayerToLocation(p, "default");
				return;
			}
		}
		debug("Nothing matched for "+p.getName());
	}
	
	public Boolean teleportPlayerToLocation(Player p, String loc) {
		HostnameRedirectLocation hrl = getLocationFromName(loc);
		if (hrl != null) {
			p.teleport(hrl.getLocation());
			return true;
		}
		return false;
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
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Can't be used from the console.");
			return true;
		}
		Player p = (Player)sender;
		if (args.length < 2) {
			return false;
		}
		String mainCommand = args[1];
		switch (mainCommand) {
		case "help":
			mU.sendHelpToPlayer((Player)sender);
			break;
		case "define":
			if (args[2] == "default") {
				if (args[3] != null && args[3] == "force") {
					this.addLocation(new HostnameRedirectLocation("default", "<no hostname>", p.getLocation(),true));
				} else {
					this.addLocation(new HostnameRedirectLocation("default", "<no hostname>", p.getLocation(),false));
				}
			}
		}
		return false;
	}

	public void addLocation(HostnameRedirectLocation hrl) {
		this.addLocation(hrl, true);
	}
	
	public void addLocation(HostnameRedirectLocation hrl, Boolean force) {
		Boolean hadTo = false;
		if (locations.containsKey(hrl.getName())) {
			if (force) {
				locations.remove(hrl.getName());
				hadTo = true;
			}
		}
		if ((hadTo && force) || !hadTo) locations.put(hrl.getName(), hrl);
		writeData();
	}
	
	public HashMap<String, HostnameRedirectLocation> getLocations() {
		return locations;
	}
}
