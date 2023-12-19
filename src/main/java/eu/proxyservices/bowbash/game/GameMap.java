package eu.proxyservices.bowbash.game;

import eu.proxyservices.bowbash.game.data.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldCreator;

import java.util.Arrays;

public class GameMap {

    private final String mapName;
    private final String mapAuthor;
    private final int maxTeams;

    private final Material mapItem;
    private int votes;
    private Location spectatorSpawn;

    private int minHeight;

    public GameMap(String mapName, String mapAuthor, int maxTeams, Material forItem) {
        this.mapName = mapName;
        this.mapAuthor = mapAuthor;
        this.maxTeams = maxTeams;
        this.mapItem = forItem;
        this.minHeight = 0;
    }

    public String getMapName() {
        return mapName;
    }

    public String getMapAuthor() {
        return mapAuthor;
    }

    public int getMaxTeams() {
        return maxTeams;
    }

    public Material getMapItem() {
        return mapItem;
    }

    public int getVotes() {
        return votes;
    }

    public void addVote() {
        votes++;
    }

    public void removeVote() {
        votes--;
    }

    public void loadLocations() {
        Bukkit.getServer().getConsoleSender().sendMessage("§7[§eGameMap§7] §7Loading locations for map §b" + mapName);
        if (Bukkit.getWorld(mapName) == null) {
            WorldCreator wc = new WorldCreator(mapName);
            wc.generator(new eu.proxyservices.bowbash.utils.VoidGenerator());
            wc.createWorld();
        }
        for (GameTeam gameTeam : Arrays.asList(GameTeam.values()).subList(0, maxTeams)) {
            gameTeam.setSpawnLocation(ConfigManager.loadSpawn(this.mapName, gameTeam.getName().toLowerCase()));
            Bukkit.getConsoleSender().sendMessage("§7[§eGameMap§7] §7Loaded spawn for team §b" + gameTeam.getName());
            Bukkit.getConsoleSender().sendMessage("§7[§eGameMap§7] §7Spawn location: §b" + gameTeam.getSpawnLocation().toString());
        }
        spectatorSpawn = ConfigManager.loadSpawn(this.mapName, "spectator");
        minHeight = ConfigManager.loadMinHeight(this.mapName);
    }

    public Location getSpectatorSpawn() {
        return spectatorSpawn;
    }

    public int getMinHeight() {
        return minHeight;
    }
}
