package eu.proxyservices.bowbash.game.data;

import eu.proxyservices.bowbash.BowBash;
import eu.proxyservices.bowbash.game.GameMap;
import eu.proxyservices.bowbash.game.GameSession;
import eu.proxyservices.bowbash.game.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ConfigManager {

    private static final HashMap<GameTeam, Location> map_loc_cache = new HashMap<>();

    private static YamlConfiguration loc_yml;
    private static YamlConfiguration cnf_yml;
    private static File loc_file;
    private static File cnf_file;
    static GameSession gameSession;
    static Location specspawn = null;

    public ConfigManager() {
        loc_file = new File("plugins/BowBash/locations.yml");
        cnf_file = new File("plugins/BowBash/config.yml");
        loc_yml = YamlConfiguration.loadConfiguration(loc_file);
        cnf_yml = YamlConfiguration.loadConfiguration(cnf_file);
    }

    public static List<String> loadMaps() {
        if (loc_yml != null) {
            return cnf_yml.getStringList("maps");
        }
        throw new NullPointerException("ConfigManager: no maps in location.yml found! Cannot load maps!");
    }

    public static GameMap loadMap(String map) {
        if (loc_yml != null) {
            return new GameMap(map, loc_yml.getString(map + ".author"), loc_yml.getInt(map + ".teams"), Material.matchMaterial(Objects.requireNonNull(loc_yml.getString(map + ".item"))));
        }
        throw new NullPointerException("ConfigManager: no maps in location.yml found! Cannot load maps!");
    }


    public static Location loadSpawn(String map, String team) {
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

    public static HashMap<String, String> loadDatabaseSettings() {
        HashMap<String, String> settings = new HashMap<>();
        settings.put("username", cnf_yml.getString("MongoDB.username"));
        settings.put("password", cnf_yml.getString("MongoDB.password"));
        settings.put("url", cnf_yml.getString("MongoDB.url"));
        settings.put("database", cnf_yml.getString("MongoDB.database"));
        settings.put("collection", cnf_yml.getString("MongoDB.collection"));
        return settings;
    }

}
