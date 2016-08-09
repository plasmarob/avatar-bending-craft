package me.plasmarob.bending;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

//import me.plasmarob.bending.firebending.FireEmitEffect;



import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_10_R1.boss.CraftBossBar;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.PolarBear;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;

import de.slikey.effectlib.EffectLib;
import de.slikey.effectlib.EffectManager;
import me.plasmarob.bending.util.Tools;
import net.minecraft.server.v1_10_R1.BossBattle;
import net.minecraft.server.v1_10_R1.DataWatcher;
import net.minecraft.server.v1_10_R1.EntityHuman;
import net.minecraft.server.v1_10_R1.EntityPig;
import net.minecraft.server.v1_10_R1.EntityPlayer;
import net.minecraft.server.v1_10_R1.IChatBaseComponent;
import net.minecraft.server.v1_10_R1.MinecraftServer;
import net.minecraft.server.v1_10_R1.NBTTagCompound;
import net.minecraft.server.v1_10_R1.Packet;
import net.minecraft.server.v1_10_R1.PacketPlayOutBlockChange;
import net.minecraft.server.v1_10_R1.PacketPlayOutBoss;
import net.minecraft.server.v1_10_R1.PacketPlayOutBoss.Action;
import net.minecraft.server.v1_10_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_10_R1.PacketPlayOutExplosion;
import net.minecraft.server.v1_10_R1.PacketPlayOutMount;
import net.minecraft.server.v1_10_R1.PacketPlayOutMultiBlockChange;
import net.minecraft.server.v1_10_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_10_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_10_R1.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_10_R1.PlayerInteractManager;
import net.minecraft.server.v1_10_R1.WorldServer;

/**
 * Main class for the Avatar Bending Plugin
 * 
 * @author      Robert Thorne <plasmarob@gmail.com>
 * @version     0.4                
 * @since       2014-04-20
 */
public class Bending extends JavaPlugin {
	// Primary objects 
	public static Bending plugin;
	public static BendingManager manager;

	// Player Listener 
	public final PlayerListener listener = new PlayerListener(this);
	// Effects Library
    private static EffectManager effectManager; 
	
	// YAML file user config objects
	File usersFile;
	FileConfiguration usersConfig;
	File playersFile;
	public static FileConfiguration playersConfig;
	
	
	//private ProtocolManager pm = ProtocolLibrary.getProtocolManager();
	
	
	/**
	 * onEnable()
	 * * Loads the Plugin
	 * * sets up objects, thread, effects manager, listener, and YAML
	 * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
	 */
	@Override
	public void onEnable()
	{
		// Primary object setups
		plugin = this;
		
		manager = new BendingManager();
		// Create the thread that updates every tick
		getServer().getScheduler().scheduleSyncRepeatingTask(this, manager, 0, 1);
		
		// Register the Player listenter
		Bukkit.getServer().getPluginManager().registerEvents(listener, this);
		
		// Create EffectManager for special effects library
		EffectLib lib = EffectLib.instance();
		effectManager = new EffectManager(lib);

		// Setup the YAML /////////////////////////////
		
		// Create files in memory
		playersFile = new File(getDataFolder(), "playersConfig.yml");
		usersFile = new File(getDataFolder(), "users.yml");
		try {
	        firstRun();	// Create files on disk if necessary
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		// Create the YAML Config in memory
		playersConfig = new YamlConfiguration();
		usersConfig = new YamlConfiguration();
		loadYamls();	// Load the config from the files
		Tools.say("Bending 0.4 Loaded!");
	}
	
	/**
	 * onDisable()
	 * Closes down effects manager and saves YAML to file
	 * @see org.bukkit.plugin.java.JavaPlugin#onDisable()
	 */
	@Override
	public void onDisable()
	{
		effectManager.dispose();	// Dump the effects manager
		saveYamls();	// Save the config to the files
		BendingManager.removeAllBending();
	}

	

	/*
	public static Packet addPacket(String displayName) {
	    UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + displayName).getBytes(Charsets.UTF_8));
	    EntityPlayer pl = new EntityPlayer(
	            MinecraftServer.getServer(),
	            MinecraftServer.getServer().getWorldServer(0),
	            (GameProfile) (new WrappedGameProfile(uuid, displayName)).getHandle(),
	            new PlayerInteractManager(MinecraftServer.getServer().getWorldServer(0))
	    );
	    PacketPlayOutPlayerInfo pi
	            = new PacketPlayOutPlayerInfo(
	                    PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, pl);
	    return pi;
	}
	
	
	public static Packet fakePlayer(String displayName) {
	    UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + displayName).getBytes(Charsets.UTF_8));
	    EntityPlayer pl = new EntityPlayer(
	            MinecraftServer.getServer(),
	            MinecraftServer.getServer().getWorldServer(0),
	            (GameProfile) (new WrappedGameProfile(uuid, displayName)).getHandle(),
	            new PlayerInteractManager(MinecraftServer.getServer().getWorldServer(0))
	    );
	    
	    //pl.entit
	    PacketPlayOutNamedEntitySpawn pn
	    	= new PacketPlayOutNamedEntitySpawn(pl);
	    
	    return pn;
	}
	*/
	
	
	/**
	 * Still in the works
	 * Allows us to spawn an NPC copy of the player at their location
	 * @param p
	 */
	public void spawn(Player p) {
		String displayName = p.getDisplayName();
		
		
		UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + displayName).getBytes(Charsets.UTF_8));
	    EntityPlayer pl = new EntityPlayer(
	            MinecraftServer.getServer(),
	            MinecraftServer.getServer().getWorldServer(0),
	            (GameProfile) (new WrappedGameProfile(p.getUniqueId(), displayName)).getHandle(),
	            new PlayerInteractManager(MinecraftServer.getServer().getWorldServer(0))
	    );
	    
	    
	    PacketPlayOutPlayerInfo pi
	            = new PacketPlayOutPlayerInfo(
	                    PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, pl);
	    
