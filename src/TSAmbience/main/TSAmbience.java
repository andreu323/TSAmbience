package TSAmbience.main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
public class TSAmbience extends JavaPlugin {
	public Map<String,  ArrayList<Location>> loc1 = new HashMap<String,  ArrayList<Location>>();
	public Map<String,  ArrayList<Location>> loc2 = new HashMap<String,  ArrayList<Location>>();
	public Map<String,  ArrayList<String>> world_sound =  new HashMap<String,  ArrayList<String>>();
	public Map<String, String>  sound = new HashMap<String, String>();
	public List<String>  name_sound = new ArrayList<String>();
	public Map<String, String>  sound2 = new HashMap<String, String>();
	public Map<String, Integer> sound_time2 = new HashMap<String, Integer>();
	
	public Map<String, Integer> sound_time = new HashMap<String, Integer>();
	public Map<String, Integer> checker_num = new HashMap<String, Integer>();
	public Map<String, Integer> checker_sound = new HashMap<String, Integer>();
	public Map<String, Integer> id_day = new HashMap<String, Integer>();
	public Map<String, Integer> play = new HashMap<String, Integer>();
	public Map<String, Integer> id_night = new HashMap<String, Integer>();
	public Map<String, String> checker_playng = new HashMap<String, String>();
	public Map<String, String> checker_name = new HashMap<String, String>();
	public Map<String, String> checker_id = new HashMap<String, String>();
	public Map<String, Integer> day = new HashMap<String, Integer>();
	public Map<String, Boolean> playermute = new HashMap<String, Boolean>();
	public Map<String, Integer> night = new HashMap<String, Integer>();
	Logger log = Logger.getLogger("room_sounds");
	public World world = Bukkit.getWorld("World");
	Plugin plugin_private = this;
	public class Command implements TabCompleter {
		@Override
		public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
			if (cmd.getName().equalsIgnoreCase("tsa")) {
	            
	            if (args.length == 1) {
	                
	                ArrayList<String> list = new ArrayList<String>();
	                list.add("mute");
	                list.add("list");
	                list.add("remove");
	                list.add("merge");
	                list.add("here");
	                list.add("debug");
	                return list;
	            }
	            return new ArrayList<String>();
	        }
	        
