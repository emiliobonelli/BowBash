package eu.proxyservices.bowbash.game.gamestates.ingame;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum GameKit {

    Standard("§7Standard", "", Material.BOW, new String[]{"§7Eisenrüstung", "§7Einfacher Bogen"}, new ItemStack[] {}, new ItemStack[] {}),
    Rescuer("§eRescuer", "bb.kit.rescuer", Material.BLAZE_ROD, new String[]{"§7Punch-II Bogen", "§eRettungsplattform", "§716x Blöcke", "§7Eisenrüstung"}, new ItemStack[] {}, new ItemStack[] {}),
    Tank("§dTank", "bb.kit.tank", Material.DIAMOND_CHESTPLATE, new String[]{"§7Verstärkter Bogen", "§bNetherite Rüstung §7mit §b35% §7weniger Rückschlag", "32x Blöcke" }, new ItemStack[] {}, new ItemStack[] {});

    private final String name;
    private final String permission;
    private final ItemStack[] items;
    private final ItemStack[] armor;
    private final Material icon;
    private final String[] description;

    GameKit(String name, String permission, Material icon, String[] description, ItemStack[] items, ItemStack[] armor) {
        this.name = name;
        this.permission = permission;
        this.icon = icon;
        this.description = description;
        this.items = items;
        this.armor = armor;

    }

    public String getName() {
        return name;
    }
    public ItemStack[] getItems() {
        return items;
    }
    public ItemStack[] getArmor() {
        return armor;
    }
    public Material getIcon() {
        return icon;
    }
    public String[] getDescription() {
        return description;
    }
    public String getPermission() {
        return permission;
    }

}
