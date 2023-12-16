package eu.proxyservices.bowbash.game;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public enum GameTeam {
    BLUE("§9", "Blau", Material.BLUE_STAINED_GLASS),
    RED("§c", "Rot", Material.RED_STAINED_GLASS),
    GREEN("§a", "Grün", Material.GREEN_STAINED_GLASS),
    YELLOW("§e", "Gelb", Material.YELLOW_STAINED_GLASS),
    ;
    private final ArrayList<GamePlayer> gamePlayerList;
    private final Material glassBlock;
    private final String colorCode;
    private final String name;
    private Location spawnLocation;
    private int points;

    GameTeam(String colorCode, String name, Material glassBlock) {
        this.glassBlock = glassBlock;
        this.colorCode = colorCode;
        this.name = name;
        this.points = 0;
        gamePlayerList = new ArrayList<>();
    }

    public Material getGlassBlock() {
        return glassBlock;
    }

    public List<GamePlayer> getGamePlayerList() {
        return gamePlayerList;
    }

    public String getColorCode() {
        return colorCode;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public void addPoint() {
        points++;
    }
    public void removePoint() {
        points--;
    }
    public Location getSpawnLocation() {
        return spawnLocation;
    }
    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

}
