package eu.proxyservices.bowbash;

import eu.proxyservices.bowbash.commands.EndlessCommand;
import eu.proxyservices.bowbash.commands.NoStatsCommand;
import eu.proxyservices.bowbash.commands.SetupCommand;
import eu.proxyservices.bowbash.commands.StatsCommand;
import eu.proxyservices.bowbash.game.DefaultGameSession;
import eu.proxyservices.bowbash.game.GameSession;
import eu.proxyservices.bowbash.game.GameState;
import eu.proxyservices.bowbash.game.GameTeam;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BowBash extends JavaPlugin {

    public GameSession gameSession = null;
    public static String prefix = "§7[§bBowBash§7] ";
    public static Plugin plugin;
    public final static int amountTeams = 2;
    public final static int maxPlayersPerTeam = 1;


    @Override
    public void onLoad() {
        final List<GameTeam> teams = new ArrayList<>(Arrays.asList(GameTeam.values()).subList(0, amountTeams));
        gameSession = new DefaultGameSession(teams, maxPlayersPerTeam);
        gameSession.setMap("1");

    }

    @Override
    public void onEnable() {
        plugin = this;
        gameSession.setGameState(GameState.LOBBY);
        final PluginManager pm = getServer().getPluginManager();
        new ConfigManager(gameSession);
        new StatsManager();
        pm.registerEvents(new PlayerConnectionListener(gameSession), this);
        pm.registerEvents(new GameDesignListener(gameSession), this);
        pm.registerEvents(new GameManager(gameSession), this);
        pm.registerEvents(new KitManager(gameSession), this);
        pm.registerEvents(new LobbyManager(gameSession), this);
        getCommand("bb").setExecutor(new SetupCommand());
        getCommand("savestats").setExecutor(new NoStatsCommand(gameSession));
        getCommand("stats").setExecutor(new StatsCommand(gameSession));
        getCommand("endless").setExecutor(new EndlessCommand());
        Bukkit.getConsoleSender().sendMessage("§eLoaded!");
    }

    @Override
    public void onDisable() {
        for (Location loc : GameManager.mapchanges) {
            if (loc.getBlock().getType() != Material.AIR) {
                loc.getBlock().setType(Material.AIR);
            }
        }
        if (StatsManager.areStatsActive()) {
            if (StatsManager.saveAllUser()) {
                Bukkit.getConsoleSender().sendMessage("§7[STATS] §aAlle Benutzer gespeichert!");
                StatsManager.disconnect();
            } else {
                Bukkit.getConsoleSender().sendMessage("§7[STATS] §cStats wurden nicht gespeichert, da sie deaktiviert wurden!");
            }
        }
    }
}
