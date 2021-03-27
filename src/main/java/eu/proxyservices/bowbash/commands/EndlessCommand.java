package eu.proxyservices.bowbash.commands;

import eu.proxyservices.bowbash.BowBash;
import eu.proxyservices.bowbash.game.gamestates.ingame.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EndlessCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("bb.admin")) {
            if (!GameManager.isEndless()) {
                GameManager.setEndless(true);
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 1, 1);
                    p.sendTitle("§c", "§aDie Runde ist nun ohne Ende");
                    p.sendMessage(BowBash.prefix + "§cDie Runde hat ab sofort kein festes Ende und endet erst beim Verlassen der Runde.");
                }
            } else {
                GameManager.setEndless(false);
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 1, 1);
                    p.sendTitle("§c", "§aDie Runde endet wieder normal");
                    p.sendMessage(BowBash.prefix + "§cDie Runde hat nun wieder ein festes Ende.");
                }
            }
        } else {
            sender.sendMessage(BowBash.prefix + "§cDas darfst du nicht.");
        }
        return false;
    }
}
