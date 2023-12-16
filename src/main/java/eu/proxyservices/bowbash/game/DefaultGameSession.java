package eu.proxyservices.bowbash.game;

import com.google.common.collect.Maps;
import eu.proxyservices.bowbash.game.countdown.Countdown;
import eu.proxyservices.bowbash.game.countdown.IngameCountdown;
import eu.proxyservices.bowbash.game.countdown.LobbyCountdown;
import eu.proxyservices.bowbash.game.gamestates.ending.ResultManager;
import eu.proxyservices.bowbash.game.gamestates.ingame.GameManager;
import eu.proxyservices.bowbash.game.gamestates.lobby.LobbyManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultGameSession implements GameSession {

    private LobbyManager lobbyManager;
    private GameManager gameManager;
    private final Map<Player, GamePlayer> gamePlayerMap = Maps.newConcurrentMap();
    private final ArrayList<GameTeam> gameTeams = new ArrayList<>();
    private final int maxPlayersPerTeam;

    private GameState gameState;
    private Countdown countdown;
    private GameMap map;

    public DefaultGameSession(List<GameTeam> teams, int maxPlayersPerTeam) {
        gameTeams.addAll(teams);
        this.maxPlayersPerTeam = maxPlayersPerTeam;
    }

    public GameState getCurrentGameState() {
        return gameState;
    }

    @Override
    public Countdown getCountdown() {
        return countdown;
    }

    @Override
    public Map<Player, GamePlayer> getGamePlayers() {
        return gamePlayerMap;
    }

    @Override
    public ArrayList<GameTeam> getGameTeams() {
        return gameTeams;
    }

    public Integer getMaxPlayersPerTeam() {
        return maxPlayersPerTeam;
    }

    public GamePlayer getGamePlayer(Player player) {
        return gamePlayerMap.getOrDefault(player, null);
    }

    @Override
    public void addGamePlayer(Player player, GamePlayer gamePlayer) {
        gamePlayerMap.put(player, gamePlayer);
    }

    public void removeGamePlayer(Player player) {
        gamePlayerMap.remove(player);
    }

    public boolean isRunning() {
        return gameState == GameState.IN_GAME;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        if (gameState == GameState.LOBBY) {
            this.lobbyManager = new LobbyManager(this);
            countdown = new LobbyCountdown(this);
        } else if (gameState == GameState.IN_GAME) {
            new IngameCountdown(this);
            this.gameManager = new GameManager(this);
            gameManager.runScoreboard();

        } else if (gameState == GameState.ENDING) {
            new ResultManager();
        }
    }

    @Override
    public LobbyManager getLobbyManager() {
        return lobbyManager;
    }

    @Override
    public GameManager getGameManager() {
        return gameManager;
    }

    @Override
    public void setMap(GameMap map) {
        this.map = map;
    }

    @Override
    public GameMap getMap() {
        return this.map;
    }

}
