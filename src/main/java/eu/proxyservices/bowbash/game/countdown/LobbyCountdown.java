package eu.proxyservices.bowbash.game.countdown;

import eu.proxyservices.bowbash.BowBash;
import eu.proxyservices.bowbash.game.DefaultGamePlayer;
import eu.proxyservices.bowbash.game.GamePlayer;
import eu.proxyservices.bowbash.game.GameSession;
import eu.proxyservices.bowbash.game.GameState;
import eu.proxyservices.bowbash.game.data.ConfigManager;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class LobbyCountdown implements Countdown {
    private GameSession gameSession;

    private BukkitTask bukkitTask;
    private int currentTime = 20;

    public LobbyCountdown(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    public void start() {
        bukkitTask = Bukkit.getScheduler().runTaskTimer(BowBash.plugin, this, 0L, 20L);
    }

    public boolean fast_start() {
        if (isRunning()) {
            currentTime = 11;
            return true;
        } else {
            return false;
        }
    }

    public void interrupt() {
        Validate.notNull(bukkitTask);

        bukkitTask.cancel();
        bukkitTask = null;
        currentTime = 20;
        for (Player current : Bukkit.getOnlinePlayers()) {
            current.sendTitle("", "");
            current.setLevel(20);
        }
    }

    @Override
    public boolean isRunning() {
        return bukkitTask != null;
    }


    public void run() {
        currentTime--;
        for (Player current : Bukkit.getOnlinePlayers()) {
            current.setLevel(currentTime);
        }
        if (currentTime == 0) {
            interrupt();
            for (Player current : Bukkit.getOnlinePlayers()) {
                GamePlayer gp = gameSession.getGamePlayer(current);
                if (gp == null) {
                    gp = new DefaultGamePlayer(gameSession, current);
                    gameSession.addGamePlayer(current, gp);
                }
                if (gp.getGameTeam() == null) {
                    gameSession.joinRandomTeam(gp);
                }
                current.teleport(ConfigManager.getSpawn(gp.getGameTeam()));
                current.getInventory().clear();
                current.setExp(0);
            }
            gameSession.setGameState(GameState.IN_GAME);

        } else if (currentTime == 1) {
            Bukkit.broadcastMessage(BowBash.prefix + "§7Die Lobbyphase endet in §eeiner §7Sekunde.");
            for (Player current : Bukkit.getOnlinePlayers()) {
                current.playSound(current.getLocation(), Sound.NOTE_PLING, 2, 1);
                current.sendTitle("§e1", " ");
            }
        } else if (currentTime == 2 || currentTime == 3) {
            Bukkit.broadcastMessage(BowBash.prefix + "§7Die Lobbyphase endet in §e" + currentTime + " §7Sekunden.");
            for (Player current : Bukkit.getOnlinePlayers()) {
                current.playSound(current.getLocation(), Sound.NOTE_PLING, 2, 1);
                current.sendTitle("§e" + currentTime, " ");
            }
        } else if (currentTime == 10) {
            for (Player current : Bukkit.getOnlinePlayers()) {
                current.playSound(current.getLocation(), Sound.NOTE_PLING, 2, 1);
                current.getInventory().remove(Material.GOLD_NUGGET);
                current.getInventory().remove(Material.EMPTY_MAP);
                current.closeInventory();
            }
            ConfigManager.loadLocations();
            Bukkit.broadcastMessage(BowBash.prefix + "§7Die Lobbyphase endet in §e" + currentTime + " §7Sekunden.");

        } else if (currentTime == 4 || currentTime == 5 || currentTime == 15) {
            Bukkit.broadcastMessage(BowBash.prefix + "§7Die Lobbyphase endet in §e" + currentTime + " §7Sekunden.");
            for (Player current : Bukkit.getOnlinePlayers()) {
                current.playSound(current.getLocation(), Sound.NOTE_PLING, 2, 1);
            }
        }
    }

    public int time() {
        return currentTime;
    }

    @Override
    public CountdownType getType() {
        return CountdownType.LOBBY_COUNTDOWN;
    }
}
