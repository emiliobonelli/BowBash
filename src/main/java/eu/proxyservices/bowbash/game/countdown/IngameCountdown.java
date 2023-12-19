package eu.proxyservices.bowbash.game.countdown;

import eu.proxyservices.bowbash.BowBash;
import eu.proxyservices.bowbash.game.GameSession;
import eu.proxyservices.bowbash.game.gamestates.ingame.KitManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;

import java.time.Instant;

public class IngameCountdown implements Countdown, Listener {

    private BukkitTask bukkitTask;
    private int currentTime = 7;
    private final GameSession gameSession;

    public IngameCountdown(GameSession gameSession) {
        this.gameSession = gameSession;
        BowBash.plugin.getServer().getPluginManager().registerEvents(this, BowBash.plugin);
        start();
    }

    @Override
    public void start() {
        bukkitTask = Bukkit.getScheduler().runTaskTimer(BowBash.plugin, this, 0L, 20L);
    }

    @Override
    public void interrupt() {
        if (bukkitTask == null) {
            return;
        }
        bukkitTask.cancel();
        bukkitTask = null;
    }

    @Override
    public void run() {
        currentTime--;
        if (currentTime == 0) {
            gameSession.setStartTime(Instant.now());
            interrupt();
            Bukkit.broadcastMessage(BowBash.prefix + "§aDas Spiel startet!");
            for (Player current : Bukkit.getOnlinePlayers()) {
                current.playSound(current.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
            }
        } else if (currentTime >= 1 && currentTime <= 5) {
            Bukkit.broadcastMessage(BowBash.prefix + "§7Das Spiel startet in " + currentTime + "...");
            for (Player current : Bukkit.getOnlinePlayers()) {
                current.playSound(current.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            }
        }
    }

    @EventHandler
    public void move1(PlayerMoveEvent e) {
        if (isRunning() &&  gameSession.getGamePlayer(e.getPlayer()) != null) {
            if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ()) {
                e.setCancelled(true);
            }
        }
    }

    public boolean isRunning() {
        return bukkitTask != null;
    }

    public int time() {
        return currentTime;
    }

    @Override
    public boolean fast_start() {
        return false;
    }

    @Override
    public CountdownType getType() {
        return CountdownType.INGAME_COUNTDOWN;
    }
}
