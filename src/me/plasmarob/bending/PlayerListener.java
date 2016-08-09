package me.plasmarob.bending;

import java.util.UUID;

import me.plasmarob.bending.airbending.AirScooter;
import me.plasmarob.bending.airbending.AirShield;
import me.plasmarob.bending.util.Tools;
import me.plasmarob.bending.waterbending.*;
import net.minecraft.server.v1_10_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_10_R1.PacketPlayOutChat;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.PolarBear;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.spigotmc.event.entity.EntityDismountEvent;

//import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
//import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
//import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

/**
 * Player Listener class
 * * created and registered by Bending
 * 
 * @author      Robert Thorne <plasmarob@gmail.com>
 * @version     0.3                
 * @since       2014-10-05
 */
public class PlayerListener implements Listener {
	public Bending plugin;
	
	public PlayerListener(Bending plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		String name = player.getName().toString();
		UUID uuid = player.getUniqueId();
		
		try
		{
			String str = Bending.playersConfig.getString(player.getUniqueId() + ".bendingType");
			
			// If player is not a bender, terminates HERE
			if (str != null)
			{
				//Tools.say("-->" + str + "<--");
				Bending.playersConfig.set(uuid + ".name", name);
				new BendingPlayer(uuid);
			}
		}
		catch (Exception e)
		{
			// Catch no value - player is not a bender (yet)
		}
		
		//new BendingPlayer(uuid);
		
		//String name = player.getName().toString();
		
		//plugin.playersConfig.set(uuid + ".name", name);
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		plugin.saveYamls();
		UUID uuid = event.getPlayer().getUniqueId();
		BendingPlayer.delete(uuid);
		BendingManager.removePlayer(event.getPlayer());
		// REMOVE ALL FORM ASSOCIATIONS HERE
	}
	
