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
    public SetupCommand(GameSession gameSession) {}
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        if (sender instanceof Player p) {
            if (!p.hasPermission("bb.admin") && !p.isOp()) {
                p.sendMessage(BowBash.prefix + "§cDas darfst du nicht.");
                return true;
            }

            if (args.length == 1) {
                if (args[0].equals("setspawn")) {
                    p.sendMessage(BowBash.prefix + "§cBitte gib eine Map an.");
                } else if (args[0].equals("newmap")) {
                    p.sendMessage(BowBash.prefix + "§cBitte gib einen Namen an.");
                } else if (args[0].equals("setitem")) {
                    p.sendMessage(BowBash.prefix + "§cBitte halte ein Item in der Hand.");
                } else {
                    sendHelp(p);
                }
            } else if (args.length == 2) {
                if (args[0].equals("setspawn")) {
                    p.sendMessage(BowBash.prefix + "§cBitte gib ein Team an.");
                } else if (args[0].equals("newmap")) {
                    p.sendMessage(BowBash.prefix + "§cBitte gib einen Autor an.");
                } else if (args[0].equals("setitem")) {
                    if (p.getInventory().getItemInMainHand().getType().isAir()) {
                        p.sendMessage(BowBash.prefix + "§cBitte halte ein Item in der Hand.");
                        return true;
                    }
                    if (ConfigManager.setItem(args[1].toLowerCase(), p.getInventory().getItemInMainHand().getType())) {
                        p.sendMessage(BowBash.prefix + "§aDas Item wurde gesetzt.");
                    } else {
                        p.sendMessage(BowBash.prefix + "§cDas Item konnte nicht gesetzt werden.");
                    }
                } else {
                    sendHelp(p);
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("setspawn")) {
                    String[] spawns = {"rot", "grün", "gelb", "blau", "spectator"};
                    if (Arrays.asList(spawns).contains(args[2].toLowerCase())) {
                        if (ConfigManager.setSpawn(args[1].toLowerCase(), p.getLocation(), args[2].toLowerCase())) {
                            p.sendMessage(BowBash.prefix + "§aDer Spawn wurde gesetzt.");
                        } else {
                            p.sendMessage(BowBash.prefix + "§cDer Spawn konnte nicht gesetzt werden.");
                        }

                    } else {
                        p.sendMessage(BowBash.prefix + "§cUnbekannter Spawn! §7(Rot/Blau/Grün/Gelb/Spectator§7)");
                    }
                } else if (args[0].equalsIgnoreCase("newmap")) {
                    p.sendMessage(BowBash.prefix + "§cBitte gib eine Anzahl an Teams an.");
                } else if (args[0].equalsIgnoreCase("setitem")) {
                    p.sendMessage(BowBash.prefix + "§cBitte gib einen Namen an.");
                } else {
                    sendHelp(p);
                }
            } else if (args.length == 4) {
                if (args[0].equalsIgnoreCase("newmap")) {
                    if (Integer.parseInt(args[3]) > 0 && Integer.parseInt(args[3]) < 5) {
                        if (ConfigManager.mapExists(args[1])) {
                            p.sendMessage(BowBash.prefix + "§cDie Map existiert bereits.");
                            return true;
                        }
                        if (ConfigManager.createMap(args[1], args[2], Integer.parseInt(args[3]))) {
                            p.sendMessage(BowBash.prefix + "§aDie Map §7" + args[1] + " §awurde erstellt.");
                        } else {
                            p.sendMessage(BowBash.prefix + "§cDie Map konnte nicht erstellt werden.");
                        }
                    } else {
                        p.sendMessage(BowBash.prefix + "§cDie Anzahl der Teams muss zwischen 0 und 4 liegen.");
                    }
                    p.sendMessage(BowBash.prefix + "§aDie Map §7" + args[1] + " §awurde erstellt.");
                } else {
                    sendHelp(p);
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
                "§e/setup §7| §dZeigt diese Hilfe an\n" +
                "§e/setup newmap §7(§cMapname§7) §7(§cAutor§7) §7(§cAnzahl Teams§7) §7| §dErstellt eine neue GameMap\n" +
                "§e/setup setitem §7(§cMapname§7) §7| §dSetzt das aktuell in der Hand gehaltene Item als Anzeige\n" +
                "§e/setup setspawn §7(§cMapname§7) §7(Rot/Blau/Grün/Gelb/Spectator§7) §7| §dSetzt den Teamspawn\n" +
                "§e/stats §7(§aSpieler§7) §7| §dZeigt die Statistiken an\n" +
                "§e/savestats §7| §dSpeichert die Statistiken\n" +
                "§e/endless §7| §dSetzt die Runde auf Endlos\n"
                );
    }
}
