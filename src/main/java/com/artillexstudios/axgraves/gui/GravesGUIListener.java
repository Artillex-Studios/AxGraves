package com.artillexstudios.axgraves.gui;

import com.artillexstudios.axgraves.commands.subcommands.Teleport;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class GravesGUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!GravesGUI.isGravesGUI(player.getUniqueId())) return;

        event.setCancelled(true);

        if (event.getCurrentItem() == null) return;
        Location loc = GravesGUI.getGraveLocation(player.getUniqueId(), event.getSlot());
        if (loc == null) return;

        player.closeInventory();
        Teleport.INSTANCE.execute(player, loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!GravesGUI.isGravesGUI(player.getUniqueId())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        GravesGUI.close(event.getPlayer().getUniqueId());
    }
}
