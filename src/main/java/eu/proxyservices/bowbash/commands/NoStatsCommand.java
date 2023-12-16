package eu.proxyservices.bowbash.commands;

import eu.proxyservices.bowbash.BowBash;
import eu.proxyservices.bowbash.game.GameSession;
import eu.proxyservices.bowbash.game.data.StatsManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class NoStatsCommand implements CommandExecutor {

    private final GameSession gameSession;

    public NoStatsCommand(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("bb.admin"))
            sender.sendMessage("§cDas darfst du nicht.");

        if (StatsManager.isEnabled()) {
            StatsManager.disableStats();

            Bukkit.getOnlinePlayers().forEach(p -> {
                p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                p.sendTitle("§c", "§7Statistiken: §cdeaktiviert", 10, 40, 10);
                p.sendMessage(BowBash.prefix + "§cEs werden keine Statistiken in dieser Runde gespeichert.");
            });
        } else {
                if (StatsManager.enableStats()) {
                    Bukkit.getOnlinePlayers().forEach(p -> {
                        p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                        p.sendTitle("§c", "§7Statistiken: §aaktiviert", 10, 40, 10);
                        p.sendMessage(BowBash.prefix + "§cStatistiken werden in dieser Runde wieder gespeichert.");
                    });
                } else {
                    sender.sendMessage(BowBash.prefix + "§cStatistiken können derzeit nicht aktiviert werden.");
                }
            }
        return true;
    }
}
