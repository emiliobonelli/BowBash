package eu.proxyservices.bowbash.game;

import eu.proxyservices.bowbash.game.data.ConfigManager;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Arrays;

public class GameMap {

    private final String mapName;
    private final String mapAuthor;
    private final int maxTeams;

    private final Material mapItem;
    private int votes;
    public Location spectatorSpawn;

    public GameMap(String mapName, String mapAuthor, int maxTeams, Material forItem) {
        this.mapName = mapName;
        this.mapAuthor = mapAuthor;
        this.maxTeams = maxTeams;
        this.mapItem = forItem;
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
        for (GameTeam gameTeam : Arrays.asList(GameTeam.values()).subList(0, maxTeams)) {
            gameTeam.setSpawnLocation(ConfigManager.loadSpawn(this.mapName.toLowerCase(), gameTeam.getName().toLowerCase()));
        }
        spectatorSpawn = ConfigManager.loadSpawn(this.mapName.toLowerCase(), "spectator");
    }

    public Location getSpectatorSpawn() {
        return spectatorSpawn;
    }

}
