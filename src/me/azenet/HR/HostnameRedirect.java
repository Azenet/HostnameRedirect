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
	private File locationsFile;
	private HRMessageUtils mU;
	
	public void onEnable() {
		locationsFile = new File(this.getDataFolder() + File.separator + "locations.ser");
		mU = new HRMessageUtils(this);
		locations = new HashMap<String, HostnameRedirectLocation>();
		if (!locationsFile.exists()) {
			try {
				this.getDataFolder().mkdirs();
				locationsFile.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
			writeData();
		} else {
			locations = getDataFromLocationsFile();
		}
		Bukkit.getPluginManager().registerEvents(new HostnameRedirectListener(this), this);
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
			l.info(p.getName()+" never played. Teleporting to defined location "+hrl.getName());
			teleportPlayerToLocation(p, hrl.getName());
			return;
		}
		if (hrl != null && hrl.hasToTP()) {
			l.info(p.getName()+" : gotta teleport, mate!");
			teleportPlayerToLocation(p, hrl.getName());
			return;
		}
		if (hrl != null && hrl.getLocation().getWorld() != p.getLocation().getWorld()) {
			l.info(p.getName()+" : not the good world");
			teleportPlayerToLocation(p, hrl.getName());
			return;
		}
		if (isLocation("default")) {
			if (getLocationFromHostname("default").hasToTP() || !p.hasPlayedBefore()) {
				l.info("Teleporting "+p.getName()+" to default location");
				teleportPlayerToLocation(p, "default");
				return;
			}
		}
		l.info("Nothing matched for "+p.getName());
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
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Can't be used from the console.");
			return true;
		}
		Player p = (Player)sender;
		if (args.length < 1) {
			return false;
		}
		String mainCommand = args[0];
		switch (mainCommand) {
		case "help":
			mU.sendHelpToPlayer((Player)sender);
			return true;
		case "define":
			if (args.length < 2) {
				HRMessageUtils.formattedMessageToPlayer(p, "Usage: /hr define [hostname] [name]");
				HRMessageUtils.formattedMessageToPlayer(p, "Usage: /hr define default");
				HRMessageUtils.formattedMessageToPlayer(p, "Usage: /hr define default force");
				return true;
			}
			if (args[1] == "default") {
				if (args.length >= 3 && args[2] == "force") {
					this.addLocation(new HostnameRedirectLocation("default", "<no hostname>", p.getLocation(),true));
				} else {
					this.addLocation(new HostnameRedirectLocation("default", "<no hostname>", p.getLocation(),false));
				}
			} else {
				if (args.length < 3) {
					HRMessageUtils.formattedMessageToPlayer(p, "Usage: /hr define [hostname] [name]");
					HRMessageUtils.formattedMessageToPlayer(p, "Usage: /hr define default");
					HRMessageUtils.formattedMessageToPlayer(p, "Usage: /hr define default force");
					return true;
				}
				this.addLocation(new HostnameRedirectLocation(args[2], args[1], p.getLocation()));
			}
			return true;
		case "list":
			mU.sendHostnamesToPlayer(p);
			return true;
		default:
			p.sendMessage("WAT ? "+args[0]+"/"+mainCommand);
			return true;
		}
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
