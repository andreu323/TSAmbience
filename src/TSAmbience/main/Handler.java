package TSAmbience.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.minecraft.server.v1_16_R3.PacketPlayOutCustomSoundEffect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
@SuppressWarnings({ "deprecation", "unused" })
public class Handler implements Listener{
	int blocksbroked = 0;
	Location a;
	double d;
	private TSAmbience plugin;
	public Handler(TSAmbience plugin) {
		this.plugin = plugin;
	}
	//adding roomsounds events to new player
	@EventHandler
	public void join(PlayerJoinEvent e) {
		plugin.soundcheck(e.getPlayer());
		
	}
	@EventHandler
	public void quit(PlayerQuitEvent e) {
		Logger log = Logger.getLogger("room_sounds");
		Player p = e.getPlayer();
		plugin.checker_num.remove(p.getName());
		plugin.checker_sound.remove(p.getName());
		plugin.checker_playng.remove(p.getName());
		plugin.id_day.remove(p.getName());
		plugin.id_night.remove(p.getName());
		Bukkit.getScheduler().cancelTask(plugin.play.get(p.getName()));
		plugin.play.remove(p.getName());
		
	}

	public boolean day() {
	    Server server = Bukkit.getServer();
	    long time = server.getWorld("world").getTime();

	    return time < 12300 || time > 23850;
	}
	
}
