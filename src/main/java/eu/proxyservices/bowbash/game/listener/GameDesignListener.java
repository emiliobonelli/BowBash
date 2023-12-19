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


public class GameDesignListener implements Listener {

    private final GameSession gameSession;

    public GameDesignListener(GameSession gameSession) {
        for (World world : Bukkit.getWorlds()) {
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
            world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
            world.setDifficulty(Difficulty.PEACEFUL);
            world.setThundering(false);
            world.setStorm(false);
            world.setWeatherDuration(0);
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            world.setTime(1000);
        }

        this.gameSession = gameSession;
    }
    // todo: better solution for this
    public static void setGamePreferences(World world) {
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
        world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
        world.setDifficulty(Difficulty.PEACEFUL);
        world.setThundering(false);
        world.setStorm(false);
        world.setWeatherDuration(0);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        world.setTime(1000);
        world.getWorldBorder().setCenter(0, 0);
        world.getWorldBorder().setSize(200);
        world.getWorldBorder().setWarningDistance(0);
        world.getWorldBorder().setDamageAmount(0);
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
        final GamePlayer gamePlayer = gameSession.getGamePlayer(e.getPlayer());
        if (gamePlayer.getGameTeam() != null) {
            String name = gamePlayer.getGameTeam().getColorCode() + gamePlayer.getGameTeam().getName() + " §7| " + gamePlayer.getGameTeam().getColorCode() + e.getPlayer().getName();
            Bukkit.broadcastMessage(name + "§7: §f" + e.getMessage());
        } else {
            Bukkit.broadcastMessage("§7" + e.getPlayer().getDisplayName() + "§7: §f" + e.getMessage());
        }
    }
}
