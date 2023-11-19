package com.artillexstudios.axgraves.utils;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

public class Utils {

    @NotNull
    public static ItemStack getPlayerHead(@NotNull OfflinePlayer player) {
        final ItemStack it = new ItemStack(Material.PLAYER_HEAD);
        final SkullMeta skullMeta = (SkullMeta) it.getItemMeta();
        skullMeta.setOwningPlayer(player);
        it.setItemMeta(skullMeta);

        return it;
    }
}
