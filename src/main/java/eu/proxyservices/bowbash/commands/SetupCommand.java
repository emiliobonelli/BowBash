package eu.proxyservices.bowbash.commands;

import eu.proxyservices.bowbash.BowBash;
import eu.proxyservices.bowbash.game.GameSession;
import eu.proxyservices.bowbash.game.data.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class SetupCommand implements CommandExecutor {

    //todo: add setup command for description and author of map
    public SetupCommand(GameSession gameSession) {
        this.gameSession = gameSession;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        if (sender instanceof Player p) {
            if (!p.hasPermission("bb.admin") && !p.isOp())
                p.sendMessage(BowBash.prefix + "§cDas darfst du nicht.");

            if (args.length == 0) {
                sendHelp(p);
            } else if (args.length == 1) {
                if (args[0].equals("setspawn")) {
                    p.sendMessage(BowBash.prefix + "§cBitte gib eine Map an.");
                } else {
                    sendHelp(p);
                }
            } else if (args.length == 2) {
                if (args[0].equals("setspawn")) {
                    p.sendMessage(BowBash.prefix + "§cBitte gib ein Team an.");
                } else {
                    sendHelp(p);
                }
            } else if (args[0].equals("setspawn")) {
                    String[] spawns = {"rot", "grün", "gelb", "blau", "spectator"};
                    if (Arrays.asList(spawns).contains(args[2].toLowerCase())) {
                        ConfigManager.saveSpawn(args[1], p.getLocation(), args[2].toLowerCase());
                        p.sendMessage(BowBash.prefix + "§aDer Spawn §7" + args[2].toUpperCase() + " §awurde gesetzt.");
                    } else {
                        p.sendMessage(BowBash.prefix + "§cUnbekannter Spawn! §7(Rot/Blau/Grün/Gelb/Spectator§7)");
                    }
            } else {
                sendHelp(p);
            }
        } else {
            sender.sendMessage("Das geht hier nicht.  :/");
        }
        return true;
    }

    private void sendHelp(Player p) {
        p.sendMessage("§7§l§m===§r §bBowBash Commands  §7§l§m===§r\n" +
                "§e/setup setspawn §7(§cMapname§7) §7(Rot/Blau/Grün/Gelb/Spec§7) §7| §dSetzt den Teamspawn\n" +
                "§e/stats §7(§aSpieler§7) §7| §dZeigt die Statistiken an\n" +
                "§e/savestats §7| §dSpeichert die Statistiken\n" +
                "§e/endless §7| §dSetzt die Runde auf Endlos\n"
                );
    }
}
