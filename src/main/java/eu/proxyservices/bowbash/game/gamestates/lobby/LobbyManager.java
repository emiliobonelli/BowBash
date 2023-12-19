package eu.proxyservices.bowbash.game.gamestates.lobby;

import eu.proxyservices.bowbash.BowBash;
import eu.proxyservices.bowbash.game.*;
import eu.proxyservices.bowbash.game.gamestates.ingame.GameKit;
import eu.proxyservices.bowbash.game.gamestates.ingame.KitManager;
import eu.proxyservices.bowbash.game.listener.GameDesignListener;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bukkit.Material.ENDER_EYE;
import static org.bukkit.Material.ENDER_PEARL;

public class LobbyManager implements Listener {
    //todo: split into multiple classes
    private final GameSession gameSession;

    private final MapVoteManager mapVoteManager;

    public LobbyManager(GameSession gameSession) {
        BowBash.plugin.getServer().getPluginManager().registerEvents(this, BowBash.plugin);
        this.gameSession = gameSession;
        this.mapVoteManager = new MapVoteManager(gameSession);
    }

    public void prepare() {
      //  mapVoteManager.countVotes();
      //  mapVoteManager.getCurrentMap().loadLocations();
        GameDesignListener.setGamePreferences(mapVoteManager.getCurrentMap().getSpectatorSpawn().getWorld());
    }

