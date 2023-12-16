package eu.proxyservices.bowbash.game.countdown;

import eu.proxyservices.bowbash.BowBash;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class EndingCountdown implements Countdown {

    private BukkitTask bukkitTask;
    private int currentTime = 10;

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
                current.sendMessage(BowBash.prefix + "§7Dieser Server wird nun heruntergefahren.");
            }
            Bukkit.shutdown();
        } else if (currentTime == 1) {
            for (Player current : Bukkit.getOnlinePlayers()) {
                current.sendMessage(BowBash.prefix + "§7Dieser Server stoppt in einer Sekunde.");
            }
        } else {
            for (Player current : Bukkit.getOnlinePlayers()) {
                current.sendMessage(BowBash.prefix + "§7Dieser Server stoppt in " + currentTime + " Sekunden.");
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
