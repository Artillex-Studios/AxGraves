package com.artillexstudios.axgraves.utils;

import com.artillexstudios.axgraves.config.Config;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;



public class InventoryUtils {

    @NotNull
    public static ItemStack[] reorderInventory(@NotNull PlayerInventory inventory, @NotNull ItemStack[] keptItems) {
        final ArrayList<ItemStack> itemsBefore = new ArrayList<>(Arrays.asList(keptItems));
        final ItemStack[] items = new ItemStack[itemsBefore.size()];
        int n = 0;

        for (String str : Config.graveItemOrder) {
            switch (str) {
                case "ARMOR" -> {
                    for (ItemStack it : inventory.getArmorContents()) {
                        if (!itemsBefore.contains(it)) continue;
                        items[n] = it;
                        itemsBefore.remove(it);
                        n++;
                    }
                }
                case "HAND" -> {
                    if (!itemsBefore.contains(inventory.getItemInMainHand())) continue;
                    items[n] = inventory.getItemInMainHand();
                    itemsBefore.remove(inventory.getItemInMainHand());
                    n++;
                }
                case "OFFHAND" -> {
                    if (!itemsBefore.contains(inventory.getItemInOffHand())) continue;
                    items[n] = inventory.getItemInOffHand();
                    itemsBefore.remove(inventory.getItemInOffHand());
                    n++;
                }
            }
        }

        for (ItemStack it : itemsBefore) {
            if (!itemsBefore.contains(it)) continue;
            items[n] = it;
            n++;
        }

        return items;
    }
}
