package com.artillexstudios.axgraves.gui;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axgraves.grave.Grave;
import com.artillexstudios.axgraves.grave.SpawnedGraves;
import com.artillexstudios.axgraves.utils.LocationUtils;
import com.artillexstudios.axgraves.utils.VaultHook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.artillexstudios.axgraves.AxGraves.CONFIG;
import static com.artillexstudios.axgraves.AxGraves.LANG;

public class GravesGUI {
    private static final Set<UUID> openGUIs = Collections.synchronizedSet(new HashSet<>());
    private static final Map<UUID, Map<Integer, Location>> slotMap = Collections.synchronizedMap(new HashMap<>());

    public static void open(Player player) {
        List<Grave> playerGraves = new ArrayList<>();
        for (Grave grave : SpawnedGraves.getGraves()) {
            if (grave.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                playerGraves.add(grave);
            }
        }

        int size = playerGraves.isEmpty() ? 9 : Math.min(54, (int) Math.ceil(playerGraves.size() / 9.0) * 9);
        String title = StringUtils.formatToString(LANG.getString("gui.title", "&0Your Graves"));
        Inventory inv = Bukkit.createInventory(null, size, title);

        Map<Integer, Location> slots = new HashMap<>();

        if (playerGraves.isEmpty()) {
            ItemStack noGraves = new ItemStack(Material.BARRIER);
            ItemMeta meta = noGraves.getItemMeta();
            meta.setDisplayName(StringUtils.formatToString(LANG.getString("gui.no-graves-name", "&cNo active graves")));
            List<String> lore = new ArrayList<>();
            for (String line : LANG.getStringList("gui.no-graves-lore")) {
                lore.add(StringUtils.formatToString(line));
            }
            meta.setLore(lore);
            noGraves.setItemMeta(meta);
            inv.setItem(4, noGraves);
        } else {
            boolean costEnabled = CONFIG.getBoolean("teleport-cost.enabled", false)
                    && VaultHook.isEnabled()
                    && !player.hasPermission("axgraves.tp.free");
            double cost = CONFIG.getDouble("teleport-cost.amount", 100.0);
            String costStr = costEnabled
                    ? LANG.getString("gui.cost-format", "$%amount%").replace("%amount%", String.format("%.2f", cost))
                    : LANG.getString("gui.cost-free", "Free");

            for (int i = 0; i < playerGraves.size(); i++) {
                Grave grave = playerGraves.get(i);
                Location loc = grave.getLocation();

                String timeStr;
                if (grave.getDespawnSeconds() == -1) {
                    timeStr = LANG.getString("gui.time-never", "Never");
                } else {
                    long remaining = grave.getDespawnSeconds() * 1_000L - (System.currentTimeMillis() - grave.getSpawned());
                    timeStr = StringUtils.formatTime(Math.max(0, remaining));
                }

                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("%number%", String.valueOf(i + 1));
                placeholders.put("%world%", LocationUtils.getWorldName(loc.getWorld()));
                placeholders.put("%x%", String.valueOf(loc.getBlockX()));
                placeholders.put("%y%", String.valueOf(loc.getBlockY()));
                placeholders.put("%z%", String.valueOf(loc.getBlockZ()));
                placeholders.put("%time%", timeStr);
                placeholders.put("%cost%", costStr);

                ItemStack item = new ItemStack(Material.COMPASS);
                ItemMeta meta = item.getItemMeta();

                String name = LANG.getString("gui.grave-item-name", "&dGrave #%number%");
                for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                    name = name.replace(entry.getKey(), entry.getValue());
                }
                meta.setDisplayName(StringUtils.formatToString(name));

                List<String> lore = new ArrayList<>();
                for (String line : LANG.getStringList("gui.grave-item-lore")) {
                    String formatted = line;
                    for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                        formatted = formatted.replace(entry.getKey(), entry.getValue());
                    }
                    lore.add(StringUtils.formatToString(formatted));
                }
                meta.setLore(lore);
                item.setItemMeta(meta);

                inv.setItem(i, item);
                slots.put(i, loc);
            }
        }

        slotMap.put(player.getUniqueId(), slots);
        openGUIs.add(player.getUniqueId());
        player.openInventory(inv);
    }

    public static boolean isGravesGUI(UUID uuid) {
        return openGUIs.contains(uuid);
    }

    public static Location getGraveLocation(UUID uuid, int slot) {
        Map<Integer, Location> slots = slotMap.get(uuid);
        if (slots == null) return null;
        return slots.get(slot);
    }

    public static void close(UUID uuid) {
        openGUIs.remove(uuid);
        slotMap.remove(uuid);
    }
}
