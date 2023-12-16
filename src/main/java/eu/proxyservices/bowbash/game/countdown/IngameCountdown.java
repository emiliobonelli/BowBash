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

public class IngameCountdown implements Countdown, Listener {

    private int taskId = -1;
    private int currentTime = 5;
    private final GameSession gameSession;

    public IngameCountdown(GameSession gameSession) {
        this.gameSession = gameSession;
        BowBash.plugin.getServer().getPluginManager().registerEvents(this, BowBash.plugin);
        start();
    }

    @Override
    public void start() {
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(BowBash.plugin, this, 0L, 20L);
    }

    @Override
    public void interrupt() {
        if (isRunning()) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    @Override
    public void run() {
        currentTime--;
        if (currentTime == 0) {
            interrupt();
            for (Player current : Bukkit.getOnlinePlayers()) {
                current.playSound(current.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
                current.sendMessage(BowBash.prefix + "§aDas Spiel startet!");
                KitManager.gameItems(current);
            }
        } else {
            for (Player current : Bukkit.getOnlinePlayers()) {
                current.playSound(current.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                current.sendMessage(BowBash.prefix + "§7Das Spiel startet in " + currentTime + "...");
            }
        }
    }

    @EventHandler
    public void move1(PlayerMoveEvent e) {
        if (isRunning() &&  gameSession.getGamePlayer(e.getPlayer()) != null) {
            e.setCancelled(true);
        }
    }

    public boolean isRunning() {
        return Bukkit.getScheduler().isCurrentlyRunning(taskId);
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