		((CraftPlayer)p).getHandle().playerConnection.sendPacket(pi);
		
		
		
		pl.setLocation(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), p.getEyeLocation().getPitch(), p.getEyeLocation().getYaw());

		
		 PacketPlayOutNamedEntitySpawn pn
	    	= new PacketPlayOutNamedEntitySpawn(pl);
		//pn.a(arg0);
		 
		((CraftPlayer)p).getHandle().playerConnection.sendPacket(pn);
		
		
		
		//*******************************
		// How to Ride
		//*******************************
		/**
		
		Entity boat = p.getWorld().spawnEntity(p.getLocation(), EntityType.BOAT);
		boat.setInvulnerable(true);
		net.minecraft.server.v1_9_R2.Entity netBoat = ((org.bukkit.craftbukkit.v1_9_R2.entity.CraftBoat)boat).getHandle();
		//net.minecraft.server.v1_9_R2.Entity boat = (net.minecraft.server.v1_9_R2.Entity) 
		pl.startRiding(netBoat);
		DataWatcher dw = new DataWatcher(pl);
        PacketPlayOutEntityMetadata pe = new PacketPlayOutEntityMetadata(pl.getId(), dw, false);
		
		PacketPlayOutMount pm = new PacketPlayOutMount(pl);
		((CraftPlayer)p).getHandle().playerConnection.sendPacket(pm);
		
		*/
		
		//*******************************
		
		
		
		Entity pig = p.getWorld().spawnEntity(p.getLocation().add(0,-0.9,0), EntityType.PIG);
		pig.setInvulnerable(true);
		((Pig)pig).setAI(false);
		
		//((Pig)pig).setSaddle(true);
	
		
		
		net.minecraft.server.v1_10_R1.Entity netBoat = ((org.bukkit.craftbukkit.v1_10_R1.entity.CraftPig)pig).getHandle();
		pl.startRiding(netBoat);
		
		//Make a silent invisible pig to ride
		NBTTagCompound tag = new NBTTagCompound();
		netBoat.e(tag); // Apply the current values
		tag.setInt("Silent", 1); // Apply silent value
		tag.setInt("NoGravity", 1); // Apply silent value
		//tag.setInt("Invisible", 1); // Apply silent value
		netBoat.f(tag); // Set the tag
		
		DataWatcher dw = new DataWatcher(pl);
        PacketPlayOutEntityMetadata pe = new PacketPlayOutEntityMetadata(pl.getId(), dw, false);
		
		PacketPlayOutMount pm = new PacketPlayOutMount(pl);
		((CraftPlayer)p).getHandle().playerConnection.sendPacket(pm);
		
		
		PotionEffect poe = new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 0,false,false);
		p.setGlowing(true);
		p.setCollidable(false);
		p.setCanPickupItems(false);
		p.setGliding(true);
		p.setInvulnerable(true);
		p.addPotionEffect(poe);
		
		Team ghost;
		((LivingEntity)pig).addPotionEffect(poe);
		
		pig.setVelocity(pig.getLocation().getDirection().normalize().multiply(2));
	}
	
	
	public void bossBar(Player player) {
		
		try {
			UUID bossid = UUID.nameUUIDFromBytes(("Bar for:" + player.getDisplayName()).getBytes(Charsets.UTF_8));
			PacketPlayOutBoss packet = new PacketPlayOutBoss();
			Field uuidField = packet.getClass().getDeclaredField("a");
            uuidField.setAccessible(true);
            uuidField.set(packet, bossid);

            Field actionField = packet.getClass().getDeclaredField("b");
            actionField.setAccessible(true);
            //actionField.set(packet, PacketPlayOutBoss.Action.valueOf(action.name()));
            actionField.set(packet, Action.ADD );

            Field messageField = packet.getClass().getDeclaredField("c");
            messageField.setAccessible(true);
            //messageField.set(packet, IChatBaseComponent.ChatSerializer.a(message));
            messageField.set(packet, IChatBaseComponent.ChatSerializer.a("{\"text\": \"Hello world\"}"));

            Field progressField = packet.getClass().getDeclaredField("d");
            progressField.setAccessible(true);
            progressField.set(packet, 0.5f);

            Field colorField = packet.getClass().getDeclaredField("e");
            colorField.setAccessible(true);
            colorField.set(packet, BossBattle.BarColor.PURPLE);

            Field typeField = packet.getClass().getDeclaredField("f");
            typeField.setAccessible(true);
            typeField.set(packet, BossBattle.BarStyle.NOTCHED_10);

            Field darkenSky = packet.getClass().getDeclaredField("g");
            darkenSky.setAccessible(true);
            darkenSky.set(packet, false);

            Field music = packet.getClass().getDeclaredField("h");
            music.setAccessible(true);
            music.set(packet, false);

            Field createFog = packet.getClass().getDeclaredField("i");
            createFog.setAccessible(true);
            createFog.set(packet, false);
            
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
		
	}
	
	
	/**
	 * onCommand(sender/source, command, _, arguments)
	 * * Holds the command-line commands
	 */
	@SuppressWarnings("unused")
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args){
		
		Player player = (Player) sender;
		String cmd = command.getName().toLowerCase();
		//String name = player.getName().toLowerCase();
		UUID uuid = player.getUniqueId();
		
		if(cmd.equals("heal")){	// Heal player
			player.setHealth(20.0);
			player.setFoodLevel(20);
			player.setSaturation(100);
			List<Player> players = player.getWorld().getPlayers();
			for (Player p : players)
				p.sendMessage(player.getDisplayName() + " used heal.");
			
			
			

			/*
			 
			try {
				Block b = player.getLocation().getBlock();
				PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange();
			
				int x = player.getLocation().getBlockX();
				int y = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getY();
				int z = player.getLocation().getBlockZ();
				int locInt = ((x & 0x3FFFFFF) << 38) | ((y & 0xFFF) << 26) | (z & 0x3FFFFFF);
				
				Field positionField = packet.getClass().getDeclaredField("a");
				positionField.setAccessible(true);
				positionField.set(packet, ((x & 0x3FFFFFF) << 38) | ((y & 0xFFF) << 26) | (z & 0x3FFFFFF));
				
				int type = 20;
				int meta = 0;
				int blockId = type << 4 | (meta & 15);
				
				Field typeField = packet.getClass().getDeclaredField("b");
				typeField.setAccessible(true);
				typeField.set(packet, blockId);
			} catch (Exception e) {
				e.printStackTrace();
			}

			*/
				
			//player.sendBlockChange(player.getLocation().add(0,-1,0), Material.GLASS, (byte)0);
			
			/*
			try {
				Block b = player.getLocation().getBlock();
				PacketPlayOutMultiBlockChange packet = new PacketPlayOutMultiBlockChange();
				
				Field chunkXField = packet.getClass().getDeclaredField("a");
				chunkXField.setAccessible(true);
				chunkXField.set(packet, player.getLocation().getChunk().getX());
				
				Field chunkZField = packet.getClass().getDeclaredField("b");
				chunkZField.setAccessible(true);
				chunkZField.set(packet, player.getLocation().getChunk().getZ());
				
				Field countField = packet.getClass().getDeclaredField("c");
				countField.setAccessible(true);
				countField.set(packet, 1);
				
				
				//player.getLocation().getBlock().get
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			*/
			
			
			
			
	    }
		
		
		if (cmd.equals("done"))
		{
			//How to ride a polar bear
			PolarBear bear = (PolarBear)player.getWorld().spawnEntity(player.getLocation(), EntityType.POLAR_BEAR);
			bear.setPassenger(player);
		}
		
	
		/*
		if(cmd.equals("regenerate")){	// Regenerate 3x3 chunk centered on player
			int tx = player.getLocation().getChunk().getX();
			int tz = player.getLocation().getChunk().getZ();
			player.getWorld().regenerateChunk(tx+1, tz+1);
			player.getWorld().regenerateChunk(tx+1, tz);
			player.getWorld().regenerateChunk(tx+1, tz-1);
			player.getWorld().regenerateChunk(tx, tz+1);
			player.getWorld().regenerateChunk(tx, tz);
			player.getWorld().regenerateChunk(tx, tz-1);
			player.getWorld().regenerateChunk(tx-1, tz+1);
			player.getWorld().regenerateChunk(tx-1, tz);
			player.getWorld().regenerateChunk(tx-1, tz-1);
		}	
		
		
		if(cmd.equals("bendinggui")){	
			if (BendingPlayer.isBender(player))
				BendingPlayer.getBendingPlayer(player.getUniqueId()).showBendingGUI();
		}
		*/
		
		if(cmd.equals("bend") || cmd.equals("bending")){	// Bending: main command
			if (args.length >= 1)
			{
				if (args[0].toLowerCase().equals("choose"))	// choose bending element
				{
					if (args.length > 1) // UNIVERSAL PLAYER DB CREATION
					{
						String inputType = args[1];
						if (inputType.equals("water") || inputType.equals("fire") ||
							inputType.equals("air") || inputType.equals("earth"))
							new BendingPlayer(player.getUniqueId(), inputType);
						else
							player.sendMessage(ChatColor.RED + "Choose your element\nUsage: /bend choose <air|earth|water|fire>");
					}
					else
						player.sendMessage(ChatColor.RED + "Choose your element\nUsage: /bend choose <air|earth|water|fire>");
				}
				else if (args[0].toLowerCase().equals("help"))
				{
					if (args.length == 1)
					{
						String help = ChatColor.RED + "";
						help = help + "Bending Help\n";						
						player.sendMessage(help);
					}
					else if (args.length >= 2 )
					{
						if (args[1].toLowerCase().equals("fire"))
							player.sendMessage(FireBendingForm.listMoves());
						if (args[1].toLowerCase().equals("earth"))
							player.sendMessage(EarthBendingForm.listMoves());
						if (args[1].toLowerCase().equals("water"))
							player.sendMessage(WaterBendingForm.listMoves());
						if (args[1].toLowerCase().equals("air"))
							player.sendMessage(AirBendingForm.listMoves());
					}
					else
					{
						player.sendMessage(ChatColor.RED + "Invalid. type \"/bend help\" for usage.");
					}
				}
				/*else if (BendingFireString.contains(args[0].toLowerCase()))
				{
					player.sendMessage(BendingFireString.help(args[0].toLowerCase()));
				}*/
				else
				{
					player.sendMessage(ChatColor.RED + "Invalid. type \"/bend help\" for usage.");
				}
			}
			else
			{
				if (BendingPlayer.isBender(player))
					BendingPlayer.getBendingPlayer(player.getUniqueId()).showBendingGUI();
			}
		}
		
		//Need to readd old setBind command
		/*
		if(cmd.equals("bb")){	// Bending - bind: bind a move to a slot
			if(!Tools.setBind(player, args[0]))
				player.sendMessage(ChatColor.RED + "invalid move to bind.");
	    }
	    */
		
		if(false && cmd.equals("bd")){	// Bending - display: shows currently bound moves
			String moveName;
			if (args.length == 0)
			{
				for (int i = 1; i <= 9; i++)
				{
					moveName = (String) Bending.playersConfig.get(uuid + "." + Tools.getBendingType(player) + ".slot" + Integer.toString(i));
					if (moveName != null)
						player.sendMessage(ChatColor.DARK_PURPLE + Integer.toString(i) + " : " + moveName);
				}
			}
		}
	    return true;
	}
	
	//effect manager getter
	public static EffectManager getEffectManager() {
		return effectManager;
	}

	/**
	 * firstRun()
	 * Create the files if they don't exist
	 */
	private void firstRun() throws Exception {
	    if(!playersFile.exists()){
	    	playersFile.getParentFile().mkdirs();
	    	copyYamlsToFile(getResource("playersConfig.yml"), playersFile);
	    }
	    if(!usersFile.exists()){
	        usersFile.getParentFile().mkdirs();
	        copyYamlsToFile(getResource("users.yml"), usersFile);
	    }
	}
	// Copy to file
	private void copyYamlsToFile(InputStream in, File file) {
	    try {
	        OutputStream out = new FileOutputStream(file);
	        byte[] buf = new byte[1024];
	        int len;
	        while((len=in.read(buf))>0){
	            out.write(buf,0,len);
	        }
	        out.close();
	        in.close();
	    } catch (Exception e) {e.printStackTrace();}
	}
	// Save YAMLs to file
	public void saveYamls() {
	    try {
	    	playersConfig.save(playersFile);
	        usersConfig.save(usersFile);
	    } catch (IOException e) {e.printStackTrace();}
	}
	// Load YAMLs from file
	public void loadYamls() {
	    try {
	    	playersConfig.load(playersFile);
	        usersConfig.load(usersFile);
	    } catch (Exception e) {e.printStackTrace();}
	}
}