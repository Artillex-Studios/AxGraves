package com.artillexstudios.axgraves.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.artillexstudios.axgraves.AxGraves.CONFIG;

public class InventoryUtils {

    @NotNull
    public static List<ItemStack> reorderInventory(@NotNull PlayerInventory inventory, @NotNull List<ItemStack> keptItems) {
        final ArrayList<ItemStack> itemsBefore = new ArrayList<>(keptItems);
        final ItemStack[] items = new ItemStack[itemsBefore.size()];
        int n = 0;

        for (String str : CONFIG.getStringList("grave-item-order")) {
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

        return Arrays.asList(items);
    }

    public static int getRequiredRows(int amount) {
        int rows = amount / 9;
        if (amount % 9 != 0) rows++;
        return Math.max(rows, 1);
    }
}
