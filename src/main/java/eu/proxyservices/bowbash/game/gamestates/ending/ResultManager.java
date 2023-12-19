package eu.proxyservices.bowbash.game.gamestates.ending;

import eu.proxyservices.bowbash.BowBash;
import eu.proxyservices.bowbash.game.GameTeam;
import eu.proxyservices.bowbash.game.countdown.EndingCountdown;
import eu.proxyservices.bowbash.game.data.StatsManager;
import eu.proxyservices.bowbash.game.data.StatsType;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Objects;

public class ResultManager {

    private final int minNeeded = 1;
    private final boolean endless = false;

    public ResultManager() {
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
                Bukkit.broadcastMessage(BowBash.prefix + "§cDas Spiel ist vorbei, da zu viele Spieler das Spiel verlassen haben.");
        }
        if (winner != null) {
            Bukkit.broadcastMessage(BowBash.prefix + "§7Team " + winner.getColorCode() + winner.getName() + " §7hat das Spiel gewonnen!");
            winner.getGamePlayerList().forEach(gamePlayer -> {
                StatsManager.updateStat(gamePlayer.getPlayer().getUniqueId(), StatsType.WINS);
                for (int i = 0; i < 3; i++) {
                    spawnFirework(gamePlayer.getPlayer().getLocation(), gamePlayer.getGameTeam().getColorCode());
                }
            });
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendTitle(winner.getColorCode() + winner.getName(), "§7hat gewonnen", 10, 80, 10);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            }
        }
        new EndingCountdown();
    }
    private void spawnFirework(Location location, String colorCode) {
        switch (colorCode) {
            case "§c":
                spawnFirework(location, Color.RED);
                break;
            case "§9":
                spawnFirework(location, Color.BLUE);
                break;
            case "§a":
                spawnFirework(location, Color.GREEN);
                break;
            case "§e":
                spawnFirework(location, Color.YELLOW);
                break;
            default:
                spawnFirework(location, Color.WHITE);
                break;
        }
    }
    private void spawnFirework(Location location, Color color) {
        // Create a new firework at the specified location
        Firework firework = Objects.requireNonNull(location.getWorld()).spawn(location, Firework.class);

        // Create a firework effect with the given color
        FireworkEffect effect = FireworkEffect.builder()
                .withColor(color)
                .with(FireworkEffect.Type.BALL)
                .build();

        // Apply the firework effect to the firework
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(effect);
        firework.setFireworkMeta(meta);
        firework.detonate();
    }
}
