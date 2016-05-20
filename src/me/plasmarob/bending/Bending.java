package me.plasmarob.bending;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

//import me.plasmarob.bending.firebending.FireEmitEffect;



import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.slikey.effectlib.EffectLib;
import de.slikey.effectlib.EffectManager;

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
	public static Tools tools;
	// Player Listener 
	public final PlayerListener listener = new PlayerListener(this);
	// Effects Library
    private static EffectManager effectManager; 
	
	// YAML file user config objects
	File usersFile;
	FileConfiguration usersConfig;
	File playersFile;
	public static FileConfiguration playersConfig;
	
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
				
		tools = new Tools(plugin);
		BendingPlayer.tools = tools;
		
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
							player.sendMessage(BendingFireString.listFireMoves());
						if (args[1].toLowerCase().equals("earth"))
							player.sendMessage(BendingEarthString.listEarthMoves());
						if (args[1].toLowerCase().equals("water"))
							player.sendMessage(BendingWaterString.listWaterMoves());
						if (args[1].toLowerCase().equals("air"))
							player.sendMessage(BendingAirString.listAirMoves());
					}
					else
					{
						player.sendMessage(ChatColor.RED + "Invalid. type \"/bend help\" for usage.");
					}
				}
				else if (BendingFireString.contains(args[0].toLowerCase()))
				{
					player.sendMessage(BendingFireString.help(args[0].toLowerCase()));
				}
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
		
		if(cmd.equals("bb")){	// Bending - bind: bind a move to a slot
			if(!Tools.setBind(player, args))
				player.sendMessage(ChatColor.RED + "invalid move to bind.");
	    }
		
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