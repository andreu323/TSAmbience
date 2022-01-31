package TSAmbience.main;



import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
public class addsound implements CommandExecutor{

	private TSAmbience plugin;

	public addsound(TSAmbience plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		for (int i = 0; i < 3; i++) {
			if(args.length == i) {
				return false;
			}
		}
		Player p = (Player) sender;
    	
		if (plugin.name_sound.contains(args[4])) {
    		sender.sendMessage(ChatColor.RED+"this name is used");
    		return true;
    	}
		
		plugin.getConfig().set("all_rooms." + args[4] + ".sound"  , args[0]);
		plugin.getConfig().set("all_rooms." + args[4] + ".sound2"  , args[2]);
		plugin.getConfig().set("all_rooms." + args[4] + ".sound_time"  , Integer.parseInt(args[1].trim()));
		plugin.getConfig().set("all_rooms." + args[4] + ".sound_time2"  , Integer.parseInt(args[3].trim()));
		plugin.saveConfig();
		//ArrayList
		plugin.loc1.put(args[4], new ArrayList<Location>());
		plugin.loc2.put(args[4], new ArrayList<Location>());
		plugin.world_sound.put(args[4], new ArrayList<String>());
		plugin.sound.put(args[4],args[0]);
		plugin.sound_time.put(args[4],Integer.parseInt(args[1].trim()));
		plugin.sound2.put(args[4],args[2]);
		plugin.sound_time2.put(args[4],Integer.parseInt(args[3].trim()));
		plugin.name_sound.add(args[4]);
		sender.sendMessage(ChatColor.GREEN+"created" );
		return true;
	}
	

}
