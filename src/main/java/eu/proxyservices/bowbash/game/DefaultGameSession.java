package eu.proxyservices.bowbash.game;

import com.google.common.collect.Maps;
import eu.proxyservices.bowbash.BowBash;
import eu.proxyservices.bowbash.game.countdown.Countdown;
import eu.proxyservices.bowbash.game.countdown.EndingCountdown;
import eu.proxyservices.bowbash.game.countdown.IngameCountdown;
import eu.proxyservices.bowbash.game.countdown.LobbyCountdown;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultGameSession implements GameSession {
    private Map<Player, GamePlayer> gamePlayerMap = Maps.newConcurrentMap();
    private final ArrayList<GameTeam> gameTeams = new ArrayList<>();
    private final int maxPlayersPerTeam;

    private GameState gameState;
    private Countdown countdown;
    private String map;

    public DefaultGameSession(List<GameTeam> teams, int maxPlayersPerTeam) {
        gameTeams.addAll(teams);
        this.maxPlayersPerTeam = maxPlayersPerTeam;
        // new GameScoreList(this);
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
            countdown = new LobbyCountdown(this);
        } else if (gameState == GameState.IN_GAME) {
            Countdown igncn = new IngameCountdown(this);
            igncn.start();
        } else if (gameState == GameState.ENDING) {
            Countdown ecn = new EndingCountdown(this);
            ecn.start();
        }
    }

    @Override
    public void joinGameTeam(GamePlayer gamePlayer, GameTeam targetGameTeam) {
        if (targetGameTeam == gamePlayer.getGameTeam()) {
            gamePlayer.getPlayer().sendMessage(BowBash.prefix + "§cDu bist bereits in diesem Team!");
            gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), Sound.ANVIL_BREAK, 2, 2);
        } else if (targetGameTeam.getGamePlayerList().size() >= maxPlayersPerTeam) {
            gamePlayer.getPlayer().sendMessage(BowBash.prefix + "§cDieses Team ist bereits voll!");
        } else {
            if (gamePlayer.getGameTeam() != null) {
                gamePlayer.getGameTeam().getGamePlayerList().remove(gamePlayer);
            }
            gamePlayer.setGameTeam(targetGameTeam);
            targetGameTeam.getGamePlayerList().add(gamePlayer);
            gamePlayer.getPlayer().sendMessage(BowBash.prefix + "§7Du bist nun in " + targetGameTeam.getColorCode() + "Team " + targetGameTeam.getName());
            gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), Sound.LEVEL_UP, 2, 2);
        }
    }

    @Override
    public void joinRandomTeam(GamePlayer gamePlayer) {
        if (GameTeam.RED.getGamePlayerList().size() < maxPlayersPerTeam) {
            GameTeam.RED.getGamePlayerList().add(gamePlayer);
            gamePlayer.setGameTeam(GameTeam.RED);
            gamePlayer.getPlayer().sendMessage(BowBash.prefix + "§7Du bist nun in " + GameTeam.RED.getColorCode() + "Team " + GameTeam.RED.getName());
        } else if (GameTeam.BLUE.getGamePlayerList().size() < maxPlayersPerTeam) {
            GameTeam.BLUE.getGamePlayerList().add(gamePlayer);
            gamePlayer.setGameTeam(GameTeam.BLUE);
            gamePlayer.getPlayer().sendMessage(BowBash.prefix + "§7Du bist nun in " + GameTeam.BLUE.getColorCode() + "Team " + GameTeam.BLUE.getName());
            /**
             } else if (GameTeam.GREEN.getGamePlayerList().size() < maxPlayersPerTeam) {
             } else if (GameTeam.YELLOW.getPlayerList().size < maxPlayersPerTeam) {
             */
        } else {
            Bukkit.getConsoleSender().sendMessage("Es konnte kein passendes Team gefunden werden.");
        }
    }

    @Override
    public void setMap(String map) {
        this.map = map;
    }

    @Override
    public String getMap() {
        return this.map;
    }
}
