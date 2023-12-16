package eu.proxyservices.bowbash.commands;

import eu.proxyservices.bowbash.BowBash;
import eu.proxyservices.bowbash.game.GameSession;
import eu.proxyservices.bowbash.game.gamestates.ingame.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EndlessCommand implements CommandExecutor {

    private final GameSession gameSession;

    public EndlessCommand(GameSession gameSession) {
        this.gameSession = gameSession;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("bb.admin")) {
            if (!GameManager.isEndless()) {
                GameManager.setEndless(true);
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                    p.sendTitle("§c", "§d∞-Modus: §caktiviert", 10, 40, 10);
                    p.sendMessage(BowBash.prefix + "§cDie Runde hat ab sofort kein festes Ende und endet erst, nachdem nur noch ein Team übrig ist.");
                }
            } else {
                GameManager.setEndless(false);
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                    p.sendTitle("§c", "§d∞-Modus: §cdeaktiviert", 10, 40, 10);
                    p.sendMessage(BowBash.prefix + "§cDie Runde endet nun wieder nachdem ein Team durch Punkte gewonnen hat.");
                }
            }
        } else {
            sender.sendMessage(BowBash.prefix + "§cDas darfst du nicht.");
        }
        return true;
    }
}
