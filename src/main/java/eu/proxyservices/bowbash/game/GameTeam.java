package eu.proxyservices.bowbash.game;

import java.util.ArrayList;
import java.util.List;

public enum GameTeam {
    BLUE(11, "§9", "Blau"),
    RED(14, "§c", "Rot"),
    // GREEN(13, "§a", "Grün", 0),
    // YELLOW(4, "§e", "Gelb", 0),
    ;
    private ArrayList<GamePlayer> gamePlayerList;
    private final byte teamDurabId;
    private final String colorCode;
    private final String name;
    private int points;

    GameTeam(int teamDurabId, String colorCode, String name) {
        this.teamDurabId = (byte) teamDurabId;
        this.colorCode = colorCode;
        this.name = name;
        this.points = 0;
        gamePlayerList = new ArrayList<>();
    }

    public byte getTeamDurabId() {
        return teamDurabId;
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

}
