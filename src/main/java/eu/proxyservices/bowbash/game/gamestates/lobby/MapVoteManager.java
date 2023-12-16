package eu.proxyservices.bowbash.game.gamestates.lobby;

import eu.proxyservices.bowbash.game.GameMap;
import eu.proxyservices.bowbash.game.data.ConfigManager;

import java.util.List;
import java.util.Map;

public class MapVoteManager {
    //todo: implement

    private List<GameMap> gameMaps;

    private GameMap currentMap = null;
    private boolean pollActive = true;

    public MapVoteManager() {
        List<String> mapNames = ConfigManager.loadMaps();
        for (String mapName : mapNames) {
            gameMaps.add(ConfigManager.loadMap(mapName));
        }

        currentMap = randomMap();
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
    public GameMap getCurrentMap() {
        return currentMap;
    }
}
