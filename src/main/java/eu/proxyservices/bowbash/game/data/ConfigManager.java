package eu.proxyservices.bowbash.game.data;

import eu.proxyservices.bowbash.game.GameSession;
import eu.proxyservices.bowbash.game.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ConfigManager {

    private static HashMap<GameTeam, Location> map_loc_cache = new HashMap<>();

    private static YamlConfiguration loc_yml;
    private static YamlConfiguration cnf_yml;
    private static File loc_file;
    private static File cnf_file;
    static GameSession gameSession;
    static Location specspawn = null;

    public ConfigManager(GameSession gameSession) {
        loc_file = new File("plugins/BowBash/locations.yml");
        cnf_file = new File("plugins/BowBash/config.yml");
        loc_yml = YamlConfiguration.loadConfiguration(loc_file);
        cnf_yml = YamlConfiguration.loadConfiguration(cnf_file);
        ConfigManager.gameSession = gameSession;
    }

    public static void loadLocations() {
        String map = "1";
        if (loc_yml != null) {
            gameSession.setMap(map);
            map_loc_cache.put(GameTeam.BLUE, loadSpawn(map, GameTeam.BLUE.getName()));
            map_loc_cache.put(GameTeam.RED, loadSpawn(map, GameTeam.RED.getName()));
            specspawn = loadSpawn(map, "Spectator");
        }
    }

    public static Location getSpawn(GameTeam gameTeam) {
        return map_loc_cache.get(gameTeam);
    }

    public static Location getSpecSpawn() {
        return specspawn;
    }


    private static Location loadSpawn(String map, String team) {
        World w = Bukkit.getWorld(loc_yml.getString(map + ".world"));
        double x = loc_yml.getDouble(map + ".spawn." + team + ".x");
        double y = loc_yml.getDouble(map + ".spawn." + team + ".y");
        double z = loc_yml.getDouble(map + ".spawn." + team + ".z");
        float yaw = (float) loc_yml.getDouble(map + ".spawn." + team + ".yaw");
        float pitch = (float) loc_yml.getDouble(map + ".spawn." + team + ".pitch");
        return new Location(w, x, y, z, yaw, pitch);
    }

    public static void saveSpawn(String map, Location location, String team) {
        loc_yml.set(map + ".world", location.getWorld().getName());
        loc_yml.set(map + ".spawn." + team + ".x", location.getX());
        loc_yml.set(map + ".spawn." + team + ".y", location.getY());
        loc_yml.set(map + ".spawn." + team + ".z", location.getZ());
        loc_yml.set(map + ".spawn." + team + ".yaw", location.getYaw());
        loc_yml.set(map + ".spawn." + team + ".pitch", location.getPitch());
        try {
            loc_yml.save(loc_file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HashMap loadDatabaseSettings() {
        HashMap<String, String> settings = new HashMap<>();
        settings.put("username", cnf_yml.getString("MongoDB.username"));
        settings.put("password", cnf_yml.getString("MongoDB.password"));
        settings.put("url", cnf_yml.getString("MongoDB.url"));
        settings.put("database", cnf_yml.getString("MongoDB.database"));
        settings.put("collection", cnf_yml.getString("MongoDB.collection"));
        return settings;
    }

}