	@EventHandler
	public void onPlayerTargeted(EntityTargetEvent event)
	{
		if (event.getTarget() instanceof Player) {
			Player player = (Player)event.getTarget();
			//Tools.say(player, event.getReason().toString());
			//Tools.say(player, BendingPlayer.isBender(player));
			//Tools.say(player, BendingPlayer.getBendingType(player));
			if ( (event.getReason().equals(TargetReason.CLOSEST_PLAYER)  ||
					event.getReason() == TargetReason.COLLISION) &&
					BendingPlayer.isBender(player) &&
					BendingPlayer.getBendingType(player).equals("air")) {
				//event.getEntity().setsetTarget(null);
				event.setCancelled(true);
			}
			
			if ( (event.getEntity().getType() == EntityType.POLAR_BEAR) &&
					BendingPlayer.isBender(player) &&
					BendingPlayer.getBendingType(player).equals("water")) {
				//event.getEntity().setsetTarget(null);
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerDismount(EntityDismountEvent event)
	{
		if (event.getDismounted() instanceof Player) {
			Player player = (Player)event.getDismounted();
			if (AirScooter.getPlayers().contains(player))
			{
				((LivingEntity)event.getEntity()).setPassenger(player);
			}
		}
		
	}
	
	@EventHandler
	public void onPlayerItemChange(PlayerItemHeldEvent event)
	{
		//Tools.say(event.getPlayer(),event.getEventName());
		//event.getPlayer().getItemInHand().se
		Player player = event.getPlayer();
		
		if (event.getPreviousSlot() != event.getNewSlot())
		{
			if (BendingPlayer.isBender(player))
			{
				//BendingPlayer bp = BendingPlayer.getBendingPlayer(player);
				String bendType = BendingPlayer.getBendingType(player);
				int slot = event.getNewSlot();
				String form = Bending.playersConfig.getString(
						player.getUniqueId() + "." + Tools.getBendingType(player) + ".slot" + (slot+1));
				if (form == null)
					form = "";	
				form = " " + ChatColor.BOLD + " " + form;
				if (bendType.equals("fire"))
					form = ChatColor.DARK_RED + form;
				else if (bendType.equals("water"))
					form = ChatColor.BLUE + form;
				else if (bendType.equals("earth"))
					form = ChatColor.DARK_GREEN + form;
				else if (bendType.equals("air"));
					form = ChatColor.GRAY + form;
				//form = ChatColor.DARK_GREEN + " " + ChatColor.BOLD + " " + form;
					
				PacketPlayOutChat packet = new PacketPlayOutChat(
					ChatSerializer.a("{ \"text\": \"" + form + "\" }"), (byte) 2);
				((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
				
			}
		}
	}
	
	
	@EventHandler
	public void onPlayerSneak(PlayerToggleSneakEvent event)
	{
		UUID uuid = event.getPlayer().getUniqueId();
		if (BendingPlayer.isBender(uuid))
		{
			if (event.getPlayer().isSneaking())
			{
				BendingPlayer.getBendingPlayer(uuid).startTimer();
				//add to the key list
				BendingPlayer.getBendingPlayer(uuid).addKey(uuid,PlayerAction.SNEAK_OFF.val());
			}
			else
			{
				BendingPlayer.getBendingPlayer(uuid).startTimer();
				//add to the key list
				BendingPlayer.getBendingPlayer(uuid).addKey(uuid,PlayerAction.SNEAK_ON.val());
			}
			if (BendingPlayer.getBendingType(event.getPlayer()).equals("fire"))
			{
				event.getPlayer().setFireTicks(0);
			}
		}
	}
	
	@EventHandler
	public void onPlayerClick(PlayerInteractEvent event)
	{
		//Tools.say(event.getPlayer(),event.getAction().toString());
		if (event.getAction() == Action.LEFT_CLICK_AIR || 
				event.getAction() == Action.LEFT_CLICK_BLOCK) {
			UUID uuid = event.getPlayer().getUniqueId();
			if (BendingPlayer.isBender(uuid)) {
				BendingPlayer.getBendingPlayer(uuid).startTimer();
				//add 2 to the key list
				BendingPlayer.getBendingPlayer(uuid).addKey(uuid,PlayerAction.LEFT_CLICK.val());
			}
		}
		
		if (event.getAction() == Action.RIGHT_CLICK_AIR || 
				event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			UUID uuid = event.getPlayer().getUniqueId();
			if (BendingPlayer.isBender(uuid)) {
				BendingPlayer.getBendingPlayer(uuid).startTimer();
				//add 3 to the key list
				BendingPlayer.getBendingPlayer(uuid).addKey(uuid,PlayerAction.RIGHT_CLICK.val());
			}
		}
		
	}
	
	//make Polar Bears ride-able
	@EventHandler
	public void onPlayerClick(PlayerInteractEntityEvent event)
	{
		if (event.getRightClicked() instanceof PolarBear) {
			event.getRightClicked().setPassenger(event.getPlayer());
		}
	}
	
	
	// EntityShootBowEvent and ProjectileHitEvent are for firebenders' bows
	@EventHandler
	public void arrowShoot(EntityShootBowEvent event)
	{
		if (event.getEntity() instanceof Player && event.getProjectile() instanceof Arrow)
		{
			Player p = (Player)event.getEntity();
			if (BendingPlayer.isBender(p) && 
					BendingPlayer.getBendingType(p).equals("fire") && p.isSneaking())
			{
				event.getProjectile().setFireTicks(300);
			}
		}
	}
	@EventHandler
	public void arrowHit(ProjectileHitEvent event)
	{
		if (event.getEntity().getShooter() instanceof Player && event.getEntity() instanceof Arrow)
		{
			Player p = (Player)event.getEntity().getShooter();
			if (BendingPlayer.isBender(p) && 
					BendingPlayer.getBendingType(p).equals("fire") && 
					event.getEntity().getFireTicks() > 0)
			{
				Block b = event.getEntity().getLocation().getBlock();
				if (b.getType() == Material.AIR)
					b.setType(Material.FIRE);
			}	
		}
	}
	
	/*
	@EventHandler
	public void burnEvent(EntityCombustByBlockEvent event)
	{
		if (event.getEntity() instanceof Player)
		{
			Player p = (Player)event.getEntity();
			if (BendingPlayer.isBender(p) && 
					BendingPlayer.getBendingType(p).equals("fire"))
			{
				event.setCancelled(true);
			}	
		}
	}
	*/
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
        	Player p = (Player)event.getEntity();
        	
        	if (BendingPlayer.getBendingType(p).equalsIgnoreCase("fire"))
        		event.setCancelled(event.getCause() == DamageCause.FIRE_TICK || event.getCause() == DamageCause.FIRE);
       
        
        	if (BendingPlayer.getBendingType(p).equalsIgnoreCase("earth") && 
        		    (event.getCause() == DamageCause.FALL || 
        		     event.getCause() == DamageCause.SUFFOCATION ))
	    		event.setCancelled(true);
        
        	if(BendingPlayer.getBendingType(p).equalsIgnoreCase("air") &&
	    			(event.getCause() == DamageCause.FALL || 
	    				(AirShield.instances.containsKey(p) && 
	    					(event.getCause() == DamageCause.PROJECTILE || 
	    					event.getCause() == DamageCause.BLOCK_EXPLOSION ||
	    					event.getCause() == DamageCause.ENTITY_EXPLOSION
	    					)))
        		)
			{
	    		//p.setAllowFlight(true);
				event.setDamage(0);
				event.setCancelled(true);
			}
        
        
        
        }
        
        
        if (event.getEntity() instanceof LivingEntity)
        {
        	LivingEntity le = (LivingEntity)event.getEntity();
        	if (event.getCause() == DamageCause.SUFFOCATION && 
        			le.getEyeLocation().getBlock().getType() == Material.ICE)
        	{
        		event.setCancelled(true);
        	}
        }
    }
	
	
	@EventHandler
	public void airFly(PlayerToggleSprintEvent event)
	{
		BendingPlayer.getBendingPlayer(event.getPlayer()).updateAirFlying();
	}
	
	
	//TODO: move this logic into the BendingFormString Enum to be caseless
	@EventHandler
	public void inventoryClick(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();

		Inventory inventory = event.getClickedInventory();
		if (inventory == null)
			return;
		if (inventory.getTitle().equals("Bending"))
		{
			event.setCancelled(true); //NO TOUCHING the gui
			if (event.isLeftClick())
			{
				//Tools.say(p.getDisplayName());
				@SuppressWarnings("unused")
				
				boolean found = true;
				switch (event.getRawSlot())
				{
					case 27:
						BendingPlayer.getBendingPlayer(p).toggleSwimming();
						break;
						
					case 35:
						BendingPlayer.getBendingPlayer(p).toggleIce();
						break;
						
					case 36:
						BendingPlayer.getBendingPlayer(p).toggleWaterRun();
						break;
				
					case 44:
						BendingPlayer.getBendingPlayer(p).toggleBending();
						break;
				}
				
				if (BendingPlayer.getBendingType(p).equals("fire") && FireBendingForm.getBySlot(event.getRawSlot()) != null) {
				Tools.setBind(p, FireBendingForm.getBySlot(event.getRawSlot()));
				} else if (BendingPlayer.getBendingType(p).equals("water") && WaterBendingForm.getBySlot(event.getRawSlot()) != null) {
					Tools.setBind(p, WaterBendingForm.getBySlot(event.getRawSlot()));
				} else if (BendingPlayer.getBendingType(p).equals("air") && AirBendingForm.getBySlot(event.getRawSlot()) != null) {
					Tools.setBind(p, AirBendingForm.getBySlot(event.getRawSlot()));
				} else if (BendingPlayer.getBendingType(p).equals("earth") && EarthBendingForm.getBySlot(event.getRawSlot()) != null) {
					Tools.setBind(p, EarthBendingForm.getBySlot(event.getRawSlot()));
				}
				
				
			
				
				/*
				for (int i = 0; i < 54; i++)
				{
					inventory.setItem(i, new ItemStack(Material.TNT));
				}
				ItemStack is = inventory.getItem(event.getRawSlot());
				
				if (is != null)
				{
					ItemMeta meta = is.getItemMeta();
					meta.setDisplayName(ChatColor.AQUA + "Hello!");
					ArrayList<String> metaStrs = new ArrayList<String>();
					metaStrs.add(ChatColor.DARK_GREEN + "Hello Again!");
					metaStrs.add(ChatColor.DARK_RED + "Triple H");
					meta.setLore(metaStrs);
					is.setItemMeta(meta);
				}
				*/
			}
			
		}
		
		if (p.getInventory() == inventory && BendingPlayer.isBender(p)
				&& BendingPlayer.getBendingPlayer(p).getBendingGUI().getViewers().size() > 0)
		{
			event.setCancelled(true);  //NO TOUCHING the gui
		}
    }
	
	/*
	@EventHandler
	public void inventoryMoveItem(InventoryMoveItemEvent event) {
		
		Inventory inventory = event.getDestination();
		
		if (inventory.getTitle().equals("Bending"))
		{
			event.setCancelled(true);
		}
		
    }
	*/
	
	@EventHandler
	public void paralyze(PlayerMoveEvent event)
	{
		if (BendingManager.paralyzeIce.contains(event.getPlayer()))
		{
			event.setCancelled(true);
		}
	}
	
	// Fixes waterbending moves so they don't bleed everywhere
	@EventHandler(ignoreCancelled = true)
	public void noWaterSpread(BlockFromToEvent event)
	{
		
		if (WaterTwister.blockHeld(event.getBlock()))
			event.setCancelled(true);
		if (TwisterBurst.blockHeld(event.getBlock()))
			event.setCancelled(true);
		/*
		if (WaterStreaming.blockHeld(event.getBlock()))
			event.setCancelled(true);
		*/
		if (LaunchedWater.blockHeld(event.getBlock()))
			event.setCancelled(true);
		
		if (WaterWhip.blockHeld(event.getBlock()))
			event.setCancelled(true);
		
		if (WaterTsunami.blockHeld(event.getBlock()))
			event.setCancelled(true);
		
		if (IceShield.blockHeld(event.getBlock()))
			event.setCancelled(true);
		
		if (WaterStreaming.blockHeld(event.getBlock()))
			event.setCancelled(true);
		
		if (WaterBlade.blockHeld(event.getBlock()))
			event.setCancelled(true);
		
		if (WaterExtinguish.blockHeld(event.getBlock()))
			event.setCancelled(true);
		
		if (WaterBubble.blockHeld(event.getToBlock()))
			event.setCancelled(true);
		
		//event.setCancelled(true);
	}
	
	
}
