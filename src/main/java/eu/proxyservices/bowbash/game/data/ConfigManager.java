package eu.proxyservices.bowbash.game.data;

import eu.proxyservices.bowbash.BowBash;
import eu.proxyservices.bowbash.game.GameMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigManager {

    private static YamlConfiguration loc_yml;
    private static YamlConfiguration cnf_yml;
    private static File loc_file;
    private static File cnf_file;

    public static void loadConfigs() {
        loc_file = new File(BowBash.plugin.getDataFolder(), "locations.yml");
        cnf_file = new File(BowBash.plugin.getDataFolder(), "config.yml");
        if (!loc_file.exists()) {
            try {
                Bukkit.getConsoleSender().sendMessage("§7[§eConfigManager§7] §aCreated location.yml");
                loc_file.getParentFile().mkdirs();
                loc_file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!cnf_file.exists()) {
            try {
                Bukkit.getConsoleSender().sendMessage("§7[§eConfigManager§7] §aCreated config.yml");
                cnf_file.getParentFile().mkdirs();
                cnf_file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (loc_file.exists()) {
            Bukkit.getConsoleSender().sendMessage("§7[§eConfigManager§7] §aLoaded location.yml");
        }
        if (cnf_file.exists()) {
            Bukkit.getConsoleSender().sendMessage("§7[§eConfigManager§7] §aLoaded config.yml");
        }

        loc_yml = YamlConfiguration.loadConfiguration(loc_file);
        cnf_yml = YamlConfiguration.loadConfiguration(cnf_file);
        loadDefaults();
    }
    private static void loadDefaults() {
        loc_yml.addDefault("maps", new String[]{"Standard"});
        loc_yml.addDefault("Standard.author", "ProxyUser");
        loc_yml.addDefault("Standard.teams", 2);
        loc_yml.addDefault("Standard.item", "BOW");
        loc_yml.addDefault("Standard.world", "standard");

        cnf_yml.setComments("MongoDB", Arrays.asList(new String[]{"MongoDB settings for the stats database"}));
        cnf_yml.addDefault("MongoDB.username", "root");
        cnf_yml.addDefault("MongoDB.password", "password");
        cnf_yml.addDefault("MongoDB.url", "localhost:27017");
        cnf_yml.addDefault("MongoDB.database", "BowBash");
        cnf_yml.addDefault("MongoDB.collection", "stats");

        cnf_yml.setComments("gameSettings", Arrays.asList(new String[]{"Settings for the game"}));
        cnf_yml.addDefault("gameSettings.maxPlayersPerTeam", 1);
        cnf_yml.setComments("gameSettings.amountTeams", Arrays.asList(new String[]{"Should be between 2 and 4"}));
        cnf_yml.addDefault("gameSettings.amountTeams", 2);
        cnf_yml.addDefault("gameSettings.prefix", "§7[§bBowBash§7] ");
        cnf_yml.addDefault("gameSettings.endless", false);

        loc_yml.options().copyDefaults(true);
        cnf_yml.options().copyDefaults(true);
        try {
            loc_yml.save(loc_file);
            cnf_yml.save(cnf_file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // reload with new values
        loc_yml = YamlConfiguration.loadConfiguration(loc_file);
        cnf_yml = YamlConfiguration.loadConfiguration(cnf_file);
    }

    public static Map<String, String> loadGameSettings() {
       return new HashMap<>() {{
           put("prefix", cnf_yml.getString("gameSettings.prefix"));
           put("amountTeams", cnf_yml.getString("gameSettings.amountTeams"));
           put("maxPlayersPerTeam", cnf_yml.getString("gameSettings.maxPlayersPerTeam"));
           put("endless", cnf_yml.getString("gameSettings.endless"));
       }};
    }

    public static List<String> loadMaps() {
        if (loc_yml != null) {
            return loc_yml.getStringList("maps");
        }
        throw new NullPointerException("ConfigManager: no maps in location.yml found! Cannot load maps!");
    }

    public static GameMap loadMap(String map) {
        if (loc_yml != null) {
            return new GameMap(map, loc_yml.getString(map + ".author"), loc_yml.getInt(map + ".teams"), Material.matchMaterial(Objects.requireNonNull(loc_yml.getString(map + ".item"))));
        }
        throw new NullPointerException("ConfigManager: no maps in location.yml found! Cannot load maps!");
    }

    public static boolean mapExists(String map) {
        if (loc_yml != null) {
            return loc_yml.contains(map);
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

    public static boolean createMap(String map, String author, int teams) {
        loc_yml.set(map + ".author", author);
        loc_yml.set(map + ".teams", teams);
        loc_yml.set("maps", loadMaps().add(map));
        try {
            loc_yml.save(loc_file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean setItem(String map, Material item) {
        loc_yml.set(map + ".item", item.name());
        try {
            loc_yml.save(loc_file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean setSpawn(String map, Location location, String team) {
        loc_yml.set(map + ".world", location.getWorld().getName());
        loc_yml.set(map + ".spawn." + team + ".x", location.getX());
        loc_yml.set(map + ".spawn." + team + ".y", location.getY());
        loc_yml.set(map + ".spawn." + team + ".z", location.getZ());
        loc_yml.set(map + ".spawn." + team + ".yaw", location.getYaw());
        loc_yml.set(map + ".spawn." + team + ".pitch", location.getPitch());
        try {
            loc_yml.save(loc_file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
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
