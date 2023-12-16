package eu.proxyservices.bowbash.game.listener;

import eu.proxyservices.bowbash.BowBash;
import eu.proxyservices.bowbash.game.DefaultGamePlayer;
import eu.proxyservices.bowbash.game.GamePlayer;
import eu.proxyservices.bowbash.game.GameSession;
import eu.proxyservices.bowbash.game.GameState;
import eu.proxyservices.bowbash.game.gamestates.lobby.LobbyManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class PlayerConnectionListener implements Listener {

    private final GameSession gameSession;
    private BukkitTask bukkitTask;

    public PlayerConnectionListener(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        if (gameSession.getCurrentGameState() == GameState.LOBBY) {
            GamePlayer gp = new DefaultGamePlayer(gameSession, e.getPlayer());
            gameSession.addGamePlayer(e.getPlayer(), gp);
            LobbyManager.items(e.getPlayer());
            e.getPlayer().setLevel(gameSession.getCountdown().time());
            e.getPlayer().teleport(Bukkit.getServer().getWorld("world").getSpawnLocation());
            Bukkit.broadcastMessage(BowBash.prefix + "§b" + e.getPlayer().getDisplayName() + " §7hat die Lobby betreten.");
            if (!gameSession.getCountdown().isRunning() && Bukkit.getOnlinePlayers().size() != gameSession.getGameTeams().size() * gameSession.getMaxPlayersPerTeam()) {
                if (bukkitTask == null) {
                    start();
                }
            } else if (!gameSession.getCountdown().isRunning() && Bukkit.getOnlinePlayers().size() == gameSession.getGameTeams().size() * gameSession.getMaxPlayersPerTeam()) {
                gameSession.getCountdown().start();
                if (bukkitTask != null) {
                    interrupt();
                }
            } else if (gameSession.getCountdown().isRunning() && Bukkit.getOnlinePlayers().size() == gameSession.getGameTeams().size() * gameSession.getMaxPlayersPerTeam()) {
                e.getPlayer().kickPlayer("§cDie Runde ist voll! Bitte betrete eine andere Runde.");
            }
        } else {
            e.getPlayer().setGameMode(GameMode.SPECTATOR);
            e.getPlayer().teleport(gameSession.getMap().getSpectatorSpawn());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (gameSession.getCurrentGameState() == GameState.LOBBY) {
            if (Bukkit.getOnlinePlayers().size() <= gameSession.getGameTeams().size() * gameSession.getMaxPlayersPerTeam()) {
                if (gameSession.getCountdown().isRunning()) {
                    gameSession.getCountdown().interrupt();
                }
                e.setQuitMessage(null);
                Bukkit.broadcastMessage(BowBash.prefix + "§7Der Spieler " + e.getPlayer().getDisplayName() + "§7 hat den Server verlassen.");
                if (Bukkit.getOnlinePlayers().isEmpty()) {
                    interrupt();
                }
            }
            if (gameSession.getGamePlayer(e.getPlayer()) != null) {
                gameSession.removeGamePlayer(e.getPlayer());
            }
        } else if (gameSession.getCurrentGameState() == GameState.IN_GAME) {
            Player p = e.getPlayer();
            if (gameSession.getGamePlayers().containsKey(e.getPlayer())) {
                gameSession.removeGamePlayer(e.getPlayer());
                Bukkit.broadcastMessage(BowBash.prefix + "§7Der Spieler " + e.getPlayer().getDisplayName() + "§7 hat den Server verlassen.");
                if (Bukkit.getOnlinePlayers().size() <= 1) {
                    gameSession.setGameState(GameState.ENDING);
                }
            }
        } else if (gameSession.getCurrentGameState() == GameState.ENDING) {
            e.setQuitMessage(null);
            Bukkit.broadcastMessage(BowBash.prefix + "§7Der Spieler " + e.getPlayer().getDisplayName() + "§7 hat den Server verlassen.");
        }
    }

    public void start() {
        bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(BowBash.plugin, (Runnable) new BukkitRunnable() {
            @Override
            public void run() {
                if (gameSession.getGameTeams().size() * gameSession.getMaxPlayersPerTeam() - Bukkit.getOnlinePlayers().size() == 1) {
                    Bukkit.broadcastMessage(BowBash.prefix + "§cWarten auf einen weiteren Spieler...");
                } else {
                    Bukkit.broadcastMessage(BowBash.prefix + "§cWarten auf " + (gameSession.getGameTeams().size() * gameSession.getMaxPlayersPerTeam() - Bukkit.getOnlinePlayers().size()) + " weitere Spieler...");
                }
            }
        }, 5 * 20L, 30 * 20L);
    }

    public void interrupt() {
        bukkitTask.cancel();
        bukkitTask = null;
    }

}
