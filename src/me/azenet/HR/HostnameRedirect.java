package me.azenet.HR;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class HostnameRedirect extends JavaPlugin {
	private Logger l = Bukkit.getLogger();
	private Connection db;
	private Statement dbStatement;
	
	public void onEnable() {
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
				dbStatement.execute("CREATE TABLE points(name varchar(255), hostname varchar(255), worldname varchar(255), x int, y int, z int, tpAnyway int)");
			}
		} catch (Exception e) {
			l.severe("You need H2 for this to run.");
			e.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(this);
		}
	}
	
	public void onDisable() {
		try {
			dbStatement.close();
			db.close();
		} catch (Exception e) {
		}
	}
}
