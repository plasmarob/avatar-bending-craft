	package me.plasmarob.bending;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Tools {
	Bending plugin;
	public Tools(Bending plugin) {
		this.plugin = plugin;
	}
	
	/*
	 * getBendingType()
	 *
	 */
	public static String getBendingType(Player player)
	{
		return Bending.playersConfig.getString(player.getUniqueId() + ".bendingType");
	}
	
	public static int lastKey(Player player)
	{
		return BendingPlayer.getKeys(player.getUniqueId()).lastElement();
	}
	
	public static boolean setBind(Player player, String args)
	{
		String[] str = new String[1];
		str[0] = args;
		return setBind(player, str);
	}
	
	/**
	 * setBind(player, string)
	 * * This method binds a move to a slot.
	 */
	public static boolean setBind(Player player, String[] args)
	{
		//String name = player.getName().toString().toLowerCase();
		UUID uuid = player.getUniqueId();
		
		if (args.length == 0 || Bending.playersConfig.getString(uuid.toString()) == null)
			return false;
		
		String form = args[0].toLowerCase();
		int slot = player.getInventory().getHeldItemSlot() + 1;
		
		String bendingType = Tools.getBendingType(player);
		
		if (bendingType.equalsIgnoreCase("air") && (BendingAirString.contains(form)))
		{
			 player.sendMessage(ChatColor.YELLOW + form + " bound to slot " + slot + ".");
			 Bending.playersConfig.set(uuid + "." + getBendingType(player) + ".slot" + slot, form);
		}
		else if (bendingType.equalsIgnoreCase("water") && (BendingWaterString.contains(form)))
		{
			player.sendMessage(ChatColor.DARK_BLUE + form + " bound to slot " + slot + ".");
			Bending.playersConfig.set(uuid + "." + getBendingType(player) + ".slot" + slot, form);
		}
		else if (bendingType.equalsIgnoreCase("earth") && (BendingEarthString.contains(form)))
		{
			player.sendMessage(ChatColor.GREEN + form + " bound to slot " + slot + ".");
			Bending.playersConfig.set(uuid + "." + getBendingType(player) + ".slot" + slot, form);
		}
		else if (bendingType.equalsIgnoreCase("fire") && (BendingFireString.contains(form)))
		{
			player.sendMessage(ChatColor.DARK_RED + form + " bound to slot " + slot + ".");
			Bending.playersConfig.set(uuid + "." + getBendingType(player) + ".slot" + slot, form);
		}
		else
		{
			player.sendMessage(ChatColor.RED + form + " is not a valid move.");
			return false;
		}
		return true;
	}

	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// bending behavioral block categories
	
	public static Integer[] nonOpaque = { 0, 6, 8, 9, 10, 11, 27, 28, 30, 31,
		32, 37, 38, 39, 40, 50, 51, 55, 59, 66, 68, 69, 70, 72, 75, 76, 77,
		78, 83, 90, 93, 94, 104, 105, 106, 111, 115, 119, 127, 131, 132 };
	@SuppressWarnings("deprecation")
	public static boolean isSolid(Block block) {
		if (Arrays.asList(nonOpaque).contains(block.getTypeId()))
			return false;
		return true;
	}
	
	
	// Shows top as if air is on top of it
	// think "under the fence"
	public static boolean showsBelow(Block block) {
		Material mat = block.getType();
		return ((mat == Material.AIR) 
		|| (mat == Material.SAPLING)
		|| (mat == Material.LONG_GRASS)
		|| (mat == Material.DEAD_BUSH)
		|| (mat == Material.YELLOW_FLOWER)
		|| (mat == Material.RED_ROSE)

		|| (mat == Material.RED_MUSHROOM)
		|| (mat == Material.BROWN_MUSHROOM)
		|| (mat == Material.TORCH)
		|| (mat == Material.FIRE)
		|| (mat == Material.REDSTONE)
		|| (mat == Material.WHEAT)
		|| (mat == Material.CROPS)
		|| (mat == Material.LADDER)
		|| (mat == Material.RAILS)
		|| (mat == Material.SIGN)
		|| (mat == Material.STONE_PLATE)
		|| (mat == Material.WOOD_PLATE)
		|| (mat == Material.REDSTONE_TORCH_OFF)
		|| (mat == Material.REDSTONE_TORCH_ON)
		|| (mat == Material.STONE_BUTTON)
		|| (mat == Material.SNOW)
		|| (mat == Material.SUGAR_CANE_BLOCK)
		|| (mat == Material.FENCE)
		|| (mat == Material.DIODE_BLOCK_OFF)
		|| (mat == Material.DIODE_BLOCK_ON)
		|| (mat == Material.TRAP_DOOR)
		|| (mat == Material.IRON_FENCE)
		|| (mat == Material.PUMPKIN_STEM)
		|| (mat == Material.MELON_STEM)
		|| (mat == Material.VINE)
		|| (mat == Material.FENCE_GATE)
		|| (mat == Material.WATER_LILY)
		|| (mat == Material.NETHER_FENCE)
		|| (mat == Material.NETHER_STALK)
		|| (mat == Material.NETHER_WARTS)
		|| (mat == Material.COCOA)
		|| (mat == Material.TRIPWIRE)
		|| (mat == Material.TRIPWIRE_HOOK)
		|| (mat == Material.CARROT_STICK)
		|| (mat == Material.CARROT)
		|| (mat == Material.POTATO)
		|| (mat == Material.WOOD_BUTTON)
		|| (mat == Material.IRON_PLATE)
		|| (mat == Material.GOLD_PLATE)
		|| (mat == Material.REDSTONE_COMPARATOR_OFF)
		|| (mat == Material.REDSTONE_COMPARATOR_ON)
		|| (mat == Material.DAYLIGHT_DETECTOR)
		|| (mat == Material.ACTIVATOR_RAIL)
		|| (mat == Material.IRON_TRAPDOOR)
		|| (mat == Material.WATCH)
		|| (mat == Material.DAYLIGHT_DETECTOR_INVERTED)
		|| (mat == Material.SPRUCE_FENCE_GATE)
		|| (mat == Material.BIRCH_FENCE_GATE)
		|| (mat == Material.JUNGLE_FENCE_GATE)
		|| (mat == Material.DARK_OAK_FENCE_GATE)
		|| (mat == Material.ACACIA_FENCE_GATE)
		|| (mat == Material.SPRUCE_FENCE)
		|| (mat == Material.BIRCH_FENCE)
		|| (mat == Material.JUNGLE_FENCE)
		|| (mat == Material.DARK_OAK_FENCE)
		|| (mat == Material.ACACIA_FENCE)
		|| (mat == Material.ITEM_FRAME)
		|| (mat == Material.DOUBLE_PLANT)
		|| (mat == Material.DETECTOR_RAIL)
		|| (mat == Material.SIGN_POST)
		|| (mat == Material.WALL_SIGN));
	}
	
	public static boolean firePasses(Block block) {
		Material mat = block.getType();
		return ((mat == Material.AIR) 
		|| (mat == Material.SAPLING)
		|| (mat == Material.LONG_GRASS)
		|| (mat == Material.DEAD_BUSH)
		|| (mat == Material.YELLOW_FLOWER)
		|| (mat == Material.RED_ROSE)

		|| (mat == Material.RED_MUSHROOM)
		|| (mat == Material.BROWN_MUSHROOM)
		|| (mat == Material.TORCH)
		|| (mat == Material.FIRE)
		|| (mat == Material.REDSTONE)
		|| (mat == Material.WHEAT)
		|| (mat == Material.CROPS)
		|| (mat == Material.LADDER)
		|| (mat == Material.RAILS)
		|| (mat == Material.SIGN)
		|| (mat == Material.STONE_PLATE)
		|| (mat == Material.WOOD_PLATE)
		|| (mat == Material.REDSTONE_TORCH_OFF)
		|| (mat == Material.REDSTONE_TORCH_ON)
		|| (mat == Material.STONE_BUTTON)
		|| (mat == Material.SNOW)
		|| (mat == Material.SUGAR_CANE_BLOCK)
		|| (mat == Material.FENCE)
		|| (mat == Material.DIODE_BLOCK_OFF)
		|| (mat == Material.DIODE_BLOCK_ON)
		|| (mat == Material.TRAP_DOOR)
		|| (mat == Material.IRON_FENCE)
		|| (mat == Material.PUMPKIN_STEM)
		|| (mat == Material.MELON_STEM)
		|| (mat == Material.VINE)
		|| (mat == Material.FENCE_GATE)
		|| (mat == Material.WATER_LILY)
		|| (mat == Material.NETHER_FENCE)
		|| (mat == Material.NETHER_STALK)
		|| (mat == Material.NETHER_WARTS)
		|| (mat == Material.COCOA)
		|| (mat == Material.TRIPWIRE)
		|| (mat == Material.TRIPWIRE_HOOK)
		|| (mat == Material.CARROT_STICK)
		|| (mat == Material.CARROT)
		|| (mat == Material.POTATO)
		|| (mat == Material.WOOD_BUTTON)
		|| (mat == Material.IRON_PLATE)
		|| (mat == Material.GOLD_PLATE)
		|| (mat == Material.REDSTONE_COMPARATOR_OFF)
		|| (mat == Material.REDSTONE_COMPARATOR_ON)
		|| (mat == Material.DAYLIGHT_DETECTOR)
		|| (mat == Material.ACTIVATOR_RAIL)
		|| (mat == Material.IRON_TRAPDOOR)
		|| (mat == Material.WATCH)
		|| (mat == Material.DAYLIGHT_DETECTOR_INVERTED)
		|| (mat == Material.SPRUCE_FENCE_GATE)
		|| (mat == Material.BIRCH_FENCE_GATE)
		|| (mat == Material.JUNGLE_FENCE_GATE)
		|| (mat == Material.DARK_OAK_FENCE_GATE)
		|| (mat == Material.ACACIA_FENCE_GATE)
		|| (mat == Material.SPRUCE_FENCE)
		|| (mat == Material.BIRCH_FENCE)
		|| (mat == Material.JUNGLE_FENCE)
		|| (mat == Material.DARK_OAK_FENCE)
		|| (mat == Material.ACACIA_FENCE)
		|| (mat == Material.ITEM_FRAME)
		|| (mat == Material.DOUBLE_PLANT)
		|| (mat == Material.DETECTOR_RAIL)
		|| (mat == Material.SIGN_POST)
		|| (mat == Material.WALL_SIGN));
	}
	
	public static boolean waterBreaks(Block block) {
		Material mat = block.getType();
		return ((mat == Material.AIR) 
		|| (mat == Material.SAPLING)
		|| (mat == Material.LONG_GRASS)
		|| (mat == Material.DEAD_BUSH)
		|| (mat == Material.YELLOW_FLOWER)
		|| (mat == Material.RED_ROSE)
		|| (mat == Material.RED_MUSHROOM)
		|| (mat == Material.BROWN_MUSHROOM)
		|| (mat == Material.TORCH)
		|| (mat == Material.FIRE)
		|| (mat == Material.REDSTONE)
		|| (mat == Material.WHEAT)
		|| (mat == Material.CROPS)
		|| (mat == Material.REDSTONE_TORCH_OFF)
		|| (mat == Material.REDSTONE_TORCH_ON)
		|| (mat == Material.STONE_BUTTON)
		|| (mat == Material.SNOW)
		|| (mat == Material.SUGAR_CANE_BLOCK)
		|| (mat == Material.DIODE_BLOCK_OFF)
		|| (mat == Material.DIODE_BLOCK_ON)
		|| (mat == Material.PUMPKIN_STEM)
		|| (mat == Material.MELON_STEM)
		|| (mat == Material.VINE)
		|| (mat == Material.WATER_LILY)
		|| (mat == Material.NETHER_STALK)
		|| (mat == Material.NETHER_WARTS)
		|| (mat == Material.COCOA)
		|| (mat == Material.TRIPWIRE)
		|| (mat == Material.TRIPWIRE_HOOK)
		|| (mat == Material.CARROT_STICK)
		|| (mat == Material.CARROT)
		|| (mat == Material.POTATO)
		|| (mat == Material.WOOD_BUTTON)
		|| (mat == Material.REDSTONE_COMPARATOR_OFF)
		|| (mat == Material.REDSTONE_COMPARATOR_ON)
		|| (mat == Material.ITEM_FRAME)
		|| (mat == Material.DOUBLE_PLANT)
		);
	}
	
	
	public static boolean isWater(Material mat)
	{
		return mat == Material.WATER || mat == Material.STATIONARY_WATER;
	}
	public static boolean isSnow(Material mat)
	{
		return mat == Material.SNOW || mat == Material.SNOW_BLOCK;
	}
	public static boolean isIce(Material mat)
	{
		return mat == Material.ICE || mat == Material.PACKED_ICE;
	}
	public static boolean isOfWater(Block block) {
		return (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER 
				|| block.getType() == Material.ICE || block.getType() == Material.PACKED_ICE
				|| block.getType() == Material.SNOW || block.getType() == Material.SNOW_BLOCK);
	}
	
	public static boolean isUnbreakable(Block block) {
		return (block.getType() == Material.OBSIDIAN || block.getType() == Material.BEDROCK
				|| block.getType() == Material.IRON_BLOCK);
	}
	
	
	public static boolean locationsMatch(Location l1, Location l2)
	{
		Block b1 = l1.getBlock();
		Block b2 = l2.getBlock();
		return (b1.getX() == b2.getX() && b1.getY() == b2.getY() && b1.getZ() == b2.getZ());
	}
	
	public static Integer[] transparentEarthbending = { 0, 6, 8, 9, 10, 11, 30,
		31, 32, 37, 38, 39, 40, 50, 51, 59, 78, 83, 106 };
	@SuppressWarnings("deprecation")
	public static boolean isTransparentToEarthbending(Player player, Block block) {
		if (Arrays.asList(transparentEarthbending).contains(block.getTypeId()))
			return true;
		return false;
	}
	
	
	public static boolean isStopper(Block block) {
		Material material = block.getType();
		if ((material == Material.WOODEN_DOOR)
		|| (material == Material.BREWING_STAND)
		|| (material == Material.FENCE)
		|| (material == Material.FENCE_GATE)
		|| (material == Material.GLASS)
		|| (material == Material.LAVA)
		|| (material == Material.NETHER_FENCE)
		|| (material == Material.THIN_GLASS)
		|| (material == Material.WATER)
		|| (material == Material.STATIONARY_LAVA)
		|| (material == Material.STATIONARY_WATER)
		) {
			return true;
		}
		return false;
	}

	public static boolean isCrushable(Block block) {
		Material material = block.getType();
		if ((material == Material.AIR) || (material == Material.LONG_GRASS)
		|| (material == Material.YELLOW_FLOWER)
		|| (material == Material.SAPLING)
		|| (material == Material.WOODEN_DOOR)
		|| (material == Material.BREWING_STAND)
		|| (material == Material.BROWN_MUSHROOM)
		|| (material == Material.CARROT)
		|| (material == Material.DEAD_BUSH)
		|| (material == Material.DOUBLE_PLANT)
		|| (material == Material.CROPS)
		|| (material == Material.FENCE)
		|| (material == Material.FENCE_GATE)
		//|| (material == Material.GLASS)
		|| (material == Material.ITEM_FRAME)
		|| (material == Material.LADDER)
		|| (material == Material.LAVA)
		|| (material == Material.LEAVES)
		|| (material == Material.LEAVES_2)
		|| (material == Material.MELON_STEM)
		|| (material == Material.NETHER_FENCE)
		|| (material == Material.NETHER_STALK)
		|| (material == Material.NETHER_WARTS)
		|| (material == Material.PAINTING)
		|| (material == Material.LEVER)
		|| (material == Material.POTATO)
		|| (material == Material.PUMPKIN_STEM)
		|| (material == Material.RED_MUSHROOM)
		|| (material == Material.RED_ROSE)
		|| (material == Material.SIGN)
		|| (material == Material.SIGN_POST)
		|| (material == Material.SKULL)
		|| (material == Material.SUGAR_CANE_BLOCK)
		|| (material == Material.STONE_BUTTON)
		|| (material == Material.THIN_GLASS)
		|| (material == Material.TORCH)
		|| (material == Material.TRIPWIRE)
		|| (material == Material.VINE)
		|| (material == Material.WHEAT)
		|| (material == Material.WEB)
		|| (material == Material.WALL_SIGN)
		|| (material == Material.WATER_LILY)
		|| (material == Material.WOOD_BUTTON)
		|| (material == Material.WATER)
		|| (material == Material.STATIONARY_LAVA)
		|| (material == Material.STATIONARY_WATER)
		) {
			return true;
		}
		return false;
	}
	
	
	public static ArrayList<Block> getTouchingBlocks(Block b)
	{
		ArrayList<Block> blocks = new ArrayList<Block>();
		blocks.add(b.getRelative(1,0,0));
		blocks.add(b.getRelative(0,1,0));
		blocks.add(b.getRelative(0,0,1));
		blocks.add(b.getRelative(-1,0,0));
		blocks.add(b.getRelative(0,-1,0));
		blocks.add(b.getRelative(0,0,-1));
		return blocks;
	}
	public static int countNearbyEarthBendables(Block block, int range)
	{
		ArrayList<Block> growingBlocks = new ArrayList<Block>();
		growingBlocks.add(block);
		
		for (int i = 0; i < range; i++)
		{
			ArrayList<Block> temp = new ArrayList<Block>();
			for (Block b : growingBlocks)
			{
				for(Block bb : Tools.getTouchingBlocks(b))
				{
					if (isEarthbendable(bb) && !growingBlocks.contains(bb))
						temp.add(bb);
				}
				
			}
			growingBlocks.addAll(temp);
		}
		return growingBlocks.size();
	}
	
	public static boolean isEarthbendable(Block block) {
		Material material = block.getType();
		if ((material == Material.STONE) || (material == Material.CLAY)
		|| (material == Material.COAL_ORE)
		|| (material == Material.DIAMOND_ORE)
		|| (material == Material.DIRT)
		|| (material == Material.GOLD_ORE)
		|| (material == Material.GRASS)
		|| (material == Material.GRAVEL)
		|| (material == Material.IRON_ORE)
		|| (material == Material.LAPIS_ORE)
		|| (material == Material.NETHERRACK)
		|| (material == Material.REDSTONE_ORE)
		|| (material == Material.SAND)
		|| (material == Material.SANDSTONE)
		|| (material == Material.COBBLESTONE)
		|| (material == Material.CLAY)
		|| (material == Material.HARD_CLAY)
		|| (material == Material.STAINED_CLAY)
		) {
			return true;
		}
		return false;
	}
	public static boolean isEarthbendable(Player player, Block block) {
		return isEarthbendable(block);
	}
	
	
	
	//////////////
	
	
	
	// returns whether a biome is "deserty"
	public static boolean canRain(Biome biome)
	{
		if (biome.equals(Biome.DESERT) ||
				biome.equals(Biome.DESERT_HILLS) ||
				biome.equals(Biome.DESERT_MOUNTAINS) ||
				biome.equals(Biome.SAVANNA) ||
				biome.equals(Biome.SAVANNA_MOUNTAINS) ||
				biome.equals(Biome.SAVANNA_PLATEAU) ||
				biome.equals(Biome.SAVANNA_PLATEAU_MOUNTAINS) ||
				biome.equals(Biome.MESA) ||
				biome.equals(Biome.MESA_BRYCE) ||
				biome.equals(Biome.MESA_PLATEAU) ||
				biome.equals(Biome.MESA_PLATEAU_FOREST) ||
				biome.equals(Biome.MESA_PLATEAU_FOREST_MOUNTAINS) ||
				biome.equals(Biome.MESA_PLATEAU_MOUNTAINS)
				)
			return false;
		return true;
	}
	
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// 2x2 block conversion for centering on corners
	
	public static ArrayList<Block> get2x2LocationBlocks(Location location)
	{
		ArrayList<Block> blocks = new ArrayList<Block>();
		Block b = get2x2CornerBlock(location);
		blocks.add(b);
		blocks.add(b.getRelative(1,0,0));
		blocks.add(b.getRelative(1,0,1));
		blocks.add(b.getRelative(0,0,1));
		return blocks;
	}
	public static Block get2x2CornerBlock(Location loc)
	{
		return get2x2CornerLocation(loc).getBlock();
	}
	public static Location get2x2CornerLocation(Location location)
	{
		Location loc = location.clone();
		double dx = loc.getX();
		double dz = loc.getZ();		
		int x = (int)dx;
		int z = (int)dz;	
		int x1 = 0;
		int z1 = 0;	
		if ((dx - x) < 0.5)
			x1--;
		if ((dz - z) < 0.5)
			z1--;
		loc.add(x1,0,z1);
		return loc;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// mobs and players in an area or near a location
	
	public static List<Entity> getMobsAroundPoint(Location location, double radius) {
		List<Entity> entities = getEntitiesAroundPoint(location, radius, 0);
		for (int i = 0 ; i < entities.size(); i++)
		{
			Entity e = entities.get(i);
			if ( !(e instanceof Player || e instanceof LivingEntity))
			{
				entities.remove(e);
				i--;
			}
		}
		return entities;
	}
	public static List<Entity> getEntitiesAroundPoint(Location location, double radius) {
		return getEntitiesAroundPoint(location, radius, 0);
	}
	public static List<Entity> getEntitiesAroundPoint(Location location,
			double radius, double minRadius) {
		List<Entity> entities = location.getWorld().getEntities();
		List<Entity> list = location.getWorld().getEntities();
		for (Entity entity : entities) {
			if (entity.getWorld() != location.getWorld()) {
				list.remove(entity);
			} else if (entity.getLocation().distance(location) > radius || 
					   entity.getLocation().distance(location) < minRadius) {
				list.remove(entity);
			}
		}
		return list;
	}
	public static List<Entity> getEntitiesAroundFromList(Location location,
			double radius, List<Entity> entities) {
		List<Entity> list = new ArrayList<Entity>();
		list.addAll(entities);
		for (Entity entity : entities) {
			if (entity.getWorld() != location.getWorld())
				list.remove(entity);
			else if (entity.getLocation().distance(location) > radius)
				list.remove(entity);
		}
		return list;
	}
	// for getting a vector pointing toward another location
	public static Vector getDirection(Location location, Location destination) {
		double x1, y1, z1;
		double x0, y0, z0;
		x1 = destination.getX();
		y1 = destination.getY();
		z1 = destination.getZ();
		x0 = location.getX();
		y0 = location.getY();
		z0 = location.getZ();
		return new Vector(x1 - x0, y1 - y0, z1 - z0);
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////
	// chat logger methods
	public static void say(Player p, int i)
	{
		p.sendMessage(Integer.toString(i));
	}
	public static void say(Player p, double i)
	{
		p.sendMessage(Double.toString(i));
	}
	public static void say(Player p, float i)
	{
		p.sendMessage(Double.toString(i));
	}
	public static void say(Player p, String s)
	{
		p.sendMessage(s);
	}
	public static void say(Player p, Boolean b)
	{
		p.sendMessage(Boolean.toString(b));
	}
	// The console logger methods
	public static void say(int i)
	{
		Bukkit.getLogger().info(Integer.toString(i));
	}
	public static void say(double i)
	{
		Bukkit.getLogger().info(Double.toString(i));
	}
	public static void say(float i)
	{
		Bukkit.getLogger().info(Double.toString(i));
	}
	public static void say(String s)
	{
		Bukkit.getLogger().info(s);
	}
	public static void say(Boolean b)
	{
		Bukkit.getLogger().info(Boolean.toString(b));
	}

	
}
