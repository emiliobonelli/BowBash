package eu.proxyservices.bowbash.game.listener;

import eu.proxyservices.bowbash.game.GamePlayer;
import eu.proxyservices.bowbash.game.GameSession;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;


public class GameDesignListener implements Listener {

    private GameSession gameSession;

    public GameDesignListener(GameSession gameSession) {
        for (World w : Bukkit.getWorlds()) {
            w.setAnimalSpawnLimit(0);
            w.setAmbientSpawnLimit(0);
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
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void rain(WeatherChangeEvent e) {
        e.setCancelled(true);
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
