package eu.proxyservices.bowbash.commands;

import eu.proxyservices.bowbash.BowBash;
import eu.proxyservices.bowbash.game.GameSession;
import eu.proxyservices.bowbash.game.data.StatsManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NoStatsCommand implements CommandExecutor {

    private GameSession gameSession;

    public NoStatsCommand(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("bb.admin")) {
            if (StatsManager.areStatsActive()) {
                StatsManager.disableStats();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 1, 1);
                    p.sendTitle("§c", "§cStats sind nun deaktiviert");
                    p.sendMessage(BowBash.prefix + "§cEs werden keine Statistiken in dieser Runde gespeichert.");
                }
            } else {
                StatsManager.enableStats();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 1, 1);
                    p.sendTitle("§c", "§aStats sind nun aktiviert");
                    p.sendMessage(BowBash.prefix + "§cStatistiken werden in dieser Runde wieder gespeichert.");
                }
            }
        } else {
            sender.sendMessage("§cDas darfst du nicht.");
        }
        return false;
    }
}
