package me.azenet.HR;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HRMessageUtils {
	private HostnameRedirect plugin;
	
	public HRMessageUtils(HostnameRedirect p) {
		this.plugin = p;
	}
	
	public void sendHelpToPlayer(Player p) {
		HRMessageUtils.formattedMessageToPlayer(p, "Note: the following uses your current coordinates.");
		HRMessageUtils.formattedMessageToPlayer(p, "/hr define default - Set newbie spawn");
		HRMessageUtils.formattedMessageToPlayer(p, "/hr define default force - Set default spawn for everyone");
		HRMessageUtils.formattedMessageToPlayer(p, "/hr define [hostname] [name] - Defines a hostname");
		HRMessageUtils.formattedMessageToPlayer(p, " (no need to write the whole hostname, a part is enough)");
		HRMessageUtils.formattedMessageToPlayer(p, "/hr toggletp [name] - Whether to teleport the player if he is already on the same world as the defined hostname");
		HRMessageUtils.formattedMessageToPlayer(p, "/hr list - List all hostnames defined");
		HRMessageUtils.formattedMessageToPlayer(p, "/hr tp [name] - Teleports to the location of a hostname");
		HRMessageUtils.formattedMessageToPlayer(p, "/hr help - Shows this");
	}
	
	public void sendHostnamesToPlayer(Player p) {
		HRMessageUtils.formattedMessageToPlayer(p, ChatColor.AQUA+"Defined hostnames:");
		for (HostnameRedirectLocation hrl : plugin.getLocations().values()) {
			HRMessageUtils.formattedMessageToPlayer(p, ChatColor.DARK_AQUA+hrl.getName()+ChatColor.WHITE+": "+ChatColor.GOLD+hrl.getHostname()+ChatColor.GRAY+((hrl.hasToTP()) ? " [force]" : "" ));
		}
	}
	
	public static void formattedMessageToPlayer(Player p, String s) {
		p.sendMessage(ChatColor.DARK_GREEN+"[HR] "+ChatColor.WHITE+s);
	}
}
