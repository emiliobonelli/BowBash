package eu.proxyservices.bowbash.game.countdown;

import eu.proxyservices.bowbash.BowBash;
import eu.proxyservices.bowbash.game.DefaultGamePlayer;
import eu.proxyservices.bowbash.game.GamePlayer;
import eu.proxyservices.bowbash.game.GameSession;
import eu.proxyservices.bowbash.game.GameState;
import eu.proxyservices.bowbash.game.data.StatsManager;
import eu.proxyservices.bowbash.game.gamestates.ingame.KitManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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
            currentTime = 6;
            return true;
        } else {
            return false;
        }
    }

    public void interrupt() {
        if (bukkitTask == null) {
            return;
        }
        bukkitTask.cancel();
        bukkitTask = null;
        if (gameSession.getCurrentGameState() == GameState.LOBBY) {
            currentTime = 20;
            for (Player current : Bukkit.getOnlinePlayers()) {
                current.sendTitle("", "", 0, 0, 0);
                current.setLevel(20);
            }
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
            Bukkit.broadcastMessage(
                    "\n§e§lInformationen für diese Runde§7:\n" +
                    "§7Map → §e" + gameSession.getMap().getMapName() + "\n\n" +
                    "§7Gebaut von §e" + gameSession.getMap().getMapAuthor() + "\n\n" +
                    "§7Statistiken → " + (StatsManager.isEnabled() ? "§aRanked" : "§cUnranked" + "\n\n" +
                    "§7Modus → §e" + gameSession.getGameTeams().size() + "x" + gameSession.getMaxPlayersPerTeam() + " " + (gameSession.getGameManager().isEndless() ? "Endless" : "10-Points") + "\n"
            ));

            for (Player current : Bukkit.getOnlinePlayers()) {
                GamePlayer gp = gameSession.getGamePlayer(current);
                if (gp == null) {
                    gp = new DefaultGamePlayer(gameSession, current);
                    gameSession.addGamePlayer(current, gp);
                }
                if (gp.getGameTeam() == null) {
                    gameSession.getGameManager().joinRandomTeam(gp);
                }
                current.teleport(gp.getGameTeam().getSpawnLocation());
                current.getInventory().clear();
                current.setGlowing(false);
                current.setHealth(20);
                current.setGameMode(GameMode.SURVIVAL);
                KitManager.gameItems(current);
                current.setExp(0);
            }
            gameSession.setGameState(GameState.IN_GAME);
            interrupt();

        } else if (currentTime == 1) {
            Bukkit.broadcastMessage(BowBash.prefix + "§7Die Lobbyphase endet in §eeiner §7Sekunde.");
            for (Player current : Bukkit.getOnlinePlayers()) {
                current.playSound(current.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 1);
              //  current.sendTitle("§e1", " ", 0, 20, 0);
            }
        } else if (currentTime == 5) {
            for (Player current : Bukkit.getOnlinePlayers()) {
                current.playSound(current.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 1);
                current.sendTitle("§e" + currentTime, " ", 0, 20, 0);
                current.getInventory().remove(Material.EMERALD);
                current.getInventory().remove(Material.MAP);
                current.getInventory().remove(Material.ENDER_EYE);
                current.getInventory().remove(Material.ENDER_PEARL);
                current.closeInventory();
            }
            gameSession.getLobbyManager().prepare();
            Bukkit.broadcastMessage(BowBash.prefix + "§7Die Lobbyphase endet in §e" + currentTime + " §7Sekunden.");
        } else if (currentTime >= 2 && currentTime < 5) {
            Bukkit.broadcastMessage(BowBash.prefix + "§7Die Lobbyphase endet in §e" + currentTime + " §7Sekunden.");
            for (Player current : Bukkit.getOnlinePlayers()) {
                current.playSound(current.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 1);
             //   current.sendTitle("§e" + currentTime, " ", 0, 20, 0);
            }
        } else if (currentTime == 15 || currentTime == 10) {
            Bukkit.broadcastMessage(BowBash.prefix + "§7Die Lobbyphase endet in §e" + currentTime + " §7Sekunden.");
            for (Player current : Bukkit.getOnlinePlayers()) {
                current.playSound(current.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 1);
              //  current.sendTitle("§e" + currentTime, " ", 0, 20, 0);
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
