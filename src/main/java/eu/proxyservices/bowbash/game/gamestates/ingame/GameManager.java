package eu.proxyservices.bowbash.game.gamestates.ingame;

import eu.proxyservices.bowbash.BowBash;
import eu.proxyservices.bowbash.game.GameSession;
import eu.proxyservices.bowbash.game.GameState;
import eu.proxyservices.bowbash.game.GameTeam;
import eu.proxyservices.bowbash.game.data.StatsManager;
import eu.proxyservices.bowbash.game.data.StatsType;
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
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
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
        this.gameSession = gameSession;
    }

    private Scoreboard Scoreboard() {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("aaa", "bbb");

        obj.setDisplayName("§bBowBash");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.getScore("§1").setScore(8);
        obj.getScore("§9Team Blau:").setScore(7);
        obj.getScore("§b" + GameTeam.BLUE.getPoints() + " Punkte").setScore(6);
        obj.getScore("§2").setScore(5);
        obj.getScore("§cTeam Rot:").setScore(4);
        obj.getScore("§b" + GameTeam.RED.getPoints() + " Punkte").setScore(3);
        obj.getScore("§3").setScore(2);
        return board;
    }

    public void runScoreboard() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(BowBash.plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.setScoreboard(Scoreboard());
            }
        }, 20L, 20L);
    }

    @EventHandler
    public void respawn(PlayerRespawnEvent e) {
        if (gameSession.isRunning() && gameSession.getGamePlayer(e.getPlayer()) != null) {
            e.setRespawnLocation(gameSession.getGamePlayer(e.getPlayer()).getGameTeam().getSpawnLocation());
            e.getPlayer().getInventory().clear();
            KitManager.gameItems(e.getPlayer());
            /**
             new BukkitRunnable() {

            @Override public void run() {
            e.getPlayer().setNoDamageTicks(1);
            }
            }.runTaskTimer(BowBash.plugin, 1L, 60L);
             */
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
                if (gameSession.getGamePlayer(p) != null && gameSession.getGamePlayer(damager) != null) {
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
            if (e.getEntity().getKiller() != null) {
                pvp_cache.remove(p);
                Player killer = e.getEntity().getKiller();
                Bukkit.broadcastMessage(BowBash.prefix + "§e" + p.getName() + " §7wurde von §c" + killer.getName() + " §7getötet.");
                gameSession.getGamePlayer(killer).getGameTeam().addPoint();
                StatsManager.updateStat(killer.getUniqueId(), StatsType.KILLS);
                StatsManager.updateStat(p.getUniqueId(), StatsType.DEATHS);

            } else if (pvp_cache.containsKey(p)) {
                Player killer = pvp_cache.get(p);
                pvp_cache.remove(p);
                Bukkit.broadcastMessage(BowBash.prefix + "§e" + p.getName() + " §7wurde von §c" + killer.getName() + " §7getötet.");
                gameSession.getGamePlayer(killer).getGameTeam().addPoint();
                StatsManager.updateStat(killer.getUniqueId(), StatsType.KILLS);
                StatsManager.updateStat(p.getUniqueId(), StatsType.DEATHS);
            } else {
                Bukkit.broadcastMessage(BowBash.prefix + "§c" + p.getName() + " §7ist gestorben.");
                StatsManager.updateStat(p.getUniqueId(), StatsType.DEATHS);
            }
            /**
             * For new point system
             * Start -> points = 5
             * Kill -> points++
             * death -> points--
             * todo: this (New point system)
             */
            if (!isEndless()) {
                GameTeam finalTeam = null;
                for (GameTeam gameTeam : GameTeam.values()) {
                    if (gameTeam.getPoints() == 10) {
                        finalTeam = gameTeam;
                    }
                }
                if (finalTeam != null) {
                    gameSession.setGameState(GameState.ENDING);
                }
            }
        }
    }

    @EventHandler
    public void check(EntityShootBowEvent e) {
        if (e.getEntity().getType() == EntityType.PLAYER && gameSession.isRunning()) {
            Player p = (Player) e.getEntity();
            if (p.getInventory().contains(gameSession.getGamePlayer(p).getGameTeam().getGlassBlock())) {
                p.getInventory().removeItem(new ItemStack(gameSession.getGamePlayer(p).getGameTeam().getGlassBlock(), 1));

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
        if (gameSession.isRunning() && e.getEntity() instanceof Player) {
            if (e.getEntityType() == EntityType.ARROW) {
                Arrow arrow = (Arrow) e.getEntity();
                World world = arrow.getWorld();
                BlockIterator iterator = new BlockIterator(world, arrow.getLocation().toVector(), arrow.getVelocity().normalize(), 0, 4);
                Block hitBlock = null;

                while (iterator.hasNext()) {
                    hitBlock = iterator.next();
                    if (isValidBlock(hitBlock.getY())) {
                        arrow.remove();
                        if (hitBlock.getType() == Material.matchMaterial("STAINED_GLASS")) {
                            Location loc = new Location(hitBlock.getWorld(), hitBlock.getX(), hitBlock.getY(), hitBlock.getZ());
                            if (mapchanges.contains(loc)) {
                                hitBlock.setType(Material.AIR);
                            }
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
            if (e.getBlock().getType() == Material.matchMaterial("STAINED_GLASS") && mapchanges.contains(e.getBlock().getLocation())) {
                e.getPlayer().getInventory().addItem(new ItemStack(gameSession.getGamePlayer(e.getPlayer()).getGameTeam().getGlassBlock(), 1));
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
                if (e.getBlock().getType() == Material.matchMaterial("STAINED_GLASS")) {
                    mapchanges.add(e.getBlockPlaced().getLocation());
                } else {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void b1(BlockDamageEvent e) {
        if (gameSession.isRunning() && e.getBlock().getType() == Material.matchMaterial("STAINED_GLASS") && e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
            if (!mapchanges.contains(e.getBlock().getLocation())) {
                e.setCancelled(true);
                e.getPlayer().getInventory().addItem(new ItemStack(gameSession.getGamePlayer(e.getPlayer()).getGameTeam().getGlassBlock(), 1));
            }
        }
    }

    private boolean isValidBlock(int y) {
        return y != -1;
    }
}
