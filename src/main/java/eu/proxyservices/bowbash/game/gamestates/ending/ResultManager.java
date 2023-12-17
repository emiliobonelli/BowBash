package eu.proxyservices.bowbash.game.gamestates.ending;

import eu.proxyservices.bowbash.BowBash;
import eu.proxyservices.bowbash.game.GameTeam;
import eu.proxyservices.bowbash.game.countdown.EndingCountdown;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ResultManager {

    private final int minNeeded = 1;
    private final boolean endless = false;

    public ResultManager() {
        if (endless) {
            return;
        }
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
                Bukkit.broadcastMessage(BowBash.prefix + "§cDas Spiel ist vorbei, da zu wenig Spieler online sind.");
        }
        if (winner != null) {
            Bukkit.broadcastMessage(BowBash.prefix + "§7Team §b" + winner.getName() + " §7hat das Spiel gewonnen!");
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendTitle(winner.getColorCode() + winner.getName(), "§7hat gewonnen", 10, 80, 10);
            }
        }
        new EndingCountdown();
    }

}
