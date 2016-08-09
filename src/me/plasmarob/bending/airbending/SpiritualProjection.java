package me.plasmarob.bending.airbending;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;

import me.plasmarob.bending.AbstractBendingForm;
import me.plasmarob.bending.util.Tools;
import net.minecraft.server.v1_10_R1.DataWatcher;
import net.minecraft.server.v1_10_R1.EntityPlayer;
import net.minecraft.server.v1_10_R1.MinecraftServer;
import net.minecraft.server.v1_10_R1.NBTTagCompound;
import net.minecraft.server.v1_10_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_10_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_10_R1.PacketPlayOutMount;
import net.minecraft.server.v1_10_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_10_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_10_R1.PlayerInteractManager;

public class SpiritualProjection extends AbstractBendingForm {

	private EntityPlayer entityPlayer;
	private Entity pig;
	
	@SuppressWarnings("deprecation")
	public SpiritualProjection(Player player) {
		
		if (Tools.lastKey(player) != 2)
			return;
		
		if (instances.containsKey(player)) {
			((SpiritualProjection)instances.get(player)).join();
			instances.remove(player);
		}
		else {
			if (!instances.containsKey(player) && player.isOnGround() && player.isSneaking() ) {
				this.player = player;
				instances.put(player, this);
				separate(player);
			}
		}	
	}
	
	@Override
	public boolean progress() {
		if (player.getAllowFlight())
			player.setFlying(true);
		return false;
	}


	public void join () { 
		player.teleport(new Location(entityPlayer.getWorld().getWorld(),entityPlayer.locX,entityPlayer.locY+1,entityPlayer.locZ));
		// remove out-of-body mode
		player.setGlowing(false);
		player.setCollidable(true);
		player.setCanPickupItems(true);
		player.setGliding(false);
		player.setInvulnerable(false);
		player.setFlying(false);
		player.setAllowFlight(false);
		player.removePotionEffect(PotionEffectType.INVISIBILITY);
		
		// Remove the player
		PacketPlayOutEntityDestroy bye = new PacketPlayOutEntityDestroy(entityPlayer.getId());
		List<Player> players = player.getWorld().getPlayers();
		for (Player p : players) {
			((CraftPlayer)p).getHandle().playerConnection.sendPacket(bye);
			//p.showPlayer(player);
		}
		
		pig.setInvulnerable(false);
		pig.remove();
	}
	
	/**
	 * Still in the works
	 * Allows us to spawn an NPC copy of the player at their location
	 * @param player
	 */
	@SuppressWarnings("deprecation")
	private void separate(Player player) {
		
		// Create an entityPlayer to spawn
		String displayName = player.getDisplayName();
		UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + displayName).getBytes(Charsets.UTF_8));
	    entityPlayer = new EntityPlayer(
	            MinecraftServer.getServer(),
	            MinecraftServer.getServer().getWorldServer(0),
	            (GameProfile) (new WrappedGameProfile(player.getUniqueId(), displayName)).getHandle(),
	            new PlayerInteractManager(MinecraftServer.getServer().getWorldServer(0))
	    );
	    
	    // create and send tab-menu Player Info first (required for spawning fake peeps)
	    PacketPlayOutPlayerInfo playerInfoPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer);
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(playerInfoPacket);

		entityPlayer.setLocation(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getEyeLocation().getYaw(), player.getEyeLocation().getPitch());
		// Send the spawn packet
		PacketPlayOutNamedEntitySpawn pn = new PacketPlayOutNamedEntitySpawn(entityPlayer);
		List<Player> players = player.getWorld().getPlayers();
		for (Player p : players) {
			((CraftPlayer)p).getHandle().playerConnection.sendPacket(pn);
			//p.hidePlayer(player);
		}
		
		// Spawn the invisible pig entity to ride (no saddle ... hax)
		pig = player.getWorld().spawnEntity(player.getLocation().add(0,-0.9,0), EntityType.PIG);
		pig.setInvulnerable(true);
		((Pig)pig).setAI(false);
		//((Pig)pig).setSaddle(true);
	
		// Set up the packet entity - to get what we need for riding
		net.minecraft.server.v1_10_R1.Entity netPig = ((org.bukkit.craftbukkit.v1_10_R1.entity.CraftPig)pig).getHandle();
		entityPlayer.startRiding(netPig);
		
		//Make a silent invisible pig to ride
		NBTTagCompound tag = new NBTTagCompound();
		netPig.e(tag); // Apply the current values
		tag.setInt("Silent", 1); // Apply silent value
		tag.setInt("NoGravity", 1); // Apply silent value
		//tag.setInt("Invisible", 1); // Apply silent value
		netPig.f(tag); // Set the tag
		
		// Get the DataWatcher for changing metadata
		//DataWatcher dw = new DataWatcher(entityPlayer);
        //PacketPlayOutEntityMetadata pe = new PacketPlayOutEntityMetadata(entityPlayer.getId(), dw, false);
		
		PacketPlayOutMount pm = new PacketPlayOutMount(entityPlayer);
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(pm);
		
		// Set player into out-of-body mode
		player.setGlowing(true);
		player.setCollidable(false);
		player.setCanPickupItems(false);
		player.setGliding(true);
		player.setInvulnerable(true);
		player.setAllowFlight(true);
		player.setFlying(true);
		// Invisibility for pig and actual person
		PotionEffect poe = new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 0,false,false);
		player.addPotionEffect(poe);
		((LivingEntity)pig).addPotionEffect(poe);
		
	}
	
	
	
	public static void removeAll() {
		if (instances.size() > 0)
		{
			List<Player> list = Collections.list(instances.keys());
			for (Player p : list) {
				((SpiritualProjection)instances.get(p)).join();
				instances.remove(p);
			}
		}
	}
	
	
	
}