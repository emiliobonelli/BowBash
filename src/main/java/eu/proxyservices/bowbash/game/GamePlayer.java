package eu.proxyservices.bowbash.game;

import eu.proxyservices.bowbash.game.gamestates.ingame.GameKit;
import org.bukkit.entity.Player;

public interface GamePlayer {

    Player getPlayer();

    GameTeam getGameTeam();

    void setGameTeam(GameTeam gameTeam);

    void setGameKit(GameKit gameKit);

    GameKit getGameKit();

}
