package eu.proxyservices.bowbash.commands;

import eu.proxyservices.bowbash.BowBash;
import eu.proxyservices.bowbash.game.data.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class SetupCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!p.hasPermission("bb.setup"))
                p.sendMessage(BowBash.prefix + "§cDas darfst du nicht.");

            if (args.length == 0) {
                sendHelp(p);
            } else if (args.length == 1) {
                sendHelp(p);
            } else if (args.length == 3) {
                if (args[0].equals("setspawn")) {
                    ArrayList<String> ca = new ArrayList<>();
                    ca.add("Rot");
                    ca.add("Gruen");
                    ca.add("Gelb");
                    ca.add("Blau");
                    ca.add("Spec");
                    if (ca.contains(args[2])) {
                        ConfigManager.saveSpawn(args[1], p.getLocation(), args[2]);
                        p.sendMessage(BowBash.prefix + "§aDer Spawn von §7" + args[2].toLowerCase() + " §awurde gesetzt.");
                    } else {
                        p.sendMessage(BowBash.prefix + "§cUnbekannter Spawn!");
                    }
                } else {
                    sendHelp(p);
                }
            } else {
                sendHelp(p);
            }
        } else {
            sender.sendMessage("Das geht hier nicht.  :/");
        }
        return false;
    }

    private void sendHelp(Player p) {
        p.sendMessage("§7§l§m===§r §bBowBash Commands  §7§l§m===§r\n" +
                "§e§l/bb setspawn §7(§aMapname) §7(Rot/Blau/Gruen/Gelb/Spec§7) §7▌ §dSetzt den Teamspawn\n" +
                "§e§l/start §7▌ §dStartet das Spiel früher\n");
    }
}
