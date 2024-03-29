package eu.proxyservices.bowbash.game.gamestates.ingame;

import eu.proxyservices.bowbash.BowBash;
import eu.proxyservices.bowbash.game.GamePlayer;
import eu.proxyservices.bowbash.game.GameSession;
import eu.proxyservices.bowbash.game.GameState;
import eu.proxyservices.bowbash.game.GameTeam;
import eu.proxyservices.bowbash.game.data.StatsManager;
import eu.proxyservices.bowbash.game.data.StatsType;
import eu.proxyservices.bowbash.utils.ScoreboardHelper;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.BlockIterator;

import java.util.ArrayList;
import java.util.HashMap;

public class GameManager implements Listener {

    private final GameSession gameSession;
    private final HashMap<Player, Player> pvp_cache = new HashMap<>();
    public static ArrayList<Location> mapchanges = new ArrayList<>();
    private static boolean endlessRound = false;

    public boolean isEndless() {
        return endlessRound;
    }

    public void setEndless(Boolean endless) {
        endlessRound = endless;
    }

    public GameManager(GameSession gameSession) {
        BowBash.plugin.getServer().getPluginManager().registerEvents(this, BowBash.plugin);
        this.gameSession = gameSession;
    }

    public void joinGameTeam(GamePlayer gamePlayer, GameTeam targetGameTeam) {
        if (targetGameTeam == gamePlayer.getGameTeam()) {
            gamePlayer.getPlayer().sendMessage(BowBash.prefix + "§cDu bist bereits in diesem Team!");
            gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), Sound.BLOCK_ANVIL_BREAK, 2, 2);
        } else if (targetGameTeam.getGamePlayerList().size() >= gameSession.getMaxPlayersPerTeam()) {
            gamePlayer.getPlayer().sendMessage(BowBash.prefix + "§cDieses Team ist bereits voll!");
        } else {
            if (gamePlayer.getGameTeam() != null) {
                gamePlayer.getGameTeam().getGamePlayerList().remove(gamePlayer);
            }
            gamePlayer.setGameTeam(targetGameTeam);
            targetGameTeam.getGamePlayerList().add(gamePlayer);
            gamePlayer.getPlayer().sendMessage(BowBash.prefix + "§7Du bist nun in " + targetGameTeam.getColorCode() + "Team " + targetGameTeam.getName());
            gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 2);
        }
    }

    public void joinRandomTeam(GamePlayer gamePlayer) {
        if (GameTeam.RED.getGamePlayerList().size() < gameSession.getMaxPlayersPerTeam()) {
            GameTeam.RED.getGamePlayerList().add(gamePlayer);
            gamePlayer.setGameTeam(GameTeam.RED);
            gamePlayer.getPlayer().sendMessage(BowBash.prefix + "§7Du bist nun in " + GameTeam.RED.getColorCode() + "Team " + GameTeam.RED.getName());
        } else if (GameTeam.BLUE.getGamePlayerList().size() < gameSession.getMaxPlayersPerTeam()) {
            GameTeam.BLUE.getGamePlayerList().add(gamePlayer);
            gamePlayer.setGameTeam(GameTeam.BLUE);
            gamePlayer.getPlayer().sendMessage(BowBash.prefix + "§7Du bist nun in " + GameTeam.BLUE.getColorCode() + "Team " + GameTeam.BLUE.getName());
        } else if (GameTeam.GREEN.getGamePlayerList().size() < gameSession.getMaxPlayersPerTeam()) {
            GameTeam.GREEN.getGamePlayerList().add(gamePlayer);
            gamePlayer.setGameTeam(GameTeam.GREEN);
            gamePlayer.getPlayer().sendMessage(BowBash.prefix + "§7Du bist nun in " + GameTeam.GREEN.getColorCode() + "Team " + GameTeam.GREEN.getName());
        } else if (GameTeam.YELLOW.getGamePlayerList().size() < gameSession.getMaxPlayersPerTeam()) {
            GameTeam.YELLOW.getGamePlayerList().add(gamePlayer);
            gamePlayer.setGameTeam(GameTeam.YELLOW);
            gamePlayer.getPlayer().sendMessage(BowBash.prefix + "§7Du bist nun in " + GameTeam.YELLOW.getColorCode() + "Team " + GameTeam.YELLOW.getName());
        } else {
            Bukkit.getConsoleSender().sendMessage("Es konnte kein passendes Team gefunden werden.");
        }
    }

    public void runScoreboard() {
        new ScoreboardHelper(gameSession);
    }

    @EventHandler
    public void respawn(PlayerRespawnEvent e) {
        if (gameSession.isRunning() && gameSession.getGamePlayer(e.getPlayer()) != null) {
            e.setRespawnLocation(gameSession.getGamePlayer(e.getPlayer()).getGameTeam().getSpawnLocation());
            e.getPlayer().getInventory().clear();
            KitManager.gameItems(e.getPlayer());
            e.getPlayer().setInvulnerable(true);
             new BukkitRunnable() {
                @Override public void run() {
                    e.getPlayer().setInvulnerable(false);
                }
            }.runTaskLater(BowBash.plugin, 20 * 3);

        }
    }

    @EventHandler
    public void fastkill(PlayerMoveEvent e) {
        if (gameSession.isRunning()) {
            if (e.getPlayer().getLocation().getY() < gameSession.getMap().getMinHeight()) {
                e.getPlayer().setHealth(0);
            }
        }
    }

    @EventHandler
    public void falldamage(EntityDamageEvent e) {
        if (gameSession.isRunning()) {
            if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                e.setCancelled(true);
            }
        } else {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void damage(EntityDamageByEntityEvent e) {
        if (e.getDamager().getType() == EntityType.PLAYER && e.getEntity().getType() == EntityType.PLAYER) {
            Player damager = (Player) e.getDamager();
            Player p = (Player) e.getEntity();
            if (gameSession.isRunning()) {
                if (gameSession.getGamePlayer(p) != null && gameSession.getGamePlayer(damager) != null && p != damager) {
                    pvp_cache.put(p, damager);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            pvp_cache.remove(p);
                        }
                    }.runTaskLater(BowBash.plugin, 20 * 20);
                    e.setCancelled(false);
                } else {
                    e.setCancelled(true);
                }
            } else {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void k1(PlayerDeathEvent e) {
        e.setDeathMessage(null);
        e.setKeepInventory(true);
        org.bukkit.entity.Player p = e.getEntity();
        if (gameSession.isRunning()) {
            if (e.getEntity().getKiller() != null && e.getEntity().getKiller() != e.getEntity()) {
                pvp_cache.remove(p);
                Player killer = e.getEntity().getKiller();
                Bukkit.broadcastMessage(BowBash.prefix + gameSession.getGamePlayer(p).getGameTeam().getColorCode() + p.getName() + " §7wurde von " + gameSession.getGamePlayer(killer).getGameTeam().getColorCode() + killer.getName() + " §7getötet.");
                gameSession.getGamePlayer(killer).getGameTeam().addPoint();
                checkForWin(gameSession.getGamePlayer(killer).getGameTeam());

                StatsManager.updateStat(killer.getUniqueId(), StatsType.KILLS);
                StatsManager.updateStat(p.getUniqueId(), StatsType.DEATHS);

            } else if (pvp_cache.containsKey(p) && pvp_cache.get(p) != p) {
                Player killer = pvp_cache.get(p);
                pvp_cache.remove(p);
                Bukkit.broadcastMessage(BowBash.prefix + gameSession.getGamePlayer(p).getGameTeam().getColorCode() + p.getName() + " §7wurde von " + gameSession.getGamePlayer(killer).getGameTeam().getColorCode() + p.getName() + killer.getName() + " §7getötet.");
                gameSession.getGamePlayer(killer).getGameTeam().addPoint();
                checkForWin(gameSession.getGamePlayer(killer).getGameTeam());

                StatsManager.updateStat(killer.getUniqueId(), StatsType.KILLS);
                StatsManager.updateStat(p.getUniqueId(), StatsType.DEATHS);
            } else {
                Bukkit.broadcastMessage(BowBash.prefix + gameSession.getGamePlayer(p).getGameTeam().getColorCode() + p.getName() +  " §7ist gestorben.");
                StatsManager.updateStat(p.getUniqueId(), StatsType.DEATHS);
            }
        }
    }

    private void checkForWin(GameTeam gameTeam) {
        if (!isEndless()) {
            if (gameTeam.getPoints() >= 10) {
                gameSession.setGameState(GameState.ENDING);
            }
        }
    }

    @EventHandler
    public void check(EntityShootBowEvent e) {
        if (e.getEntity().getType() == EntityType.PLAYER && gameSession.isRunning()) {
            Player p = (Player) e.getEntity();
            if (p.getInventory().contains(gameSession.getGamePlayer(p).getGameTeam().getTeamBlock())) {
                p.getInventory().removeItem(new ItemStack(gameSession.getGamePlayer(p).getGameTeam().getTeamBlock(), 1));

            } else {
                e.setCancelled(true);
                p.sendMessage(BowBash.prefix + "§cDu hast keine Munition mehr!");
            }
        } else {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onHit(ProjectileHitEvent e) {
        if (gameSession.isRunning()) {
            if (e.getEntityType() == EntityType.ARROW) {
                Arrow arrow = (Arrow) e.getEntity();
                World world = arrow.getWorld();
                BlockIterator iterator = new BlockIterator(world, arrow.getLocation().toVector(), arrow.getVelocity().normalize(), 0, 4);
                Block hitBlock = null;

                while (iterator.hasNext()) {
                    hitBlock = iterator.next();
                    if (isValidBlock(hitBlock.getY())) {
                        if (isValidTeamBlock(hitBlock) && mapchanges.contains(hitBlock.getLocation())) {
                            hitBlock.setType(Material.AIR);
                            arrow.remove();
                        }
                    }
                }
            }
        } else {
            e.getEntity().remove();
        }
    }

    @EventHandler
    public void b2(BlockBreakEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.SURVIVAL && gameSession.isRunning()) {
            if (isValidTeamBlock(e.getBlock()) && mapchanges.contains(e.getBlock().getLocation())) {
                e.getPlayer().getInventory().addItem(new ItemStack(gameSession.getGamePlayer(e.getPlayer()).getGameTeam().getTeamBlock(), 1));
            } else {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void p1(BlockPlaceEvent e) {
        if (gameSession.isRunning()) {
            StatsManager.updateStat(e.getPlayer().getUniqueId(), StatsType.BLOCKS_PLACED);
            if (e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
                if (isValidTeamBlock(e.getBlock())) {
                    mapchanges.add(e.getBlockPlaced().getLocation());
                } else {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void b1(BlockDamageEvent e) {
        if (gameSession.isRunning() && isValidTeamBlock(e.getBlock()) && e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
            if (!mapchanges.contains(e.getBlock().getLocation())) {
                e.setCancelled(true);
                e.getPlayer().getInventory().addItem(new ItemStack(gameSession.getGamePlayer(e.getPlayer()).getGameTeam().getTeamBlock(), 1));
            }
        }
    }

    private boolean isValidBlock(int y) {
        return y != -1;
    }

    private boolean isValidTeamBlock(Block b) {
        // blocks not hard coded here because of the possibility to change glass to other blocks on different maps
        for (GameTeam gameTeam : gameSession.getGameTeams()) {
            if (b.getType() == gameTeam.getTeamBlock()) {
                return true;
            }
        }
        return false;
    }
}
