package eu.proxyservices.bowbash.game.gamestates.ingame;

import eu.proxyservices.bowbash.BowBash;
import eu.proxyservices.bowbash.game.GameSession;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.UUID;

public class KitManager implements Listener {

    private static GameSession gameSession;
    private static final ArrayList<Player> cooldown = new ArrayList<>();

    public KitManager(GameSession gameSession) {
        KitManager.gameSession = gameSession;
    }

    public static GameKit getGameKitByName(String name) {
        for (GameKit gameKit : GameKit.values()) {
            if (gameKit.getName().equalsIgnoreCase(name)) {
                return gameKit;
            }
        }
        return null;
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
                        player.getInventory().addItem(giveRescueKitItem());
                        player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
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
                loc.getBlock().setType(gameSession.getGamePlayer(player).getGameTeam().getGlassBlock());
                GameManager.mapchanges.add(loc.getBlock().getLocation());
            }
        }
    }

    public static void gameItems(Player player) {

        switch (gameSession.getGamePlayer(player).getGameKit()) {
            case Standard -> StandardKit(player);
            case Rescuer -> {
                giveRescueKit(player);
            }
        }
    }

    public static void StandardKit(Player player) {
        ItemStack bow = new ItemStack(Material.BOW);
        bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
        bow.addEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
        bow.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
        ItemMeta im = bow.getItemMeta();
        assert im != null;
        im.setUnbreakable(true);
        bow.setItemMeta(im);
        player.getInventory().setItem(0, bow);
        player.getInventory().setItem(2, new ItemStack(gameSession.getGamePlayer(player).getGameTeam().getGlassBlock(), 16));
        player.getInventory().setItem(9, new ItemStack(Material.ARROW));
        player.getInventory().setArmorContents(createArmor());
    }
    private static ItemStack[] createArmor() {
        ItemStack[] armor = new ItemStack[4];
        armor[0] = new ItemStack(Material.NETHERITE_HELMET);
        armor[1] = new ItemStack(Material.NETHERITE_CHESTPLATE);
        armor[2] = new ItemStack(Material.NETHERITE_LEGGINGS);
        armor[3] = new ItemStack(Material.NETHERITE_BOOTS);
        return armor;
    }

    public static void giveRescueKit(Player player) {
        ItemStack bow = new ItemStack(Material.BOW);
        bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
        bow.addEnchantment(Enchantment.ARROW_KNOCKBACK, 2);
        bow.addEnchantment(Enchantment.ARROW_DAMAGE, 2);
        bow.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
        ItemMeta im = bow.getItemMeta();
        assert im != null;
        im.setUnbreakable(true);
        bow.setItemMeta(im);
        player.getInventory().setItem(0, bow);
        player.getInventory().setItem(2, new ItemStack(gameSession.getGamePlayer(player).getGameTeam().getGlassBlock(), 16));
        player.getInventory().setItem(9, new ItemStack(Material.ARROW));
        player.getInventory().setArmorContents(createArmor());
        if (!cooldown.contains(player)) {
            ItemStack rescue = new ItemStack(Material.BLAZE_ROD, 1);
            ItemMeta rm = rescue.getItemMeta();
            assert rm != null;
            rm.setDisplayName("§6Rettungsplattform");
            rescue.setItemMeta(rm);
            player.getInventory().addItem(rescue);
        } else {
            player.getInventory().addItem(giveRescueKitCooldown());
        }

    }
    public static ItemStack giveRescueKitItem() {
        ItemStack rescue = new ItemStack(Material.BLAZE_ROD, 1);
        ItemMeta rm = rescue.getItemMeta();
        assert rm != null;
        rm.setDisplayName("§6Rettungsplattform");
        rescue.setItemMeta(rm);
        return rescue;
    }
    public static ItemStack giveRescueKitCooldown() {
        ItemStack rescue = new ItemStack(Material.STICK);
        ItemMeta rm = rescue.getItemMeta();
        assert rm != null;
        rm.setDisplayName("§7Rettungsplattform");
        rescue.setItemMeta(rm);
        return rescue;
    }

    public static void giveTankKit(Player player) {
        ItemStack bow = new ItemStack(Material.BOW);
        bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
        bow.addEnchantment(Enchantment.ARROW_KNOCKBACK, 2);
        bow.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
        ItemMeta im = bow.getItemMeta();
        assert im != null;
        im.setUnbreakable(true);
        bow.setItemMeta(im);
        player.getInventory().setItem(0, bow);
        player.getInventory().setItem(2, new ItemStack(gameSession.getGamePlayer(player).getGameTeam().getGlassBlock(), 32));
        player.getInventory().setItem(9, new ItemStack(Material.ARROW));
        player.getInventory().setArmorContents(createCustomArmor());
    }

    public static ItemStack[] createCustomArmor() {
        ItemStack[] armor = new ItemStack[4];
        armor[0] = createCustomItem(Material.NETHERITE_HELMET, EquipmentSlot.HEAD);
        armor[1] = createCustomItem(Material.NETHERITE_CHESTPLATE, EquipmentSlot.CHEST);
        armor[2] = createCustomItem(Material.NETHERITE_LEGGINGS, EquipmentSlot.LEGS);
        armor[3] = createCustomItem(Material.NETHERITE_BOOTS, EquipmentSlot.FEET);

        return armor;
    }

    private static ItemStack createCustomItem(Material material, EquipmentSlot slot) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "generic.knockbackResistance", 0.25, AttributeModifier.Operation.ADD_NUMBER, slot);
            meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, modifier);
            item.setItemMeta(meta);
        }
        return item;
    }

}
