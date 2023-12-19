package eu.proxyservices.bowbash.game;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public enum GameTeam {
    BLUE("§9", "Blau", Material.BLUE_STAINED_GLASS, Material.BLUE_BANNER),
    RED("§c", "Rot", Material.RED_STAINED_GLASS, Material.RED_BANNER),
    GREEN("§a", "Grün", Material.LIME_STAINED_GLASS, Material.LIME_BANNER),
    YELLOW("§e", "Gelb", Material.YELLOW_STAINED_GLASS, Material.YELLOW_BANNER),
    ;
    private final ArrayList<GamePlayer> gamePlayerList;
    private Material teamBlock;
    private final Material teamBanner;
    private final String colorCode;
    private final String name;
    private Location spawnLocation;
    private int points;

    GameTeam(String colorCode, String name, Material teamBlock, Material teamBanner) {
        this.teamBlock = teamBlock;
        this.teamBanner = teamBanner;
        this.colorCode = colorCode;
        this.name = name;
        this.points = 0;
        gamePlayerList = new ArrayList<>();
    }

    public Material getTeamBlock() {
        return teamBlock;
    }

    public Material getTeamBanner() {
        return teamBanner;
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

    public void setTeamBlock(Material material) {
        this.teamBlock = material;
    }

}
