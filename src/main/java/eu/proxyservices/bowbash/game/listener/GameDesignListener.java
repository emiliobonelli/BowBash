package eu.proxyservices.bowbash.game.listener;

import eu.proxyservices.bowbash.game.GamePlayer;
import eu.proxyservices.bowbash.game.GameSession;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;


public class GameDesignListener implements Listener {

    private final GameSession gameSession;

    public GameDesignListener(GameSession gameSession) {
        for (World w : Bukkit.getWorlds()) {
            w.setTime(6000);
            w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            w.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            w.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            w.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
            w.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
            w.setDifficulty(Difficulty.PEACEFUL);
            w.setThundering(false);
            w.setStorm(false);
            w.setWeatherDuration(0);
        }
        this.gameSession = gameSession;
    }

    @EventHandler
    public void drop(PlayerDropItemEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
            if (gameSession.isRunning() && e.getItemDrop().getItemStack().getType() == Material.GLASS) {
                return;
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void eat(FoodLevelChangeEvent e) {
        Player p = (Player) e.getEntity();
        p.setFoodLevel(20);
        e.setCancelled(true);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        e.setCancelled(true);
        if (gameSession.isRunning()) {
            final GamePlayer gamePlayer = gameSession.getGamePlayer(e.getPlayer());
            String name = gamePlayer.getGameTeam().getColorCode() + gamePlayer.getGameTeam().getName() + " §7| " + gamePlayer.getGameTeam().getColorCode() + e.getPlayer().getName();
            Bukkit.broadcastMessage(name + "§7: §f" + e.getMessage());
        } else {
            Bukkit.broadcastMessage("§7" + e.getPlayer().getDisplayName() + "§7: §f" + e.getMessage());
        }
    }
}
