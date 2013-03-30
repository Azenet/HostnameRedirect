package me.azenet.HR;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class HostnameRedirectListener implements Listener {
	private HostnameRedirect plugin;
	
	public HostnameRedirectListener(HostnameRedirect p) {
		this.plugin = p;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLogin(PlayerLoginEvent ev) {
		
	}
	
}
