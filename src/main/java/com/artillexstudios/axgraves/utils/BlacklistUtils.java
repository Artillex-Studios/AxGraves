package com.artillexstudios.axgraves.utils;

import com.artillexstudios.axgraves.config.Config;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;


public class BlacklistUtils {

    public static boolean isBlacklisted(ItemStack it) {
        if (it == null) return false;

        if (Config.blacklistedItems == null) return false;
        for (Map.Entry<String, Map<String, String>> s : Config.blacklistedItems.entrySet()) {
            boolean banned = false;

            if (s.getValue().get(".material") != null) {
                final Material mt = Material.getMaterial(s.getValue().get(".material").toUpperCase());
                if (mt == null) continue;
                if (!it.getType().equals(mt)) continue;
                banned = true;
            }

            if (s.getValue().get(".name-contains") != null) {
                if (it.getItemMeta() == null) continue;
                if (!it.getItemMeta().getDisplayName().contains(s.getValue().get(".name-contains"))) continue;
                banned = true;
            }

            if (banned) return true;
        }

        return false;
    }
}
