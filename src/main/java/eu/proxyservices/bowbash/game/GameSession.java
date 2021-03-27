package eu.proxyservices.bowbash.game;

import eu.proxyservices.bowbash.game.countdown.Countdown;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public interface GameSession {

    GameState getCurrentGameState();

    Countdown getCountdown();

    Map<Player, GamePlayer> getGamePlayers();

    List<GameTeam> getGameTeams();

    GamePlayer getGamePlayer(Player player);

    void addGamePlayer(Player player, GamePlayer gamePlayer);

    void removeGamePlayer(Player player);

    boolean isRunning();

    Integer getMaxPlayersPerTeam();

    void setGameState(GameState gameState);

    void joinGameTeam(GamePlayer gamePlayer, GameTeam targetGameTeam);

    void joinRandomTeam(GamePlayer gamePlayer);

    void setMap(String map);

    String getMap();

}