	        return new ArrayList<String>();
		    
		}
		
		
	}
	@Override
	public void onEnable() {
		this.getCommand("tsa").setTabCompleter(new Command());
		File config = new File(getDataFolder() + File.separator + "config.yml");
		
		if(!config.exists()) {
			log.info("Creating config File :D");
			getConfig().options().copyDefaults(true);
			saveDefaultConfig();
		}
		else{
			reloadConfig();
			
		}
		ConfigurationSection sounds = getConfig().getConfigurationSection("all_rooms");
		//adding rooms from a config
		if(sounds != null) {
			for (String s : sounds.getKeys(false)) {
				sound.put(s,getConfig().getString("all_rooms." + s + ".sound"));
				sound_time.put(s,getConfig().getInt("all_rooms." + s + ".sound_time"));
				sound2.put(s,getConfig().getString("all_rooms." + s + ".sound2"));
				sound_time2.put(s,getConfig().getInt("all_rooms." + s + ".sound_time2"));
				loc1.put(s, new ArrayList<Location>());
				loc2.put(s, new ArrayList<Location>());
				world_sound.put(s, new ArrayList<String>());
				name_sound.add(s);
			}
		}


		for(String i : name_sound) {
			if(i.equals("all_rooms")||i.equals("debug")||i.equals("prefix") )continue;
			ConfigurationSection roomss = getConfig().getConfigurationSection(i);
			if(roomss != null) {
				for(String s :roomss.getKeys(false)) {
					loc1.get(i).add(new Location(world, getConfig().getInt(i+ "." + s + ".x1") ,getConfig().getInt(i + "."+ s + ".y1"),getConfig().getInt(i+ "." + s + ".z1")));
					loc2.get(i).add(new Location(world, getConfig().getInt(i + "."+ s + ".x2") ,getConfig().getInt(i+ "." + s + ".y2"),getConfig().getInt(i+ "." + s + ".z2")));
					world_sound.get(i).add(getConfig().getString(i + "."+ s + ".world"));
					
				}
			}
			
		}
		
		
		//connecting room saunds check (reload version)
		for (Player p : Bukkit.getOnlinePlayers()) {
			soundcheck(p);
		}
		log.info("version: 1.2");
		Bukkit.getPluginManager().registerEvents(new Handler(this), this);
		getCommand("addsound").setExecutor(new addsound(this));
		getCommand("tsa").setExecutor(new tsa(this));
	
		log.info(loc2.toString());
	}
	public void soundcheck(Player p) {
		checker_num.put(p.getName(), 0);
		checker_sound.put(p.getName(), -1);
		checker_playng.put(p.getName(), "false");
		id_day.put(p.getName(), 0);
		long time1 = Bukkit.getServer().getWorld("world").getTime();
		id_night.put(p.getName(), 0);
		day.put(p.getName(), 0);
		night.put(p.getName(), 0);
		if(!getConfig().contains("playermutestatus." + p.getUniqueId().toString())) {
			getConfig().set("playermutestatus." + p.getUniqueId().toString(), false);
			playermute.put(p.getUniqueId().toString(), true);
			saveConfig();
		}
		else {
			
			playermute.put(p.getUniqueId().toString(), Boolean.parseBoolean(getConfig().get("playermutestatus." + p.getUniqueId().toString()).toString()));
			
		}
		for(String i : name_sound) {
			p.stopSound(sound.get(i));
			p.stopSound(sound2.get(i));
		}
		if(time1 > 0 && time1 < 12300) {
			day.put(p.getName(), 1);
	    } else{
		    night.put(p.getName(), 1);
		}
		//roomsounds check and plays music event
		play.put(p.getName(), Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin_private, new Runnable(){
			 
		    public void run() {
		    	
		    	if(loc1.size() != 0) {
		    		
			    	for(String i : name_sound ) {

			    		for (int ii = 0;  loc1.get(i).size() != ii; ii++   ) {
				    	if (world_sound.get(i).get(ii).equals(p.getWorld().getName().toString())) {
				    		
				    		   Location max_loc = new Location(world , loc1.get(i).get(ii).getX() , loc1.get(i).get(ii).getY() , loc1.get(i).get(ii).getZ());
					    	   Location min_loc = new Location(world , loc2.get(i).get(ii).getX() , loc2.get(i).get(ii).getY() , loc2.get(i).get(ii).getZ());
				    		   int x1;
				    		   int x2;
				    		   int y1;
				    		   int y2;
				    		   int z1;
				    		   int z2;
					    	   if (min_loc.getX() <= max_loc.getX()) {
					    		   x1 = (int) min_loc.getX();
					    		   x2 = (int) max_loc.getX();
					    	   }
					    	   else {
					    		   x1 = (int) max_loc.getX();
					    		   x2 = (int) min_loc.getX();
					    	   }
					    	   if (min_loc.getY() <= max_loc.getY()) {
					    		   y1 = (int) min_loc.getY();
					    		   y2 = (int) max_loc.getY();
					    	   }
					    	   else {
					    		   y1 = (int) max_loc.getY();
					    		   y2 = (int) min_loc.getY();
					    	   }
					    	   if (min_loc.getZ() <= max_loc.getZ()) {
					    		   z1 = (int) min_loc.getZ();
					    		   z2 = (int) max_loc.getZ();
					    	   }
					    	   else {
					    		   z1 = (int) max_loc.getZ();
					    		   z2 = (int) min_loc.getZ();
					    	   }
					    	   if (p.getLocation().getX() >= x1 && p.getLocation().getX() <= x2 && p.getLocation().getY() >= y1 && p.getLocation().getY() <= y2 && p.getLocation().getZ() >= z1 && p.getLocation().getZ() <= z2 && playermute.get(p.getUniqueId().toString()) == false) {
				    			   long time = Bukkit.getServer().getWorld("world").getTime();
				    			   
				    			   Runnable r2 = new Runnable() {
				    				   public void run() {
				    					   if (checker_num.get(p.getName()) == 1 && checker_sound.get(p.getName()) != -1) {
				    						   checker_num.put(p.getName() , 0);
				    						   checker_playng.put(p.getName() , "false");
				    					   }
				    				   }
				    			   };
				    			   if(day() == true && night.get(p.getName()) == 1) {
					    					p.stopSound(sound2.get(i));
					    					p.stopSound(sound.get(i));
			    				    		Bukkit.getScheduler().cancelTask(id_day.get(p.getName()));
			    				    		Bukkit.getScheduler().cancelTask(id_night.get(p.getName()));
			    				    		checker_sound.put(p.getName() , -1);
					    					checker_num.put(p.getName() , 0);
					    					checker_playng.put(p.getName() , "false");
					    					checker_name.put(p.getName() , i);
					    					checker_id.put(p.getName(),String.valueOf(ii));
					    					night.put(p.getName() , 0);
					    					day.put(p.getName() , 1);
			    				    } else if (day() == false && day.get(p.getName()) == 1) {
			    				    		p.stopSound(sound.get(i));
			    				    		p.stopSound(sound2.get(i));
			    				    		Bukkit.getScheduler().cancelTask(id_day.get(p.getName()));
			    				    		Bukkit.getScheduler().cancelTask(id_night.get(p.getName()));
			    				    		checker_sound.put(p.getName() , -1);
			    				    		checker_num.put(p.getName() , 0);
			    				    		checker_playng.put(p.getName() , "false");
			    				    		checker_id.put(p.getName(),String.valueOf(ii));
			    				    		checker_name.put(p.getName() , i);
			    				    		night.put(p.getName() , 1);
			    				    		day.put(p.getName() , 0);
			    				    }
				    			   if (checker_num.get(p.getName()) == 0 && checker_playng.get(p.getName()).equals("false") ) {
				    				    if(day()) {
						    				if (sound.get(i) != null) {
						    					if (getConfig().getString("debug")  == "true") {
						    						p.sendMessage(ChatColor.GREEN + "playing " + i+ " -  day");
									    		}
						    					p.playSound(p.getLocation(), sound.get(i), 1.0F, 1.0F);
						    					checker_playng.put(p.getName() , "true");
						    					checker_name.put(p.getName() , i);
						    					checker_id.put(p.getName(),String.valueOf(ii));
						    					Bukkit.getScheduler().cancelTask(id_night.get(p.getName()));
						    					id_day.put(p.getName() , Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin_private, r2, sound_time.get(i) * 20));
						    				}
				    				    } else {
				    				    	if (sound2.get(i) != null) {
				    				    		if (getConfig().getString("debug")  == "true") {
				    				    			p.sendMessage(ChatColor.GREEN + "playing " + i + " -  night");
									    		}
						    					p.playSound(p.getLocation(), sound2.get(i), 1.0F, 1.0F);
						    					checker_playng.put(p.getName() , "true");
						    					checker_name.put(p.getName() , i);
						    					checker_id.put(p.getName(),String.valueOf(ii));
						    					id_night.put(p.getName() , Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin_private, r2, sound_time2.get(i) * 20));
						    				}
				    				    }
				    				   checker_num.put(p.getName() ,1);
				    				   checker_sound.put(p.getName() ,ii);
				    			   }
				    			   

					    	   	}
					    	   
			
					    	   else if (checker_num.get(p.getName()) == 1 && checker_name.get(p.getName()) != i && checkplayerpositionforsound(p)) {
					    		   p.stopSound(sound.get(i));
					    		   if (getConfig().getString("debug")  == "true") {
					    			   p.sendMessage("music stoped");
					    		   }
					    		   p.stopSound(sound2.get(i));
					    		   checker_playng.put(p.getName() ,"false");
					    		   checker_name.put(p.getName() , "null");
					    		   checker_id.put(p.getName(),"null");
					    		   checker_num.put(p.getName() ,0);
					    		   checker_sound.put(p.getName() ,-1);
					    		   Bukkit.getScheduler().cancelTask(id_day.get(p.getName()));
					    		   Bukkit.getScheduler().cancelTask(id_night.get(p.getName()));
					    	   	}
					    	   	else if (checker_num.get(p.getName()) == 1 &&  !checkplayerpositionforsound(p)) {
					    	   		p.stopSound(sound.get(i));
						    		   if (getConfig().getString("debug")  == "true") {
						    			   p.sendMessage("music stoped");
						    		   }
						    		   p.stopSound(sound2.get(i));
						    		   checker_playng.put(p.getName() ,"false");
						    		   checker_name.put(p.getName() , "null");
						    		   checker_id.put(p.getName(),"null");
						    		   checker_num.put(p.getName() ,0);
						    		   checker_sound.put(p.getName() ,-1);
						    		   Bukkit.getScheduler().cancelTask(id_day.get(p.getName()));
						    		   Bukkit.getScheduler().cancelTask(id_night.get(p.getName()));
					    	   	}
					    	    }
					 
					    }
				    	}
				        }
		        
		    
			    }}, 1, 1));
	}
	public boolean checkplayerpositionforsound(Player p) {
		
		for(String i : name_sound ) {

    		for (int ii = 0;  loc1.get(i).size() != ii; ii++   ) {
	    	if (world_sound.get(i).get(ii).equals(p.getWorld().getName().toString())) {
	    		
		Location max_loc = new Location(world , loc1.get(i).get(ii).getX() , loc1.get(i).get(ii).getY() , loc1.get(i).get(ii).getZ());
 	   Location min_loc = new Location(world , loc2.get(i).get(ii).getX() , loc2.get(i).get(ii).getY() , loc2.get(i).get(ii).getZ());
		   int x1;
		   int x2;
		   int y1;
		   int y2;
		   int z1;
		   int z2;
 	   if (min_loc.getX() <= max_loc.getX()) {
 		   x1 = (int) min_loc.getX();
 		   x2 = (int) max_loc.getX();
 	   }
 	   else {
 		   x1 = (int) max_loc.getX();
 		   x2 = (int) min_loc.getX();
 	   }
 	   if (min_loc.getY() <= max_loc.getY()) {
 		   y1 = (int) min_loc.getY();
 		   y2 = (int) max_loc.getY();
 	   }
 	   else {
 		   y1 = (int) max_loc.getY();
 		   y2 = (int) min_loc.getY();
 	   }
 	   if (min_loc.getZ() <= max_loc.getZ()) {
 		   z1 = (int) min_loc.getZ();
 		   z2 = (int) max_loc.getZ();
 	   }
 	   else {
 		   z1 = (int) max_loc.getZ();
 		   z2 = (int) min_loc.getZ();
 	   }
 	   if (p.getLocation().getX() >= x1 && p.getLocation().getX() <= x2 && p.getLocation().getY() >= y1 && p.getLocation().getY() <= y2 && p.getLocation().getZ() >= z1 && p.getLocation().getZ() <= z2 && playermute.get(p.getUniqueId().toString()) == false) {
			   
			  return true;
			   

 	   	}
 	   
	    	}}}
		return false;
	}
	public boolean day() {
	    Server server = Bukkit.getServer();
	    long time = server.getWorld("world").getTime();

	    return time < 12300 || time > 23850;
	}
}

 