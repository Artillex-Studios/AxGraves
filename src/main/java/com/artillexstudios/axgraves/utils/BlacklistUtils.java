package com.artillexstudios.axgraves.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static com.artillexstudios.axgraves.AxGraves.CONFIG;

public class BlacklistUtils {

    public static boolean isBlacklisted(ItemStack it) {
        if (it == null) return false;

        if (CONFIG.getSection("blacklisted-items") == null) return false;
        for (String s : CONFIG.getSection("blacklisted-items").getRoutesAsStrings(false)) {
            boolean banned = false;

            if (CONFIG.getString("blacklisted-items." + s + ".material") != null) {
                final Material mt = Material.getMaterial(CONFIG.getString("blacklisted-items." + s + ".material").toUpperCase());
                if (mt == null) continue;
                if (!it.getType().equals(mt)) continue;
                banned = true;
            }

            if (CONFIG.getString("blacklisted-items." + s + ".name-contains") != null) {
                if (it.getItemMeta() == null) continue;
                if (!it.getItemMeta().getDisplayName().contains(CONFIG.getString("blacklisted-items." + s + ".name-contains"))) continue;
                banned = true;
            }

            if (banned) return true;
        }

        return false;
    }
}
