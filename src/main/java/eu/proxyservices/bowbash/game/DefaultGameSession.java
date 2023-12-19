package eu.proxyservices.bowbash.game;

import com.google.common.collect.Maps;
import eu.proxyservices.bowbash.BowBash;
import eu.proxyservices.bowbash.game.countdown.Countdown;
import eu.proxyservices.bowbash.game.countdown.IngameCountdown;
import eu.proxyservices.bowbash.game.countdown.LobbyCountdown;
import eu.proxyservices.bowbash.game.data.StatsManager;
import eu.proxyservices.bowbash.game.gamestates.ending.ResultManager;
import eu.proxyservices.bowbash.game.gamestates.ingame.GameManager;
import eu.proxyservices.bowbash.game.gamestates.lobby.LobbyManager;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultGameSession implements GameSession {

    private final LobbyManager lobbyManager;
    private final GameManager gameManager;
    private final Map<Player, GamePlayer> gamePlayerMap = Maps.newConcurrentMap();
    private final ArrayList<GameTeam> gameTeams = new ArrayList<>();
    private final int maxPlayersPerTeam;

    private Instant gameStartTime;
    private GameState gameState;
    private Countdown countdown;
    private GameMap map;

    public DefaultGameSession(List<GameTeam> teams, int maxPlayersPerTeam, boolean endless) {
        gameTeams.addAll(teams);
        this.maxPlayersPerTeam = maxPlayersPerTeam;

        this.lobbyManager = new LobbyManager(this);
        this.gameManager = new GameManager(this);
        gameManager.setEndless(endless);

        BowBash.plugin.getServer().getConsoleSender().sendMessage("§7[§eGameSession§7] §7Current selected mode: §b" +
                getGameTeams().size() + "x" + getMaxPlayersPerTeam() + " (" + (gameManager.isEndless() ? "Endless/" : "10-Points/") +
                (StatsManager.isEnabled() ? "Ranked" : "Unranked") + ")");
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
        if (gamePlayerMap.containsKey(player)) {
            if (gamePlayerMap.get(player).getGameTeam() != null) {
                gamePlayerMap.get(player).getGameTeam().getGamePlayerList().remove(gamePlayerMap.get(player));
            }
            gamePlayerMap.remove(player);
        }
    }

    public boolean isRunning() {
        return gameState == GameState.IN_GAME;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        if (gameState == GameState.LOBBY) {
            countdown = new LobbyCountdown(this);
        } else if (gameState == GameState.IN_GAME) {
            new IngameCountdown(this);

            gameManager.runScoreboard();

        } else if (gameState == GameState.ENDING) {
            new ResultManager();
        } else if (gameState == GameState.SETUP) {
            countdown.interrupt();
            countdown = null;
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

    @Override
    public long getGameTime() {
        if (gameStartTime == null) {
            return 0;
        } else {
            return Duration.between(gameStartTime, Instant.now()).getSeconds();
        }
    }

    @Override
    public void setStartTime(Instant gameTime) {
        this.gameStartTime = gameTime;
    }
}
