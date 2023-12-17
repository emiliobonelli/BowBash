package eu.proxyservices.bowbash;

import eu.proxyservices.bowbash.commands.EndlessCommand;
import eu.proxyservices.bowbash.commands.NoStatsCommand;
import eu.proxyservices.bowbash.commands.SetupCommand;
import eu.proxyservices.bowbash.commands.StatsCommand;
import eu.proxyservices.bowbash.game.*;
import eu.proxyservices.bowbash.game.data.ConfigManager;
import eu.proxyservices.bowbash.game.data.StatsManager;
import eu.proxyservices.bowbash.game.gamestates.ingame.GameManager;
import eu.proxyservices.bowbash.game.gamestates.ingame.KitManager;
import eu.proxyservices.bowbash.game.gamestates.lobby.LobbyManager;
import eu.proxyservices.bowbash.game.listener.GameDesignListener;
import eu.proxyservices.bowbash.game.listener.PlayerConnectionListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class BowBash extends JavaPlugin {

    public GameSession gameSession = null;

    public static String prefix;
    public static Plugin plugin;
    public static int amountTeams;
    public static int maxPlayersPerTeam;


    @Override
    public void onEnable() {
        plugin = this;
        ConfigManager.loadConfigs();
        Map<String, String> gameSettings = ConfigManager.loadGameSettings();
        amountTeams = Integer.parseInt(gameSettings.get("amountTeams"));
        maxPlayersPerTeam = Integer.parseInt(gameSettings.get("maxPlayersPerTeam"));
        prefix = gameSettings.get("prefix");

        gameSession = new DefaultGameSession(Arrays.asList(GameTeam.values()).subList(0, amountTeams), maxPlayersPerTeam, gameSettings.get("endless").equalsIgnoreCase("true"));
        gameSession.setGameState(GameState.LOBBY);

        final PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerConnectionListener(gameSession), this);
        pm.registerEvents(new GameDesignListener(gameSession), this);
        pm.registerEvents(new KitManager(gameSession), this);
        getCommand("setup").setExecutor(new SetupCommand(gameSession));
        getCommand("savestats").setExecutor(new NoStatsCommand(gameSession));
        getCommand("stats").setExecutor(new StatsCommand(gameSession));
        getCommand("endless").setExecutor(new EndlessCommand(gameSession));

        Bukkit.getConsoleSender().sendMessage(BowBash.prefix + "§aSuccessfully enabled BowBash!");
    }

    @Override
    public void onDisable() {
        /*
            only for testing purposes
         */
        for (Location loc : GameManager.mapchanges) {
            if (loc.getBlock().getType() != Material.AIR) {
                loc.getBlock().setType(Material.AIR);
            }
        }
        if (StatsManager.isEnabled()) {
            if (StatsManager.saveAllUser()) {
                Bukkit.getConsoleSender().sendMessage("§7[STATS] §aAlle Benutzer gespeichert!");
                StatsManager.disconnect();
            } else {
                Bukkit.getConsoleSender().sendMessage("§7[STATS] §cStats wurden nicht gespeichert, da sie deaktiviert wurden!");
            }
        }
    }
}
