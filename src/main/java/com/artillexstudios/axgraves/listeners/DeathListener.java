package com.artillexstudios.axgraves.listeners;

import com.artillexstudios.axgraves.api.events.GravePreSpawnEvent;
import com.artillexstudios.axgraves.api.events.GraveSpawnEvent;
import com.artillexstudios.axgraves.grave.Grave;
import com.artillexstudios.axgraves.grave.SpawnedGraves;
import com.artillexstudios.axgraves.utils.ExperienceUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import static com.artillexstudios.axgraves.AxGraves.CONFIG;

public class DeathListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(@NotNull PlayerDeathEvent event) {
        if (CONFIG.getStringList("disabled-worlds") != null && CONFIG.getStringList("disabled-worlds").contains(event.getEntity().getWorld().getName())) return;
        if (!CONFIG.getBoolean("override-keep-inventory", true) && event.getKeepInventory()) return;

        Player player = event.getEntity();
        if (!player.hasPermission("axgraves.allowgraves")) return;

        if (player.getLastDamageCause() != null && CONFIG.getStringList("blacklisted-death-causes").contains(player.getLastDamageCause().getCause().name())) return;
        if (player.getInventory().isEmpty() && player.getTotalExperience() == 0) return;

        int xp = 0;
        boolean storeXp = CONFIG.getBoolean("store-xp", true);
        if (storeXp) {
            xp = Math.round(ExperienceUtils.getExp(player) * CONFIG.getFloat("xp-keep-percentage", 1f));
        }

        Location location = player.getLocation();
        location.add(0, -0.5, 0);

        final GravePreSpawnEvent gravePreSpawnEvent = new GravePreSpawnEvent(player, location);
        Bukkit.getPluginManager().callEvent(gravePreSpawnEvent);
        if (gravePreSpawnEvent.isCancelled()) return;

        List<ItemStack> drops = null;
        if (!event.getKeepInventory()) {
            drops = event.getDrops();
        } else if (CONFIG.getBoolean("override-keep-inventory", true)) {
            drops = Arrays.asList(player.getInventory().getContents());
            if (storeXp) {
                player.setLevel(0);
                player.setTotalExperience(0);
            }
            player.getInventory().clear();
        }

        if (drops == null) return;
        Grave grave = new Grave(location, player, drops, xp, System.currentTimeMillis());

        if (storeXp) event.setDroppedExp(0);
        event.getDrops().clear();

        SpawnedGraves.addGrave(grave);

        final GraveSpawnEvent graveSpawnEvent = new GraveSpawnEvent(player, grave);
        Bukkit.getPluginManager().callEvent(graveSpawnEvent);
    }
}