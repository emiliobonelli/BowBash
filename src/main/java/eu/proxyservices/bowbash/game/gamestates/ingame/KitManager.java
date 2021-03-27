package eu.proxyservices.bowbash.game.gamestates.ingame;

import eu.proxyservices.bowbash.BowBash;
import eu.proxyservices.bowbash.game.GameSession;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class KitManager implements Listener {

    private static GameSession gameSession;
    private static ArrayList<Player> cooldown = new ArrayList<Player>();

    public KitManager(GameSession gameSession) {
        KitManager.gameSession = gameSession;
    }

    @EventHandler
    public void i1(PlayerInteractEvent e) {
        if (gameSession.isRunning() && e.getItem() != null) {
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                if (e.getItem().getType() == Material.BLAZE_ROD) {
                    rescueKit(e.getPlayer());
                }
            }
        }
    }

    public void rescueKit(Player player) {
        if (!cooldown.contains(player)) {
            player.getInventory().remove(Material.BLAZE_ROD);
            player.getInventory().addItem(giveRescueKitCooldown());
            cooldown.add(player);
            placeRescuePlatform(player);
            new BukkitRunnable() {
                int currentTime = 30;

                @Override
                public void run() {
                    currentTime--;
                    if (currentTime == 0) {
                        player.getInventory().removeItem(giveRescueKitCooldown());
                        player.getInventory().addItem(giveRescueKit());
                        player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                        cooldown.remove(player);
                        cancel();
                    } else {
                        for (ItemStack stack : player.getInventory().getContents()) {
                            if (stack.getType() == Material.STICK) {
                                stack.setAmount(currentTime);
                                break;
                            }
                        }
                    }
                }
            }.runTaskTimer(BowBash.plugin, 0L, 20L);
        } else {
            player.getInventory().remove(Material.BLAZE_ROD);
            player.getInventory().addItem(giveRescueKitCooldown());
        }
    }

    public static void placeRescuePlatform(Player player) {
        ArrayList<Location> locs = new ArrayList<>();
        final Location loc0 = player.getLocation().add(0.0D, -1, 0.0D);
        final Location loc1a = player.getLocation().add(1.0D, -1, 0.0D);
        final Location loc2a = player.getLocation().add(0.0D, -1, 1.0D);
        final Location loc3a = player.getLocation().add(1.0D, -1, 1.0D);
        final Location loc4a = player.getLocation().add(-1.0D, -1, 0.0D);
        final Location loc5a = player.getLocation().add(0.0D, -1, -1.0D);
        final Location loc6 = player.getLocation().add(-1.0D, -1, -1.0D);
        final Location loc7 = player.getLocation().add(-1.0D, -1, 1.0D);
        final Location loc8 = player.getLocation().add(1.0D, -1, -1.0D);
        locs.add(loc0);
        locs.add(loc1a);
        locs.add(loc2a);
        locs.add(loc3a);
        locs.add(loc4a);
        locs.add(loc5a);
        locs.add(loc6);
        locs.add(loc7);
        locs.add(loc8);

        for (Location loc : locs) {
            if (loc.getBlock().getType() == Material.AIR) {
                loc.getBlock().setType(Material.STAINED_GLASS);
                loc.getBlock().setData(gameSession.getGamePlayer(player).getGameTeam().getTeamDurabId());
                GameManager.mapchanges.add(loc.getBlock().getLocation());
            }
        }
    }

    public static void gameItems(Player player) {
        final GameKit kit = gameSession.getGamePlayer(player).getGameKit();
        //  if (gameSession.getGamePlayer(player).getGameKit() == null) {
        //     gameSession.getGamePlayer(player).setGameKit(GameKit.RESCUE);
        StandardKit(player);
        if (!cooldown.contains(player)) {
            player.getInventory().addItem(giveRescueKit());
        } else {
            player.getInventory().addItem(giveRescueKitCooldown());
        }
        //   }
    }

    public static void StandardKit(Player player) {
        ItemStack bow = new ItemStack(Material.BOW);
        bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
        bow.addEnchantment(Enchantment.ARROW_KNOCKBACK, 2);
        bow.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
        ItemMeta im = bow.getItemMeta();
        im.spigot().setUnbreakable(true);
        bow.setItemMeta(im);
        player.getInventory().setItem(0, bow);
        player.getInventory().setItem(2, new ItemStack(Material.STAINED_GLASS, 16, gameSession.getGamePlayer(player).getGameTeam().getTeamDurabId()));
        player.getInventory().setItem(9, new ItemStack(Material.ARROW));

        ItemStack i1 = new ItemStack(Material.IRON_BOOTS);
        ItemMeta m1 = i1.getItemMeta();
        m1.spigot().setUnbreakable(true);
        i1.setItemMeta(m1);
        player.getInventory().setBoots(i1);
        ItemStack i2 = new ItemStack(Material.IRON_LEGGINGS);
        ItemMeta m2 = i2.getItemMeta();
        m2.spigot().setUnbreakable(true);
        i2.setItemMeta(m2);
        player.getInventory().setLeggings(i2);
        ItemStack i3 = new ItemStack(Material.IRON_CHESTPLATE);
        ItemMeta m3 = i3.getItemMeta();
        m3.spigot().setUnbreakable(true);
        i3.setItemMeta(m3);
        player.getInventory().setChestplate(i3);
        ItemStack i4 = new ItemStack(Material.IRON_HELMET);
        ItemMeta m4 = i4.getItemMeta();
        m4.spigot().setUnbreakable(true);
        i4.setItemMeta(m4);
        player.getInventory().setHelmet(i4);
    }

    public static ItemStack giveRescueKit() {
        ItemStack rescue = new ItemStack(Material.BLAZE_ROD);
        rescue.setAmount(1);
        ItemMeta rm = rescue.getItemMeta();
        rm.setDisplayName("§6Rettungsplattform");
        rescue.setItemMeta(rm);
        return rescue;
    }

    public static ItemStack giveRescueKitCooldown() {
        ItemStack rescue = new ItemStack(Material.STICK);
        ItemMeta rm = rescue.getItemMeta();
        rm.setDisplayName("§7Rettungsplattform");
        rescue.setItemMeta(rm);
        return rescue;
    }

}
