package com.artillexstudios.axgraves.listeners;

import com.artillexstudios.axgraves.api.events.GravePreSpawnEvent;
import com.artillexstudios.axgraves.api.events.GraveSpawnEvent;
import com.artillexstudios.axgraves.grave.Grave;
import com.artillexstudios.axgraves.grave.SpawnedGrave;
import com.artillexstudios.axgraves.utils.ExperienceUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import static com.artillexstudios.axgraves.AxGraves.CONFIG;

public class DeathListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(@NotNull PlayerDeathEvent event) {
        if (CONFIG.getStringList("disabled-worlds") != null && CONFIG.getStringList("disabled-worlds").contains(event.getEntity().getWorld().getName())) return;
        if (!CONFIG.getBoolean("override-keep-inventory", true) && event.getKeepInventory()) return;

        final Player player = event.getEntity();

        if (player.getInventory().isEmpty() && player.getTotalExperience() == 0) return;

        Grave grave = null;

        if (!event.getKeepInventory()) {
            grave = new Grave(player.getLocation(), player, event.getDrops().toArray(new ItemStack[0]), ExperienceUtils.getExp(player));
        } else if (CONFIG.getBoolean("override-keep-inventory", true)) {
            grave = new Grave(player.getLocation(), player, player.getInventory().getContents(), ExperienceUtils.getExp(player));
            player.setLevel(0);
            player.setTotalExperience(0);
            player.getInventory().clear();
        }

        if (grave == null) return;

        final GravePreSpawnEvent gravePreSpawnEvent = new GravePreSpawnEvent(player, grave);
        Bukkit.getPluginManager().callEvent(gravePreSpawnEvent);
        if (gravePreSpawnEvent.isCancelled()) return;

        event.setDroppedExp(0);
        event.getDrops().clear();

        SpawnedGrave.addGrave(grave);

        final GraveSpawnEvent graveSpawnEvent = new GraveSpawnEvent(player, grave);
        Bukkit.getPluginManager().callEvent(graveSpawnEvent);

        Location deathLocation = player.getLocation();
        if (CONFIG.getBoolean("global-announce-death-coordinates", true)){
            for (Player p : Bukkit.getOnlinePlayers()){
                p.sendMessage(String.format("%s died at X:%.2f Y:%.2f Z:%.2f", player.getName(), deathLocation.getX(), deathLocation.getY(), deathLocation.getZ())); 
            }
        } else if (CONFIG.getBoolean("private-announce-death-coordinates", true)){
            player.sendMessage(String.format("You died at X:%.2f Y:%.2f Z:%.2f", deathLocation.getX(), deathLocation.getY(), deathLocation.getZ())); 
        }
    }
}
