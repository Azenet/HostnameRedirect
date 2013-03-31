package me.azenet.HR;

import java.io.Serializable;

import org.bukkit.Location;
import org.bukkit.World;

public class HostnameRedirectLocation implements Serializable {
	private static final long serialVersionUID = 8431258053049898710L;
	private Double x;
	private Double y;
	private Double z;
	private Float yaw;
	private Float pitch;
	private String hostname;
	private String name;
	private Boolean tpAnyway;
	private World world;

	public HostnameRedirectLocation(String name, String hostname, World world, double x, double y, double z) {
		this(name, hostname, world, x, y, z, false, 0F, 0F);
	}
	public HostnameRedirectLocation(String name, String hostname, World world, double x, double y, double z, boolean tpAnyway) {
		this(name, hostname, world, x, y, z, tpAnyway, 0F, 0F);
	}
	public HostnameRedirectLocation(String name, String hostname, Location loc) {
		this(name, hostname, loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), false, loc.getYaw(), loc.getPitch());
	}
	public HostnameRedirectLocation(String name, String hostname, Location loc, Boolean tpAnyway) {
		this(name, hostname, loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), tpAnyway, loc.getYaw(), loc.getPitch());
	}
	public HostnameRedirectLocation(String name, String hostname, World world, double x, double y, double z, boolean tpAnyway, float yaw, float pitch) {
		this.name = name;
		this.hostname = hostname;
		this.world = world;
		this.x = Double.valueOf(x);
		this.y = Double.valueOf(y);
		this.z = Double.valueOf(z);
		this.tpAnyway = tpAnyway;
		this.yaw = Float.valueOf(yaw);
		this.pitch = Float.valueOf(pitch);
	}
	
	public Location getLocation() {
		return new Location(world, x, y, z, yaw, pitch);
	}
	
	public String getHostname() {
		return hostname;
	}
	
	public String getName() {
		return name;
	}
	
	public Boolean hasToTP() {
		return tpAnyway;
	}
	
}
