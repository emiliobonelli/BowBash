package eu.proxyservices.bowbash.game.gamestates.ending;

import eu.proxyservices.bowbash.BowBash;
import eu.proxyservices.bowbash.game.GameSession;
import eu.proxyservices.bowbash.game.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ResultManager {

    private final GameSession gameSession;
    private int minNeeded = 1;
    private boolean endless = false;

    public ResultManager(GameSession gameSession) {
        this.gameSession = gameSession;
        getResult();
    }

    public void getResult() {
        GameTeam winner = null;
        int highest = 0;
        for (GameTeam gameTeam : GameTeam.values()) {
            if (gameTeam.getPoints() > highest) {
                winner = gameTeam;
                highest = gameTeam.getPoints();
            }
        }
        if (Bukkit.getOnlinePlayers().size() <= minNeeded) {
            Bukkit.broadcastMessage(BowBash.prefix + "§cDas ist Spiel vorbei, da zu viele die Runde verlassen haben.");
        } else if (highest >= 10) {
            //Needed?
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(winner.getColorCode() + winner.getName(), "§7hat gewonnen");
        }
    }

}
