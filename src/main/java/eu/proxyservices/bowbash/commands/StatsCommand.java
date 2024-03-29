package eu.proxyservices.bowbash.commands;

import eu.proxyservices.bowbash.BowBash;
import eu.proxyservices.bowbash.game.GameSession;
import eu.proxyservices.bowbash.game.data.StatsManager;
import eu.proxyservices.bowbash.game.data.StatsType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Objects;

public class StatsCommand implements CommandExecutor {


    public StatsCommand(GameSession gameSession) {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player p) {
            if (!StatsManager.isEnabled()) {
                p.sendMessage(BowBash.prefix + "§cStatistiken können derzeit nicht abgerufen werden.");
                return true;
            }
            if (args.length == 0) {
                Map<StatsType, Integer> stats = StatsManager.getStats(p.getUniqueId());
                sendStats(p, stats);
            } else if (args.length == 1) {
                if (Objects.requireNonNull(Bukkit.getPlayer(args[0])).isValid()) {
                    Map<StatsType, Integer> stats = StatsManager.getStats(Objects.requireNonNull(Bukkit.getPlayer(args[0])).getUniqueId());
                    sendStats(p, stats);
                } else {
                    p.sendMessage(BowBash.prefix + "§7Dieser Spieler konnte nicht gefunden werden.");
                }
            } else {
                p.sendMessage(BowBash.prefix + "§7Dieser Spieler konnte nicht gefunden werden.");
            }
        }
        return true;
    }

    private void sendStats(Player p, Map<StatsType, Integer> stats) {
        p.sendMessage("§8]§8§m------------------§8[");
        p.sendMessage("§b" + p.getName() + "s Statistiken");
        p.sendMessage("§8]§8§m------------------§8[");
        p.sendMessage("§7Wins: §b" + stats.get(StatsType.WINS));
        p.sendMessage("§7Kills: §b" + stats.get(StatsType.KILLS));
        p.sendMessage("§7Deaths: §b" + stats.get(StatsType.DEATHS));
        p.sendMessage("§7Schüsse: §b" + stats.get(StatsType.SHOTS));
        p.sendMessage("§7Blöcke plaziert: §b" + stats.get(StatsType.BLOCKS_PLACED));
        p.sendMessage("§8]§8§m------------------§8[");
    }
}
