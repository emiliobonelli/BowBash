package eu.proxyservices.bowbash.game.countdown;

import eu.proxyservices.bowbash.BowBash;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class EndingCountdown implements Countdown {

    private BukkitTask bukkitTask;
    private int currentTime = 15;

    public EndingCountdown() {
        start();
    }

    @Override
    public void start() {
        bukkitTask = Bukkit.getScheduler().runTaskTimer(BowBash.plugin, this, 0L, 20L);
    }

    @Override
    public void run() {
        currentTime--;
        if (currentTime == 0) {
            interrupt();
            for (Player current : Bukkit.getOnlinePlayers()) {
                current.sendMessage(BowBash.prefix + "§cDieser Server wird nun heruntergefahren.");
            }
            Bukkit.spigot().restart();
        } else if (currentTime == 1) {
            for (Player current : Bukkit.getOnlinePlayers()) {
                current.sendMessage(BowBash.prefix + "§cDieser Server stoppt in einer Sekunde.");
            }
        } else if (currentTime <= 5 && currentTime >= 2) {
            for (Player current : Bukkit.getOnlinePlayers()) {
                current.sendMessage(BowBash.prefix + "§cDieser Server stoppt in " + currentTime + " Sekunden.");
            }
        }
    }

    /**
     * Unnecessary things...
     */

    @Override
    public void interrupt() {

        Bukkit.getScheduler().cancelTask(bukkitTask.getTaskId());
        bukkitTask = null;
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
        return CountdownType.ENDING_COUNTDOWN;
    }
}
