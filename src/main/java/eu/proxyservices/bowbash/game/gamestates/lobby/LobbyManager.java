package eu.proxyservices.bowbash.game.gamestates.lobby;

import eu.proxyservices.bowbash.BowBash;
import eu.proxyservices.bowbash.game.GameSession;
import eu.proxyservices.bowbash.game.GameState;
import eu.proxyservices.bowbash.game.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class LobbyManager implements Listener {

    private GameSession gameSession;

    public LobbyManager(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    public static void items(Player player) {
        player.getInventory().clear();
        ItemStack kit = new ItemStack(Material.CHEST);
        ItemMeta km = kit.getItemMeta();
        km.setDisplayName("§dKit auswählen");
        kit.setItemMeta(km);
        player.getInventory().setItem(0, kit);
        ItemStack nether = new ItemStack(Material.NETHER_STAR);
        ItemMeta nm = nether.getItemMeta();
        nm.setDisplayName("§9Team auswählen");
        nether.setItemMeta(nm);
        player.getInventory().setItem(1, nether);
        ItemStack leave = new ItemStack(Material.FIREWORK_CHARGE);
        ItemMeta lm = leave.getItemMeta();
        lm.setDisplayName("§cSpiel verlassen");
        leave.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        leave.setItemMeta(lm);
        player.getInventory().setItem(8, leave);
        if (player.hasPermission("bb.vip")) {
            ItemStack start = new ItemStack(Material.GOLD_NUGGET);
            ItemMeta sm = start.getItemMeta();
            sm.setDisplayName("§aStarten");
            start.setItemMeta(sm);
            player.getInventory().setItem(4, start);

            ItemStack map = new ItemStack(Material.EMPTY_MAP);
            ItemMeta im = map.getItemMeta();
            im.setDisplayName("§eMap auswählen");
            map.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
            map.setItemMeta(im);
            player.getInventory().setItem(5, map);
        }
    }

    @EventHandler
    public void cancel(InventoryMoveItemEvent e) {
        if (!gameSession.isRunning()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void i(PlayerInteractEvent e) {
        if (!gameSession.isRunning() && e.getItem() != null && (e.getAction() == Action.RIGHT_CLICK_BLOCK | e.getAction() == Action.RIGHT_CLICK_AIR)) {
            e.setCancelled(true);
            if (e.getItem().getType() == Material.NETHER_STAR && gameSession.getCurrentGameState().equals(GameState.LOBBY)) {
                e.getPlayer().openInventory(teamInv());
            } else if (e.getItem().getType() == Material.FIREWORK_CHARGE && gameSession.getCurrentGameState().equals(GameState.LOBBY)) {
                e.getPlayer().kickPlayer("");
            } else if (e.getItem().getType() == Material.CHEST && gameSession.getCurrentGameState().equals(GameState.LOBBY)) {
                e.getPlayer().openInventory(kitInv(e.getPlayer()));
            } else if (e.getItem().getType() == Material.EMPTY_MAP && gameSession.getCurrentGameState().equals(GameState.LOBBY)) {
                e.getPlayer().openInventory(mapInv());
                e.setCancelled(true);
            } else if (e.getItem().getType() == Material.GOLD_NUGGET && gameSession.getCurrentGameState().equals(GameState.LOBBY)) {
                if (gameSession.getCountdown().fast_start()) {
                    e.getPlayer().sendMessage(BowBash.prefix + "§aDu hast das Spiel gestartet!");
                } else {
                    e.getPlayer().sendMessage(BowBash.prefix + "§cEs sind zu wenig Spieler in der Runde!");
                }
            }
        }
    }

    @EventHandler
    public void click(InventoryClickEvent e) {
        if (gameSession.getCurrentGameState() == GameState.LOBBY) {
            e.setCancelled(true);
            if (e.getClickedInventory().getName().equals("§6Teamauswahl")) {
                if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§9Team Blau")) {
                    if (gameSession.getGamePlayer((Player) e.getWhoClicked()) != null) {
                        gameSession.joinGameTeam(gameSession.getGamePlayer((Player) e.getWhoClicked()), GameTeam.BLUE);
                        e.getWhoClicked().closeInventory();
                    }
                } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§cTeam Rot")) {
                    if (gameSession.getGamePlayer((Player) e.getWhoClicked()) != null) {
                        gameSession.joinGameTeam(gameSession.getGamePlayer((Player) e.getWhoClicked()), GameTeam.RED);
                        e.getWhoClicked().closeInventory();
                    }
                }
            }
        }
    }

    @EventHandler
    public void b2(BlockBreakEvent e) {
        if (!gameSession.isRunning() && e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void b2(BlockPlaceEvent e) {
        if (!gameSession.isRunning() && e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            e.setCancelled(true);
        }
    }


    public static org.bukkit.inventory.Inventory teamInv() {
        org.bukkit.inventory.Inventory inv = Bukkit.createInventory(null, InventoryType.HOPPER, "§6Teamauswahl");
        ItemStack s1 = new ItemStack(Material.WOOL, 1, GameTeam.BLUE.getTeamDurabId());
        ItemMeta m1 = s1.getItemMeta();
        m1.setDisplayName("§9Team Blau");
        s1.setItemMeta(m1);
        ItemStack s2 = new ItemStack(Material.WOOL, 1, GameTeam.RED.getTeamDurabId());
        ItemMeta m2 = s2.getItemMeta();
        m2.setDisplayName("§cTeam Rot");
        s2.setItemMeta(m2);
        inv.setItem(1, s1);
        inv.setItem(3, s2);
        return inv;
    }

    public static org.bukkit.inventory.Inventory kitInv(Player player) {
        org.bukkit.inventory.Inventory inv = Bukkit.createInventory(null, InventoryType.CHEST, "§dKitauswahl");
        ItemStack s1 = new ItemStack(Material.COBBLESTONE, 1);
        ItemMeta m1 = s1.getItemMeta();
        m1.setDisplayName("§7Standard Kit");
        ArrayList<String> l1 = new ArrayList<>();
        l1.add("§7Enthält:");
        l1.add("§8- §eGlas x16");
        l1.add("§8- §eStandard Bogen");
        l1.add("§8- §eEisenrüstung");
        m1.setLore(l1);
        s1.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        s1.setItemMeta(m1);
        inv.setItem(0, s1);
        ItemStack s2 = new ItemStack(Material.BLAZE_ROD, 1);
        ItemMeta m2 = s2.getItemMeta();
        m2.setDisplayName("§cRettungsplattform Kit §a(ausgewählt)");
        ArrayList<String> l2 = new ArrayList<>();
        l2.add("§7Enthält:");
        l2.add("§8- §eGlas x16");
        l2.add("§8- §eeStandard Bogen");
        l2.add("§8- §cRettungsplattform");
        l2.add("§8  §cAlle 30 Sekunden Einsetzbar.");
        l2.add("§8- §eEisenrüstung");
        m2.setLore(l2);
        s2.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        s2.setItemMeta(m2);
        inv.setItem(1, s2);

        return inv;
    }

    public static org.bukkit.inventory.Inventory mapInv() {
        org.bukkit.inventory.Inventory inv = Bukkit.createInventory(null, InventoryType.CHEST, "§dMapauswahl");
        ItemStack s1 = new ItemStack(Material.STAINED_GLASS, 1);
        ItemMeta m1 = s1.getItemMeta();
        m1.setDisplayName("§7Standard Map");
        s1.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        s1.setItemMeta(m1);
        inv.setItem(0, s1);
        return inv;
    }

}
