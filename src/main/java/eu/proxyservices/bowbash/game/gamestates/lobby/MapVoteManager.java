package eu.proxyservices.bowbash.game.gamestates.lobby;

import eu.proxyservices.bowbash.BowBash;
import eu.proxyservices.bowbash.game.GameMap;
import eu.proxyservices.bowbash.game.GameSession;
import eu.proxyservices.bowbash.game.data.ConfigManager;

import java.util.ArrayList;
import java.util.List;

public class MapVoteManager {

    private final GameSession gameSession;
    private final List<GameMap> gameMaps;

    private GameMap currentMap = null;
    private boolean pollActive = true;

    public MapVoteManager(GameSession gameSession) {
        this.gameSession = gameSession;

        gameMaps = new ArrayList<>();
        List<String> mapNames = ConfigManager.loadMaps();
        for (String mapName : mapNames) {
            gameMaps.add(ConfigManager.loadMap(mapName));
        }

        currentMap = randomMap();
        BowBash.plugin.getServer().getConsoleSender().sendMessage("§7[§eMapVoteManager§7] §aLoaded " + gameMaps.size() + " maps!");
        BowBash.plugin.getServer().getConsoleSender().sendMessage("§7[§eMapVoteManager§7] §7Current selected map: " + currentMap.getMapName());
    }

    public List<GameMap> getGameMaps() {
        return gameMaps;
    }
    public GameMap countVotes() {
        if (!pollActive) {
            return currentMap;
        }
        int highestVotes = 0;
        GameMap highestVotedMap = randomMap();
        for (GameMap gameMap : gameMaps) {
            if (gameMap.getVotes() > highestVotes) {
                highestVotes = gameMap.getVotes();
                highestVotedMap = gameMap;
            }
        }
        this.currentMap = highestVotedMap;
        return highestVotedMap;
    }

    public void forceMap(String gameMap) {
        this.currentMap = getMapByName(gameMap);
        this.pollActive = false;
    }
    private GameMap randomMap() {
        return gameMaps.get((int) (Math.random() * gameMaps.size()));
    }

    private GameMap getMapByName(String name) {
        for (GameMap gameMap : gameMaps) {
            if (gameMap.getMapName().equalsIgnoreCase(name)) {
                return gameMap;
            }
        }
        return null;
    }

    private void setCurrentMap(GameMap gameMap) {
        this.currentMap = gameMap;
        gameSession.setMap(gameMap);
    }
    public GameMap getCurrentMap() {
        return currentMap;
    }
}
