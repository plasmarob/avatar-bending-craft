package me.plasmarob.bending;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import me.plasmarob.bending.airbending.*;
import me.plasmarob.bending.earthbending.*;
import me.plasmarob.bending.firebending.*;
import me.plasmarob.bending.waterbending.*;
//import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
//import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
//import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.BlockIterator;

public class BendingPlayer {
	
	private static ConcurrentHashMap<UUID, BendingPlayer> players = new ConcurrentHashMap<UUID, BendingPlayer>();
	private UUID uuid;
	public boolean isRunning;
	public static Tools tools;
	private Vector<Integer> keys = new Vector<Integer>();
	private Player player;
	private String bendingType = "";
	private Inventory bendingGUI; 
	
	private boolean bendingIsEnabled = true;
	private boolean iceBool = false;
	private boolean swipeDirection = false;
	
	private boolean swimmingEnabled = true;
	private boolean waterRunEnabled = false;
	private List<Block> waterRunBlocks = new ArrayList<Block>();
	
	
	Location loc;
	BlockIterator bit;
	Block next;
	
	/**
	 * Reinitializing method
	 * @param uuid
	 */
	public BendingPlayer(UUID uuid)
	{
		if (players.containsKey(uuid)) 
			return; // this should never happen
		
		this.uuid = uuid;
		this.bendingType = Bending.playersConfig.getString(uuid + ".bendingType");
		players.put(uuid, this);
		
		player = Bukkit.getPlayer(uuid);
		bendingGUI = Bukkit.getServer().createInventory(null, 54, "Bending");
		guiSetup();
	}
	
	/**
	 * Creation method
	 * @param uuid
	 * @param bendingType
	 */
	public BendingPlayer(UUID uuid, String bendingType) {
		if (players.containsKey(uuid)) 
		{
			players.remove(uuid);
			//return; // this should never happen
		}
		player = Bukkit.getServer().getPlayer(uuid);
		
		if (bendingType.equals("water"))
		{
			player.sendMessage(ChatColor.BLUE + "You are now a waterbender!");
			Bending.playersConfig.set(uuid + ".water_xp", 0);
			Bending.playersConfig.set(uuid + ".plant_xp", 0);
			Bending.playersConfig.set(uuid + ".north_xp", 0);
			Bending.playersConfig.set(uuid + ".south_xp", 0);
			Bending.playersConfig.set(uuid + ".republic_xp", 0);
		}
		else if (bendingType.equals("air"))
		{
			player.sendMessage(ChatColor.YELLOW + "You are now a airbender!");
			Bending.playersConfig.set(uuid + ".air_xp", 0);
		}
		else if (bendingType.equals("fire"))
		{
			player.sendMessage(ChatColor.DARK_RED + "You are now a firebender!");
			Bending.playersConfig.set(uuid + ".fire_xp", 0);
			Bending.playersConfig.set(uuid + ".tradition_xp", 0);
			Bending.playersConfig.set(uuid + ".nation_xp", 0);
		}
		else if (bendingType.equals("earth"))
		{
			player.sendMessage(ChatColor.GREEN + "You are now a earthbender!");
			Bending.playersConfig.set(uuid + ".earth_xp", 0);
			Bending.playersConfig.set(uuid + ".sand_xp", 0);
			Bending.playersConfig.set(uuid + ".metal_xp", 0);
			Bending.playersConfig.set(uuid + ".lava_xp", 0);
		}
		Bending.playersConfig.set(uuid + ".bendingType", bendingType);
		Bending.playersConfig.set(uuid + ".name", player.getName().toString());
		
		this.uuid = uuid;
		this.bendingType = bendingType;
		players.put(uuid, this);
		
		player = Bukkit.getPlayer(uuid);
		bendingGUI = Bukkit.getServer().createInventory(null, 54, "Bending");
		guiSetup();
	}
	
