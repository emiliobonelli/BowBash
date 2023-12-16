package eu.proxyservices.bowbash.game.countdown;

import eu.proxyservices.bowbash.BowBash;
import eu.proxyservices.bowbash.game.DefaultGamePlayer;
import eu.proxyservices.bowbash.game.GamePlayer;
import eu.proxyservices.bowbash.game.GameSession;
import eu.proxyservices.bowbash.game.GameState;
import eu.proxyservices.bowbash.game.data.ConfigManager;
import eu.proxyservices.bowbash.game.data.StatsManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class LobbyCountdown implements Countdown {
    private final GameSession gameSession;

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
        bukkitTask.cancel();
        bukkitTask = null;
        currentTime = 20;
        for (Player current : Bukkit.getOnlinePlayers()) {
            current.sendTitle("", "", 0, 0, 0);
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
                current.teleport(gp.getGameTeam().getSpawnLocation());
                current.getInventory().clear();
                current.setExp(0);
            }
            gameSession.setGameState(GameState.IN_GAME);

        } else if (currentTime == 1) {
            Bukkit.broadcastMessage(BowBash.prefix + "§7Die Lobbyphase endet in §eeiner §7Sekunde.");
            for (Player current : Bukkit.getOnlinePlayers()) {
                current.playSound(current.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 1);
                current.sendTitle("§e1", " ", 0, 20, 0);
            }
        } else if (currentTime >= 2 && currentTime <= 5 || currentTime == 15) {
            Bukkit.broadcastMessage(BowBash.prefix + "§7Die Lobbyphase endet in §e" + currentTime + " §7Sekunden.");
            for (Player current : Bukkit.getOnlinePlayers()) {
                current.playSound(current.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 1);
                current.sendTitle("§e" + currentTime, " ", 0, 20, 0);
            }
        } else if (currentTime == 10) {
            for (Player current : Bukkit.getOnlinePlayers()) {
                current.playSound(current.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 1);
                current.sendTitle("§e" + currentTime, " ", 0, 20, 0);
                current.getInventory().remove(Material.GOLD_NUGGET);
                current.getInventory().remove(Material.MAP);
                current.closeInventory();
            }
            // todo: close poll for map and load most voted map
            gameSession.getMap().loadLocations();
            Bukkit.broadcastMessage(BowBash.prefix + "§7Die Lobbyphase endet in §e" + currentTime + " §7Sekunden.");
            Bukkit.broadcastMessage(BowBash.prefix +
                    "§7Informationen für diese §eRunde§7:\n" +
                    "§7Map → §e" + gameSession.getMap().getMapName() + "\n" +
                    "§7Gebaut von §e" + gameSession.getMap().getMapAuthor() + "\n" +
                    "§7Statistiken → " + (StatsManager.isEnabled() ? "§aaktiviert" : "§cdeaktiviert"));
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