    public void items(Player player) {
        player.getInventory().clear();
        player.getInventory().setChestplate(new ItemStack(Material.ELYTRA));
        ItemStack kit = new ItemStack(Material.LIGHT_GRAY_SHULKER_BOX);
        ItemMeta km = kit.getItemMeta();
        km.setDisplayName("§dKit auswählen");
        kit.setItemMeta(km);
        player.getInventory().setItem(1, kit);
        ItemStack nether = new ItemStack((gameSession.getGamePlayer(player).getGameTeam() != null ? gameSession.getGamePlayer(player).getGameTeam().getTeamBanner() : Material.WHITE_BANNER), 1);
        ItemMeta nm = nether.getItemMeta();
        nm.setDisplayName("§eTeam auswählen");
        nether.setItemMeta(nm);
        player.getInventory().setItem(2, nether);
        ItemStack leave = new ItemStack(Material.RED_DYE);
        ItemMeta lm = leave.getItemMeta();
        lm.setDisplayName("§cSpiel verlassen");
        leave.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        leave.setItemMeta(lm);
        player.getInventory().setItem(8, leave);
        if (player.hasPermission("bb.vip")) {
            if (gameSession.getGameManager().isEndless()) {
                ItemStack endless = new ItemStack(ENDER_EYE);
                ItemMeta em = endless.getItemMeta();
                em.setDisplayName("§d∞-Modus: §aaktiviert");
                endless.setItemMeta(em);
                player.getInventory().setItem(5, endless);
            } else {
                ItemStack endless = new ItemStack(ENDER_PEARL);
                ItemMeta em = endless.getItemMeta();
                em.setDisplayName("§d∞-Modus: §cdeaktiviert");
                endless.setItemMeta(em);
                player.getInventory().setItem(5, endless);
            }

            ItemStack start = new ItemStack(Material.EMERALD);
            ItemMeta sm = start.getItemMeta();
            sm.setDisplayName("§aSpiel starten");
            start.setItemMeta(sm);
            player.getInventory().setItem(4, start);

            ItemStack map = new ItemStack(Material.MAP);
            ItemMeta im = map.getItemMeta();
            im.setDisplayName("§eMap auswählen");
            map.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
            map.setItemMeta(im);
            player.getInventory().setItem(6, map);
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
        if (gameSession.getCurrentGameState().equals(GameState.LOBBY) && e.getItem() != null && (e.getAction() == Action.RIGHT_CLICK_BLOCK | e.getAction() == Action.RIGHT_CLICK_AIR)) {
            e.setCancelled(true);
            Player p = e.getPlayer();
            switch (e.getItem().getType()) {
                case WHITE_BANNER, RED_BANNER, BLUE_BANNER, YELLOW_BANNER, LIME_BANNER  -> e.getPlayer().openInventory(teamInv());
                case RED_DYE -> e.getPlayer().kickPlayer("");
                case LIGHT_GRAY_SHULKER_BOX -> e.getPlayer().openInventory(kitInv(gameSession.getGamePlayer(p)));
                case MAP -> e.getPlayer().openInventory(mapInv());
                case EMERALD -> {
                    if (gameSession.getCountdown().fast_start()) {
                        e.getPlayer().sendMessage(BowBash.prefix + "§aDu hast das Spiel gestartet!");
                    } else {
                        e.getPlayer().sendMessage(BowBash.prefix + "§cEs sind zu wenig Spieler in der Runde!");
                    }
                }
                case ENDER_EYE -> {
                    if (gameSession.getGameManager().isEndless()) {
                        gameSession.getGameManager().setEndless(false);
                        p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                        for (Player p1 : Bukkit.getOnlinePlayers()) {
                            items(p);
                            p1.sendMessage(BowBash.prefix + "§d∞-Modus: §cdeaktiviert");
                            p1.sendMessage(BowBash.prefix + "§cDie Runde endet nun, nachdem ein Team durch Punkte gewonnen hat.");
                        }
                    }
                }
                case ENDER_PEARL -> {
                    if (!gameSession.getGameManager().isEndless()) {
                        gameSession.getGameManager().setEndless(true);
                        p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                        for (Player p1 : Bukkit.getOnlinePlayers()) {
                            items(p);
                            p1.sendMessage(BowBash.prefix + "§d∞-Modus: §aaktiviert");
                            p1.sendMessage(BowBash.prefix + "§cDie Runde hat ab sofort kein festes Ende und endet erst, nachdem nur noch ein Team übrig ist.");
                        }
                    }
                }
            }
        }
    }

    // info: only for 2x1 teams supported
    // todo: change
    @EventHandler
    public void click(InventoryClickEvent e) {
        if (gameSession.getCurrentGameState() == GameState.LOBBY) {
            e.setCancelled(true);
            if (e.getView().getTitle().equals("§6Teamauswahl") && e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
                switch (e.getCurrentItem().getItemMeta().getDisplayName()) {
                    case "§9Team Blau" -> {
                        if (gameSession.getGamePlayer((Player) e.getWhoClicked()) != null) {
                            gameSession.getGameManager().joinGameTeam(gameSession.getGamePlayer((Player) e.getWhoClicked()), GameTeam.BLUE);
                            e.getWhoClicked().closeInventory();
                        }
                    }
                    case "§cTeam Rot" -> {
                        if (gameSession.getGamePlayer((Player) e.getWhoClicked()) != null) {
                            gameSession.getGameManager().joinGameTeam(gameSession.getGamePlayer((Player) e.getWhoClicked()), GameTeam.RED);
                            e.getWhoClicked().closeInventory();
                        }
                    }
                    case "§eTeam Gelb" -> {
                        if (gameSession.getGamePlayer((Player) e.getWhoClicked()) != null) {
                            gameSession.getGameManager().joinGameTeam(gameSession.getGamePlayer((Player) e.getWhoClicked()), GameTeam.YELLOW);
                            e.getWhoClicked().closeInventory();
                        }
                    }
                    case "§aTeam Grün" -> {
                        if (gameSession.getGamePlayer((Player) e.getWhoClicked()) != null) {
                            gameSession.getGameManager().joinGameTeam(gameSession.getGamePlayer((Player) e.getWhoClicked()), GameTeam.GREEN);
                            e.getWhoClicked().closeInventory();
                        }
                    }
                }
                items((Player) e.getWhoClicked());
            } else if (e.getView().getTitle().equals("§dKitauswahl")) {
                if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
                    // no need to replace the color code, because it's the same as in the enum
                    gameSession.getGamePlayer((Player) e.getWhoClicked()).setGameKit(KitManager.getGameKitByName(e.getCurrentItem().getItemMeta().getDisplayName().replace(" §7(§aausgewählt§7)", "")));
                    e.getWhoClicked().closeInventory();
                    e.getWhoClicked().sendMessage(BowBash.prefix + "§aDu hast das Kit §e" + e.getCurrentItem().getItemMeta().getDisplayName().replace(" §7(§aausgewählt§7)", "") + " §aausgewählt.");
                    ((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                }
            } else if (e.getView().getTitle().equals("§eMapauswahl")) {
                if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
                    // name in enum is without color code
                    String mapName = e.getCurrentItem().getItemMeta().getDisplayName().replace("§e", "").replace(" §7(§aausgewählt§7)", "");
                    if (mapVoteManager.getCurrentMap() != mapVoteManager.getGameMapByName(mapName)) {
                        mapVoteManager.forceMap(mapName);
                        e.getWhoClicked().sendMessage(BowBash.prefix + "§aDu hast die Map §e" + e.getCurrentItem().getItemMeta().getDisplayName().replace("§e", "").replace(" §7(§aausgewählt§7)", "") + " §aausgewählt.");
                        ((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    } else {
                        e.getWhoClicked().sendMessage(BowBash.prefix + "§cDie Map wurde bereits gewählt.");
                    }
                    e.getWhoClicked().closeInventory();
                }
            }
        }
    }
    /*
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
    */

    private Inventory teamInv() {
        Inventory inv = Bukkit.createInventory(null, InventoryType.HOPPER, "§6Teamauswahl");
        for (GameTeam team : gameSession.getGameTeams()) {
            ItemStack s1 = new ItemStack(team.getTeamBlock(), 1);
            ItemMeta m1 = s1.getItemMeta();
            assert m1 != null;
            m1.setDisplayName(team.getColorCode() + "Team " + team.getName());
            ArrayList<String> l1 = new ArrayList<>();
            l1.add("§7Klicke um dem Team beizutreten.");
            m1.setLore(l1);
            s1.setItemMeta(m1);
            inv.addItem(s1);
        }
        return inv;
    }

    private Inventory kitInv(GamePlayer gamePlayer) {
        Inventory inv = Bukkit.createInventory(null, InventoryType.CHEST, "§dKitauswahl");
        for (GameKit gameKit : GameKit.values()) {
            ItemStack s1 = new ItemStack(gameKit.getIcon(), 1);
            ItemMeta m1 = s1.getItemMeta();
            assert m1 != null;

            if (gamePlayer.getPlayer().hasPermission(gameKit.getPermission())) {
                m1.setDisplayName(gameKit.getName());
            } else {
                m1.setDisplayName(gameKit.getName() + " §7(§cLOCKED§7)");
            }
            m1.setLore(Arrays.asList(gameKit.getDescription()));
            s1.setItemMeta(m1);

            if (gamePlayer.getGameKit() == gameKit) {
                m1.setDisplayName(gameKit.getName() + " §7(§aausgewählt§7)");
                s1.addUnsafeEnchantment(Enchantment.LUCK, 1);

            }
            s1.setItemMeta(m1);
            inv.addItem(s1);
        }
        return inv;
    }

    private Inventory mapInv() {
        Inventory inv = Bukkit.createInventory(null, InventoryType.CHEST, "§eMapauswahl");
        for (GameMap gameMap : mapVoteManager.getGameMaps()) {

            ItemStack s1 = new ItemStack(gameMap.getMapItem(), 1);
            ItemMeta m1 = s1.getItemMeta();
            assert m1 != null;
            m1.setLore(List.of("§7von §e" + gameMap.getMapAuthor()));
            if (gameMap == mapVoteManager.getCurrentMap()) {
                m1.setDisplayName("§e" + gameMap.getMapName() + " §7(§aausgewählt§7)");
                s1.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
            }
            s1.setItemMeta(m1);

            inv.addItem(s1);
        }
        return inv;
    }
}