	@SuppressWarnings("deprecation")
	public void toggleIce() {
		iceBool = !iceBool;
		
		ItemStack is;
		ItemMeta meta;
		ArrayList<String> metaStrs;
		
		if (iceBool)
		{	
			bendingGUI.setItem(35, new ItemStack(Material.INK_SACK, 1, (short)0, (byte) 10));
			
			is = bendingGUI.getItem(35);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.WHITE + "Toggle Temp");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.AQUA + " " + ChatColor.ITALIC + "freeze");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
		}
		else
		{
			bendingGUI.setItem(35, new ItemStack(Material.INK_SACK, 1, (short)0, (byte) 9));
			
			is = bendingGUI.getItem(35);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.WHITE + "Toggle Temp");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.RED + " " + ChatColor.ITALIC + "melt");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
		}
	}
	public boolean getIceBool() {
		return iceBool;
	}
	
	@SuppressWarnings("deprecation")
	public void toggleSwimming() {
		swimmingEnabled = !swimmingEnabled;
		
		ItemStack is;
		ItemMeta meta;
		ArrayList<String> metaStrs;
		
		if (swimmingEnabled)
		{	
			bendingGUI.setItem(27, new ItemStack(Material.INK_SACK, 1, (short)0, (byte) 10));
			
			is = bendingGUI.getItem(27);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.WHITE + "Swimming");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.GREEN + " " + ChatColor.ITALIC + "On");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
		}
		else
		{
			bendingGUI.setItem(27, new ItemStack(Material.INK_SACK, 1, (short)0, (byte) 9));
			
			is = bendingGUI.getItem(27);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.WHITE + "Swimming");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.RED + " " + ChatColor.ITALIC + "Off");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
		}
	}
	@SuppressWarnings("deprecation")
	public void toggleWaterRun() {
		waterRunEnabled = !waterRunEnabled;
		
		ItemStack is;
		ItemMeta meta;
		ArrayList<String> metaStrs;
		
		if (waterRunEnabled)
		{	
			bendingGUI.setItem(36, new ItemStack(Material.INK_SACK, 1, (short)0, (byte) 10));
			
			is = bendingGUI.getItem(36);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.WHITE + "Run on water");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.GREEN + " " + ChatColor.ITALIC + "On");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
		}
		else
		{
			bendingGUI.setItem(36, new ItemStack(Material.INK_SACK, 1, (short)0, (byte) 9));
			
			is = bendingGUI.getItem(36);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.WHITE + "Run on water");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.RED + " " + ChatColor.ITALIC + "Off");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
		}
	}
	
	public void toggleSwipe() {
		swipeDirection = !swipeDirection;
	}
	public boolean getSwipeDir() {
		return swipeDirection;
	}
	
	@SuppressWarnings("deprecation")
	public void toggleBending() {
		bendingIsEnabled = !bendingIsEnabled;
		
		ItemStack is;
		ItemMeta meta;
		ArrayList<String> metaStrs;
		
		if (bendingIsEnabled)
		{	
			bendingGUI.setItem(44, new ItemStack(Material.INK_SACK, 1, (short)0, (byte) 10));
			
			is = bendingGUI.getItem(44);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.WHITE + "Toggle Bending");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.GREEN + " " + ChatColor.ITALIC + "enabled");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
		}
		else
		{
			bendingGUI.setItem(44, new ItemStack(Material.INK_SACK, 1, (short)0, (byte) 9));
			
			is = bendingGUI.getItem(44);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.WHITE + "Toggle Bending");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.RED + " " + ChatColor.ITALIC + "disabled");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
		}
		
	}
	
	@SuppressWarnings("deprecation")
	public void guiSetup()
	{
		ItemStack is;
		ItemMeta meta;
		ArrayList<String> metaStrs;
		
		bendingGUI.setItem(44, new ItemStack(Material.INK_SACK, 1, (short)0, (byte) 10));
		
		is = bendingGUI.getItem(44);
		meta = is.getItemMeta();
		meta.setDisplayName(ChatColor.WHITE + "Toggle Bending");
		metaStrs = new ArrayList<String>();
		metaStrs.add(ChatColor.GREEN + " " + ChatColor.ITALIC + "enabled");
		meta.setLore(metaStrs);
		is.setItemMeta(meta);
		
		// The Firebending User Interface
		if (bendingType.equals("fire"))
		{
			bendingGUI.setItem(10, new ItemStack(Material.NETHERRACK));
			bendingGUI.setItem(11, new ItemStack(Material.TORCH));
			bendingGUI.setItem(12, new ItemStack(Material.REDSTONE_TORCH_ON));
			bendingGUI.setItem(13, new ItemStack(Material.ICE));
			bendingGUI.setItem(14, new ItemStack(Material.LEATHER_BOOTS));
			bendingGUI.setItem(15, new ItemStack(Material.REDSTONE));
			bendingGUI.setItem(16, new ItemStack(Material.BLAZE_ROD));
			
			bendingGUI.setItem(19, new ItemStack(Material.LEASH));
			bendingGUI.setItem(20, new ItemStack(Material.FIREBALL));
			bendingGUI.setItem(21, new ItemStack(Material.BRICK));
			bendingGUI.setItem(22, new ItemStack(Material.DIAMOND_SWORD));
			bendingGUI.setItem(23, new ItemStack(Material.FEATHER));
			bendingGUI.setItem(24, new ItemStack(Material.MAGMA_CREAM));
			bendingGUI.setItem(25, new ItemStack(Material.BLAZE_POWDER));
			
			bendingGUI.setItem(30, new ItemStack(Material.TNT));
			bendingGUI.setItem(32, new ItemStack(Material.BEACON));
			
			is = bendingGUI.getItem(10);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_RED + "Heat");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.RED + " " + ChatColor.ITALIC + "intense emotion");
			metaStrs.add(ChatColor.WHITE + "Click to melt your surroundings.");
			metaStrs.add(ChatColor.WHITE + "Sneak to heat what you hold.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(11);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_RED + "Emit");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.RED + " " + ChatColor.ITALIC + "hand-to-hand");
			metaStrs.add(ChatColor.WHITE + "Click to punch.");
			metaStrs.add(ChatColor.WHITE + "Sneak to kick.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(12);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_RED + "Beam");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.RED + " " + ChatColor.ITALIC + "burst of fire");
			metaStrs.add(ChatColor.WHITE + "Sneak to charge, click to unleash.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(13);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_RED + "Cool");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.RED + " " + ChatColor.ITALIC + "calm the storm");
			metaStrs.add(ChatColor.WHITE + "Sneak to end fire and lava.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(14);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_RED + "Spin Kick");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.RED + " " + ChatColor.ITALIC + "foot-to-foot");
			metaStrs.add(ChatColor.WHITE + "Sneak and click to trip your foe.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(15);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_RED + "Wave");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.RED + " " + ChatColor.ITALIC + "threatening rage");
			metaStrs.add(ChatColor.WHITE + "Click to swing a large wave.");
			metaStrs.add(ChatColor.WHITE + "Sneak determines angle.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(16);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_RED + "Jet");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.RED + " " + ChatColor.ITALIC + "stream of fire");
			metaStrs.add(ChatColor.WHITE + "Click to fire a bolt.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(19);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_RED + "Toss");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.RED + " " + ChatColor.ITALIC + "soldier's trap");
			metaStrs.add(ChatColor.WHITE + "Click to throw a flame.");
			metaStrs.add(ChatColor.WHITE + "Holding sneak will instead toss a band.");
			metaStrs.add(ChatColor.WHITE + "Try it over your enemy's head!");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(20);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_RED + "Ball");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.RED + " " + ChatColor.ITALIC + "soldier's attack");
			metaStrs.add(ChatColor.WHITE + "Click to throw a fireball.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(21);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_RED + "Wall");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.RED + " " + ChatColor.ITALIC + "Jeong Jeong's defense");
			metaStrs.add(ChatColor.WHITE + "Hold sneak and drag across the ground.");
			metaStrs.add(ChatColor.WHITE + "Click to activate.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(22);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_RED + "Blade");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.RED + " " + ChatColor.ITALIC + "slice and dice");
			metaStrs.add(ChatColor.WHITE + "Hold sneak and swing");
			metaStrs.add(ChatColor.WHITE + "to cut through anything.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(23);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_RED + "Launch");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.RED + " " + ChatColor.ITALIC + "energetic flight");
			metaStrs.add(ChatColor.WHITE + "While sprinting, click to fly.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(24);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_RED + "Sphere");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.RED + " " + ChatColor.ITALIC + "A master's control");
			metaStrs.add(ChatColor.WHITE + "Hold sneak to protect yourself");
			metaStrs.add(ChatColor.WHITE + "with a ball of flame.");
			metaStrs.add(ChatColor.WHITE + "Click to teach your opponent a lesson.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(25);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_RED + "Blast");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.RED + " " + ChatColor.ITALIC + "Fire Lord's wrath");
			metaStrs.add(ChatColor.WHITE + "Hold sneak to unleash destruction.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(30);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_RED + "Combustion");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.RED + " " + ChatColor.ITALIC + "sparky sparky boom");
			metaStrs.add(ChatColor.WHITE + "Hold sneak, and charge up an explosion.");
			metaStrs.add(ChatColor.WHITE + "When it is charged, let go of it,");
			metaStrs.add(ChatColor.WHITE + "and wait for the explosion,");
			metaStrs.add(ChatColor.WHITE + "or click to detonate it in midair.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(32);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_RED + "Lightning");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.RED + " " + ChatColor.ITALIC + "deadly force");
			metaStrs.add("Hold sneak to charge up some lightning.");
			metaStrs.add("Once you pull it together");
			metaStrs.add("in the center, left click.");
			metaStrs.add("If it is not centered,");
			metaStrs.add("or you come out of stance,");
			metaStrs.add("You may be seriously harmed.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
		}
		// The Waterbending User Interface
		else if (bendingType.equals("water"))
		{
			bendingGUI.setItem(27, new ItemStack(Material.INK_SACK, 1, (short)0, (byte) 10));
			
			is = bendingGUI.getItem(27);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.WHITE + "Swimming");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.GREEN + " " + ChatColor.ITALIC + "On");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			bendingGUI.setItem(36, new ItemStack(Material.INK_SACK, 1, (short)0, (byte) 9));
			
			is = bendingGUI.getItem(36);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.WHITE + "Run on water");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.RED + " " + ChatColor.ITALIC + "Off");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			bendingGUI.setItem(35, new ItemStack(Material.INK_SACK, 1, (short)0, (byte) 9));
			
			is = bendingGUI.getItem(35);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.WHITE + "Toggle Temp");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.RED + " " + ChatColor.ITALIC + "melt");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			bendingGUI.setItem(10, new ItemStack(Material.RAW_FISH));
			bendingGUI.setItem(11, new ItemStack(Material.PISTON_BASE));
			bendingGUI.setItem(12, new ItemStack(Material.TRIPWIRE_HOOK));
			bendingGUI.setItem(13, new ItemStack(Material.STAINED_GLASS, 1, (short)0, (byte) 11));
			bendingGUI.setItem(14, new ItemStack(Material.LEASH));
			bendingGUI.setItem(15, new ItemStack(Material.ICE));
			bendingGUI.setItem(16, new ItemStack(Material.PACKED_ICE));
			
			bendingGUI.setItem(19, new ItemStack(Material.IRON_BLOCK));
			bendingGUI.setItem(20, new ItemStack(Material.GLASS));
			bendingGUI.setItem(21, new ItemStack(Material.CARPET, 1, (short)0, (byte) 11));
			bendingGUI.setItem(22, new ItemStack(Material.IRON_SWORD));
			bendingGUI.setItem(23, new ItemStack(Material.FIREBALL));
			bendingGUI.setItem(24, new ItemStack(Material.PRISMARINE_CRYSTALS));
			bendingGUI.setItem(25, new ItemStack(Material.BLAZE_ROD));

			
			is = bendingGUI.getItem(10);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_BLUE + "Blob");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.BLUE + " " + ChatColor.ITALIC + "here fishy fishy");
			metaStrs.add(ChatColor.WHITE + "Sneak to grab water.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(11);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_BLUE + "Splash");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.BLUE + " " + ChatColor.ITALIC + "push and pull");
			metaStrs.add(ChatColor.WHITE + "Sneak/click and drag.");
			metaStrs.add(ChatColor.WHITE + "Try in a boat!");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(12);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_BLUE + "Ice Crawler");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.BLUE + " " + ChatColor.ITALIC + "halt!");
			metaStrs.add(ChatColor.WHITE + "Sneak to trap someone");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(13);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_BLUE + "Heal");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.BLUE + " " + ChatColor.ITALIC + "fireproof");
			metaStrs.add(ChatColor.WHITE + "Sneak+click to heal yourself.");
			metaStrs.add(ChatColor.WHITE + "Click, then sneak to heal others.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(14);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_BLUE + "Water Whip");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.BLUE + " " + ChatColor.ITALIC + "shift your stance");
			metaStrs.add(ChatColor.WHITE + "Sneak with bucket or at water.");
			metaStrs.add(ChatColor.WHITE + "Click early to whip it.");
			metaStrs.add(ChatColor.WHITE + "Click late and it will hit you.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(15);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_BLUE + "Freeze");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.BLUE + " " + ChatColor.ITALIC + "Eskimo's furor");
			metaStrs.add(ChatColor.WHITE + "Click to toggle freeze and melt.");
			metaStrs.add(ChatColor.WHITE + "Sneak and wait to freeze or melt.");
			metaStrs.add(ChatColor.WHITE + "Try it underwater!");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(16);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_BLUE + "Ice Blast");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.BLUE + " " + ChatColor.ITALIC + "Master's trap");
			metaStrs.add(ChatColor.WHITE + "Sneak + Click on a source");
			metaStrs.add(ChatColor.WHITE + "to launch a massive ice wave.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(19);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_BLUE + "Shield");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.BLUE + " " + ChatColor.ITALIC + "sudden protection");
			metaStrs.add(ChatColor.WHITE + "Click or sneak with a bucket.");
			metaStrs.add(ChatColor.WHITE + "While active, sneak to shield.");
			metaStrs.add(ChatColor.WHITE + "Will auto-block arrows.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(20);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_BLUE + "Bubble");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.BLUE + " " + ChatColor.ITALIC + "breathe!");
			metaStrs.add(ChatColor.WHITE + "Click to toggle.");
			metaStrs.add(ChatColor.WHITE + "Water will flee before you.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(21);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_BLUE + "Tsunami");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.BLUE + " " + ChatColor.ITALIC + "ride the wave");
			metaStrs.add(ChatColor.WHITE + "Sneak to lift.");
			metaStrs.add(ChatColor.WHITE + "Click to launch.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(22);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_BLUE + "Blade");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.BLUE + " " + ChatColor.ITALIC + "slice and dice");
			metaStrs.add(ChatColor.WHITE + "Click to swing.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(23);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_BLUE + "Blast");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.BLUE + " " + ChatColor.ITALIC + "take this!");
			metaStrs.add(ChatColor.WHITE + "Sneak while standing on a source.");
			metaStrs.add(ChatColor.WHITE + "Click when charged.");
			metaStrs.add(ChatColor.WHITE + "Click to toggle water/ice.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(24);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_BLUE + "Manipulate");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.BLUE + " " + ChatColor.ITALIC + "Broken");
			metaStrs.add(ChatColor.WHITE + "X X X");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(25);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_BLUE + "Twister");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.BLUE + " " + ChatColor.ITALIC + "Master's offense");
			metaStrs.add(ChatColor.WHITE + "Click in deep water to activate.");
			metaStrs.add(ChatColor.WHITE + "While active, space to rise.");
			metaStrs.add(ChatColor.WHITE + "Click to send blast.");
			metaStrs.add(ChatColor.WHITE + "Sneak on descent to send outward.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
		}
		// The Airbending User Interface
		else if (bendingType.equals("air"))
		{
			bendingGUI.setItem(11, new ItemStack(Material.DIAMOND_SWORD));
			bendingGUI.setItem(12, new ItemStack(Material.PISTON_BASE));
			bendingGUI.setItem(13, new ItemStack(Material.MINECART));
			bendingGUI.setItem(14, new ItemStack(Material.DIAMOND_CHESTPLATE));
			bendingGUI.setItem(15, new ItemStack(Material.STRING));

			is = bendingGUI.getItem(11);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_GRAY + "Blade");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.GRAY + " " + ChatColor.ITALIC + "Airbending slice!");
			metaStrs.add(ChatColor.WHITE + "Click to slice horizontally.");
			metaStrs.add(ChatColor.WHITE + "Sneak for vertical.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(12);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_GRAY + "Gust");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.GRAY + " " + ChatColor.ITALIC + "*whoosh*");
			metaStrs.add(ChatColor.WHITE + "Click to blow air.");
			metaStrs.add(ChatColor.WHITE + "Sneak to focus it and deal damage.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(13);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_GRAY + "Scooter");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.GRAY + " " + ChatColor.ITALIC + "wheeee.");
			metaStrs.add(ChatColor.WHITE + "Sneak while there's air under you (jump)");
			metaStrs.add(ChatColor.WHITE + "If there's room, you'll make a scooter.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(14);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_GRAY + "Shield");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.GRAY + " " + ChatColor.ITALIC + "Defend");
			metaStrs.add(ChatColor.WHITE + "Click to toggle.");
			metaStrs.add(ChatColor.WHITE + "Sneak to descend.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(15);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_GRAY + "Twister");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.GRAY + " " + ChatColor.ITALIC + "TORNADO!");
			metaStrs.add(ChatColor.WHITE + "While sneaking, turn around in circles.");
			metaStrs.add(ChatColor.WHITE + "Let go to launch.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
		}
		// The Earthbending User Interface
		else if (bendingType.equals("earth"))
		{
			bendingGUI.setItem(10, new ItemStack(Material.IRON_SPADE));
			bendingGUI.setItem(11, new ItemStack(Material.COBBLE_WALL));
			bendingGUI.setItem(12, new ItemStack(Material.NETHERRACK));
			bendingGUI.setItem(13, new ItemStack(Material.WATER_BUCKET));
			bendingGUI.setItem(14, new ItemStack(Material.SAND));
			bendingGUI.setItem(15, new ItemStack(Material.PISTON_BASE));
			bendingGUI.setItem(16, new ItemStack(Material.GRASS));

			is = bendingGUI.getItem(10);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_GREEN + "Dig");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.GREEN + " " + ChatColor.ITALIC + "get your hands dirty");
			metaStrs.add(ChatColor.WHITE + "Click to dig much faster!");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(11);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_GREEN + "Pillar");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.GREEN + " " + ChatColor.ITALIC + "time to rumble!");
			metaStrs.add(ChatColor.WHITE + "Click the ground while sneaking.");
			metaStrs.add(ChatColor.WHITE + "Release to pull the earth up.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(12);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_GREEN + "Fissure");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.GREEN + " " + ChatColor.ITALIC + "shake time");
			metaStrs.add(ChatColor.WHITE + "Click to launch a fissure.");
			metaStrs.add(ChatColor.WHITE + "Add sneak to dig one.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(13);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_GREEN + "Tsunami");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.GREEN + " " + ChatColor.ITALIC + "wheeee.");
			metaStrs.add(ChatColor.WHITE + "Sneak to lift the earth.");
			metaStrs.add(ChatColor.WHITE + "Click to launch a wave.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(14);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_GREEN + "Sand Trap");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.GREEN + " " + ChatColor.ITALIC + "gotcha!");
			metaStrs.add(ChatColor.WHITE + "Sneak+click a target to trip them.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(15);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_GREEN + "Push");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.GREEN + " " + ChatColor.ITALIC + "blast!");
			metaStrs.add(ChatColor.WHITE + "Click to push earth.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
			
			is = bendingGUI.getItem(16);
			meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_GREEN + "Launch");
			metaStrs = new ArrayList<String>();
			metaStrs.add(ChatColor.GREEN + " " + ChatColor.ITALIC + "Take this!");
			metaStrs.add(ChatColor.WHITE + "Click the earth while sneaking.");
			metaStrs.add(ChatColor.WHITE + "Then click the blocks you have grabbed.");
			meta.setLore(metaStrs);
			is.setItemMeta(meta);
		}
		
		/*
		 //* FOUR BENDING PATTERNS
		
		ChatColor.DARK_RED;
		ChatColor.RED;
		
		ChatColor.DARK_GREEN;
		ChatColor.GREEN;
		
		ChatColor.DARK_BLUE;
		ChatColor.BLUE;
		
		ChatColor.DARK_GRAY;
		ChatColor.GRAY;
		*/
	}
	
	public void showBendingGUI()
	{
		player = Bukkit.getPlayer(uuid);
		player.openInventory(bendingGUI);
	}
	public Inventory getBendingGUI()
	{
		return bendingGUI;
	}
	
	public static void delete(UUID uuid) {
		players.remove(uuid);
	}
	
	private Vector<Integer> getKeys() {
		return keys;
	}
	public static Vector<Integer> getKeys(UUID uuid) {
		if (players.containsKey(uuid)) {
			return players.get(uuid).getKeys();
		}
		return null;
	}
	
	public static boolean isBender(UUID uuid)
	{
		return players.containsKey(uuid);
	}
	public static boolean isBender(Player player)
	{
		return players.containsKey(player.getUniqueId());
	}
	public static String getBendingType(Player player)
	{
		if (players.containsKey(player.getUniqueId()))
			return players.get(player.getUniqueId()).bendingType;
		else
			return "";
	}
	
	public static BendingPlayer getBendingPlayer(UUID uuid)
	{
		if (players.containsKey(uuid)) 	
			return players.get(uuid);
		else
			return null;
	}
	
	public static BendingPlayer getBendingPlayer(Player player)
	{
		UUID uuid = player.getUniqueId();
		if (players.containsKey(uuid)) 	
			return players.get(uuid);
		else
			return null;
	}
	
	public static void progressAll() {
		if (players.size() > 0)
			for (UUID id : players.keySet())
				players.get(id).progress();
	}
	public static boolean progress(UUID id) {
		if (players.get(id) != null)
			return players.get(id).progress();
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public boolean progress() {
		
		player = Bukkit.getPlayer(uuid);
		
		/*
		PacketPlayOutChat packet = new PacketPlayOutChat(
      		  ChatSerializer.a("{ text: \"" + "************************************" + "\" }"), (byte) 2);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
		*/
		
		if (swimmingEnabled)
		{
			
			if( player != null && bendingType.equals("water") && player.isSneaking() 
				 && (Tools.isWater(player.getLocation().getBlock().getType())) 
				 && !WaterExtinguish.instances.containsKey(player)
				 && !WaterBlob.instances.containsKey(player)
				 && !IceCrawler.instances.containsKey(player)
				 && !WaterTsunami.instances.containsKey(player)
				 && !WaterTwister.instances.containsKey(player)
				 && !IceChange.instances.containsKey(player)
				 && !IceGrowth.instances.containsKey(player)
				 && !WaterBlast.instances.containsKey(player)
				 && !WaterBlade.instances.containsKey(player)
				 && !IceShield.instances.containsKey(player) 
				 )
			{
				org.bukkit.util.Vector v = player.getEyeLocation().getDirection().clone().normalize().multiply(0.8);
				if (player.isOnGround())
					v.setY(0.01);
				player.setVelocity(v);
			}
		}
		
		if (waterRunEnabled)
		{
			if( player != null && bendingType.equals("water"))
			{
				if (waterRunBlocks.size() > 0)
				{
					for (Block b : waterRunBlocks)
						b.setType(Material.WATER);
					waterRunBlocks.clear();
				}
				if (player.isSprinting() 
					&& Tools.waterBreaks(player.getLocation().getBlock())
					&& Tools.waterBreaks(player.getEyeLocation().getBlock())
					&& !Tools.isOfWater(player.getLocation().getBlock())
					&& !Tools.isOfWater(player.getEyeLocation().getBlock())
					&& Tools.isWater(player.getLocation().getBlock().getRelative(0,-1,0).getType()))
				{
					loc = player.getLocation().getBlock().getRelative(0,-1,0).getLocation();
					loc.setYaw(player.getEyeLocation().getYaw());
					loc.setPitch(0);
					bit = new BlockIterator(loc, 0.0, 1);
					while (bit.hasNext())
					{
						next = bit.next();
						if (Tools.isWater(next.getType()) && (next.getData() < 2 || next.getData() > 7))
							waterRunBlocks.add(next);
					}
					for (Block b : waterRunBlocks)
						b.setType(Material.ICE);
				}
			}
				
				
		}
		
		
		
		
		if(bendingType.equals("air"))
		{
			player.setAllowFlight(true); // no damage sound
			if (player.isSprinting())
			{
				if (!player.isOnGround()) // allow fast footsteps!
					player.setFlying(true);
				org.bukkit.util.Vector v = player.getEyeLocation().getDirection().clone().normalize().multiply(0.9);
				player.setVelocity(v);
			}
			else
			{
				if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE)
				{
					if (!AirShield.instances.containsKey(player))
						player.setFlying(false);
				}
					
			}
			
		}
		return true;
	}
	
	/*
	 *  for PlayerAction,
	 *  - 0 = shift down
	 *  - 1 = shift up
	 *  - 2 = left click
	 */
	public void add(UUID uuid, PlayerAction action)
	{
		if (players.containsKey(uuid)) {
			getBendingPlayer(uuid).addToList(action.val());
		}
	}
	public void add(UUID uuid, int move)
	{
		if (players.containsKey(uuid)) {
			getBendingPlayer(uuid).addToList(move);
		}
	}
	private void addToList(int move)
	{
		keys.add(move);
		tryMove(uuid);
	}
	
	public void startTimer()
	{
		isRunning = true;
	}
	
	public int[] getList()
	{
		int[] list = new int[keys.size()];
		for (int i = 0; i < keys.size(); i++)
			list[i] = keys.get(i);
		return list;	
	}
	public void resetList()
	{
		keys = new Vector<Integer>();
	}
	public String getListStr()
	{
		String list = "";
		for (int i = 0; i < keys.size(); i++)
			list.concat(keys.get(i).toString() + ",");
		return list;	
	}
	
	/*
	 * tryMove(player) 
	 * * This method attempts to run moves
	 */
	public void tryMove(UUID uuid)
	{		
		Player player = Bukkit.getServer().getPlayer(uuid);
		if (!BendingPlayer.isBender(uuid) || keys == null
				|| player.getItemInHand().getType() == Material.BOW)
			return;
		//java.util.Vector<Integer> keys = BendingPlayer.getKeys(player.getUniqueId());
		/*
		if (WaterStreaming.hasPlayer(player))
		{
			new WaterStreaming(player);
			return;
		}
		*/
			
		int slot = player.getInventory().getHeldItemSlot() + 1;
		String form = Bending.playersConfig.getString(uuid + "." + Tools.getBendingType(player) + ".slot" + slot);
		if (form == null || !bendingIsEnabled)	// Do nothing if no move is bound or moves off
			return;
		
		
		
		//preempt the vehicle check for this one
		if (form.equals("watersplash"))
			new WaterSplash(player);
		
		if (player.isInsideVehicle())
			return;
		
		// FIRE
		if (form.equals("fireball"))
			new FireBall(player);
		if (form.equals("firejet"))
			new FireJet(player);
		if (form.equals("fireemit"))
			new FireEmit(player);
		if (form.equals("firewave"))
			new FireWave(player);
		if (form.equals("firebeam"))
			new FireBeam(player);
		if (form.equals("firespinkick"))
			new FireSpinKick(player);
		if (form.equals("fireline"))
			new FireLine(player);
		if (form.equals("firewall"))
			new FireWall(player);
		if (form.equals("lightning"))
			new Lightning(player);
		if (form.equals("firelaunch"))
			new FireLaunch(player);
		if (form.equals("combustion"))
			new Combustion(player);
		if (form.equals("fireheat"))
			new FireHeat(player);
		if (form.equals("fireshield"))
			new FireShield(player);
		if (form.equals("fireblade"))
			new FireBlade(player);
		if (form.equals("firecool"))
			new FireCool(player);
		if (form.equals("fireblast"))
			new FireBlast(player);

		// WATER
		if (form.equals("waterblob"))
			new WaterBlob(player);
		if (form.equals("waterwhip"))
			new WaterWhip(player);
		if (form.equals("icechange"))
			new IceChange(player);
		if (form.equals("icecrawler"))
			new IceCrawler(player);
		if (form.equals("watertwister"))
			new WaterTwister(player);
		if (form.equals("watertsunami"))
			new WaterTsunami(player);
		if (form.equals("iceshield"))
			new IceShield(player);
		if (form.equals("waterstreaming"))
			new WaterStreaming(player);
		if (form.equals("waterblade"))
			new WaterBlade(player);
		if (form.equals("waterextinguish"))
			new WaterExtinguish(player);
		if (form.equals("icegrowth"))
			new IceGrowth(player);
		if (form.equals("waterblast"))
			new WaterBlast(player);
		if (form.equals("waterbubble"))
			new WaterBubble(player);
		
		
		// AIR
		if (form.equals("airgust"))
			new AirGust(player);
		if (form.equals("airblade"))
			new AirBlade(player);
		if (form.equals("airscooter"))
			new AirScooter(player);
		if (form.equals("airshield"))
			new AirShield(player);
		if (form.equals("airtwister"))
			new AirTwister(player);
		
		// EARTH
		if (form.equals("earthdig"))
			new EarthDig(player);
		if (form.equals("sandtarget"))
			new SandTarget(player);
		if (form.equals("earthpillar"))
			new EarthPillar(player);
		if (form.equals("earthfissure"))
			new EarthFissure(player);
		if (form.equals("earthtsunami"))
			new EarthTsunami(player);
		//if (form.equals("earthgrab"))
			//new EarthGrab(player);
		if (form.equals("earthpush"))
			new EarthPush(player);
		if (form.equals("earthlaunch"))
			new EarthLaunch(player);
	}
	
	
	public static void reset(UUID uuid) {
		//The following works:
		//Bukkit.getLogger().info(Integer.toString(getPlayerMoveTimer(name).keys.get(0)));
		Bukkit.getServer().getPlayer(uuid).sendMessage(Integer.toString(getBendingPlayer(uuid).getList().length));
		getBendingPlayer(uuid).resetList();
		Bukkit.getServer().getPlayer(uuid).sendMessage("Removed!");
		getBendingPlayer(uuid).isRunning = false;
	}
	
}
