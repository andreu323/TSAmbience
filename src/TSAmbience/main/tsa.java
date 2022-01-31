package TSAmbience.main;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;

public class tsa implements CommandExecutor {
	private TSAmbience plugin;

	public tsa(TSAmbience plugin) {
		this.plugin = plugin;
	}
	@SuppressWarnings("unlikely-arg-type")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		Player p = (Player) sender;
		if (args[0].equals("mute")) {
			if(args.length == 2) {
				if ( args[1].equals("on") ) {
					plugin.getConfig().set("playermutestatus." + ((OfflinePlayer) sender).getUniqueId().toString(), true);
					plugin.playermute.put(((OfflinePlayer) sender).getUniqueId().toString(), true);
					plugin.saveConfig();
					sender.sendMessage(ChatColor.GREEN +"mute " + args[1]);
				}
				else if ( args[1].equals("off") ) {
					plugin.getConfig().set("playermutestatus." + ((OfflinePlayer) sender).getUniqueId().toString(), false);
					plugin.playermute.put(((OfflinePlayer) sender).getUniqueId().toString(), false);
					plugin.saveConfig();
					sender.sendMessage(ChatColor.GREEN +"mute " + args[1]);
				}
			}
			else {
				sender.sendMessage(ChatColor.RED +"mute off/on");
			}
			return true;
		}
		if(!isPlayerInGroup(p,"builders") && !isPlayerInGroup(p,"devs") && !isPlayerInGroup(p,"moderators") && !p.isOp()) {
			p.sendMessage(ChatColor.RED + "you dont have permissions!");
			return true;
		}
		if( args[0].equals("merge")){
		
			LocalSession session = getWorldEdit().getSession(p);
			Region selection;
			try {
			    selection = session.getSelection(session.getSelectionWorld());
			} catch (IncompleteRegionException e) {
			    p.sendMessage(ChatColor.RED + "Region not selected");
			    return true;
			}
			if (!(selection instanceof CuboidRegion)) {
			    p.sendMessage(ChatColor.RED + "region type: " + selection.getClass().getSimpleName() + " not supported");
			    return true;
			}
			
			
			CuboidRegion cuboid = (CuboidRegion) selection;
			BlockVector3 min = cuboid.getMinimumPoint();
			BlockVector3 max = cuboid.getMaximumPoint();
			
			plugin.log.info(plugin.loc1.toString());
			Location l = new Location(plugin.world,max.getBlockX() +1 ,max.getBlockY(),max.getBlockZ() +1);
			plugin.loc1.get(args[1]).add(new Location(plugin.world,min.getBlockX() ,min.getBlockY(),min.getBlockZ()));
			plugin.loc2.get(args[1]).add(l);
			plugin.world_sound.get(args[1]).add(selection.getWorld().getName());
			final int i = plugin.loc2.get(args[1]).indexOf(l);
			plugin.getConfig().set( args[1] +"."+ i+".world"  , selection.getWorld().getName());
			plugin.getConfig().set(args[1] + "."+i+".x1"  , min.getBlockX());
			plugin.getConfig().set( args[1] + "."+i+".x2"  , max.getBlockX()+1);
			plugin.getConfig().set(  args[1] +"."+i+ ".z1"  , min.getBlockZ());
			plugin.getConfig().set( args[1] + "."+i+".z2"  , max.getBlockZ()+1);
			plugin.getConfig().set( args[1] + "."+i+".y1"  , min.getBlockY());
			plugin.getConfig().set( args[1] + "."+i+".y2"  , max.getBlockY());
			plugin.saveConfig();
			sender.sendMessage(ChatColor.GREEN +args[1]+"  successfully migrated - id: " +i);
			return true;
		}
		else if (args[0].equals("here")) {
			sender.sendMessage(ChatColor.GOLD + " playng now " + plugin.checker_name.get(p.getName()) + " selection id: " + plugin.checker_id.get(p.getName()));
			return true;
		}
		else if (args[0].equals("debug")) {
			if(args.length == 2) {
				if ( args[1].equals("on") ) {
					plugin.getConfig().set("debug", true);
					plugin.saveConfig();
					sender.sendMessage(ChatColor.GREEN +"debug " + args[1]);
				}
				else if ( args[1].equals("off") ) {
					plugin.getConfig().set("debug", false);
					plugin.saveConfig();
					sender.sendMessage(ChatColor.GREEN +"debug " + args[1]);
				}
			}
			else {
				sender.sendMessage(ChatColor.RED +"debug off/on");
			}
			
			return true;
		}
		else if (args[0].equals("list") ) {
			if(args.length == 2) {
				String name = args[1];
				if(plugin.loc1.containsKey(name)) {
					if( plugin.loc1.get(name).size() != 0) {
						p.sendMessage("-----"+name+"-----");
						for(Location l : plugin.loc1.get(name)) {
							Location l2 = plugin.loc2.get(name).get(plugin.loc1.get(name).indexOf(l));
							p.sendMessage(" ");
							p.sendMessage("id:"+ plugin.loc1.get(name).indexOf(l) + " "+ "location1:" + "x:" + l.getX()+  "," +  "y:" + l.getY()+  ","+  "z:" + l.getZ()+  " "+"location2:" +  "x:" + l2.getX()+  "," +  "y:" + l2.getY()+  ","+  "z:" + l2.getZ()+  " " + "world:"+ l.getWorld().getName());
						}
						p.sendMessage("------------------");
					}
				}
			}
			else {
				p.sendMessage("-----list of group names-----");
				for(String name  : plugin.loc1.keySet()) {
					p.sendMessage(" ");
					p.sendMessage("name:" + name + " ids:" + plugin.loc1.get(name).size()) ;
				}
				p.sendMessage("-----------------------------");
			}
			return true;
		}
		else if (args[0].equals("remove")) {
			String printed = args[1];
			if (plugin.name_sound.size() == 0) {
	    		sender.sendMessage("no sounds");
	    		return true;
	    	}
			
			if (args.length == 3) {
				if(plugin.loc1.get(printed).size() >= Integer.parseInt(args[2])) {
					if (plugin.name_sound.contains(printed)) {
						plugin.getConfig().set( printed + "." + args[2],null);
						plugin.saveConfig();
						
						plugin.loc1.get(printed).remove(Integer.parseInt(args[2]));
						plugin.loc2.get(printed).remove(Integer.parseInt(args[2]));
						plugin.world_sound.get(printed).remove(Integer.parseInt(args[2]));
						sender.sendMessage(ChatColor.GREEN +args[1]+ " with id " + args[2] + " sound removed");
						
						return true;
					}
					else {
						sender.sendMessage( ChatColor.RED +args[1]+" sound not in list");
					}
				}
				
				
	    		return false;
	    	}
			
			if (plugin.name_sound.contains(printed)) {
				
				plugin.getConfig().set("all_rooms." + printed, null);
				plugin.getConfig().set( printed ,null);
				plugin.saveConfig();
				plugin.name_sound.remove(printed);
				plugin.loc1.remove(printed);
				plugin.loc2.remove(printed);
				plugin.sound.remove(printed);
				plugin.world_sound.remove(printed);
				plugin.sound_time.remove(printed);
				plugin.sound_time2.remove(printed);
				plugin.sound2.remove(printed);
				sender.sendMessage(ChatColor.GREEN +args[1]+" sound removed");
				
				return true;
			}
			else {
				sender.sendMessage( ChatColor.RED +args[1]+" sound not in list");
			}
		}
		
		
		else {
			sender.sendMessage( ChatColor.RED +args[0]+" not in list");
		}
		return false;
	}
	
	public WorldEditPlugin getWorldEdit() {
		Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		
		if(p instanceof WorldEditPlugin) return (WorldEditPlugin) p;
		else return null;
	
	}
	public static boolean isPlayerInGroup(Player player, String group) {
	    return player.hasPermission("group." + group);
	}
}
