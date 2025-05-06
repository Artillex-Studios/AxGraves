package com.artillexstudios.axgraves.commands.subcommands;

import com.artillexstudios.axapi.utils.PaperUtils;
import com.artillexstudios.axgraves.grave.Grave;
import com.artillexstudios.axgraves.grave.SpawnedGraves;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public enum SubCommandTeleport {
    INSTANCE;

    public void subCommand(@NotNull Player sender, World world, double x, double y, double z) {
        final Location location = new Location(world, x, y, z);

        Optional<Grave> foundGrave = SpawnedGraves.getGraves().stream()
                .filter(grave -> grave.getPlayer().getUniqueId().equals(sender.getUniqueId()))
                .filter(grave -> Objects.equals(grave.getLocation().getWorld(), location.getWorld()))
                .filter(grave -> grave.getLocation().distanceSquared(location) < 1)
                .findAny();

        if (!sender.hasPermission("axgraves.tp.bypass") && foundGrave.isEmpty()) return;
        PaperUtils.teleportAsync(sender, foundGrave.isEmpty() ? location : foundGrave.get().getLocation());
    }
}
