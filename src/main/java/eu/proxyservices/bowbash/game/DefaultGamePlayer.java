package eu.proxyservices.bowbash.game;

import eu.proxyservices.bowbash.game.gamestates.ingame.GameKit;
import org.bukkit.entity.Player;

public class DefaultGamePlayer implements GamePlayer {

    private final GameSession gameSession;
    private final Player player;
    private GameKit gameKit;
    private GameTeam gameTeam;

    public DefaultGamePlayer(GameSession gameSession, Player player) {
        this.gameSession = gameSession;
        this.player = player;
        this.gameTeam = null;
        this.gameKit = null;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public GameTeam getGameTeam() {
        return gameTeam;
    }

    @Override
    public void setGameTeam(GameTeam gameTeam) {
        this.gameTeam = gameTeam;
    }

    @Override
    public void setGameKit(GameKit gameKit) {
        this.gameKit = gameKit;
    }

    @Override
    public GameKit getGameKit() {
        return gameKit;
    }
}
