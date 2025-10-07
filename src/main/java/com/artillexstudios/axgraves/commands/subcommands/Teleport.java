package com.artillexstudios.axgraves.commands.subcommands;

import com.artillexstudios.axapi.utils.PaperUtils;
import com.artillexstudios.axgraves.grave.Grave;
import com.artillexstudios.axgraves.grave.SpawnedGraves;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Optional;

import static com.artillexstudios.axgraves.AxGraves.MESSAGEUTILS;

public enum Teleport {
    INSTANCE;

    public void execute(Player sender, World world, Double x, Double y, Double z) {
        if (world == null || x == null || y == null || z == null) {
            Grave grave = SpawnedGraves.getGraves().stream().filter(gr -> gr.getPlayer().getUniqueId().equals(sender.getUniqueId())).findAny().orElse(null);
            if (grave == null) {
                MESSAGEUTILS.sendLang(sender, "grave-list.no-graves");
                return;
            }
            PaperUtils.teleportAsync(sender, grave.getLocation());
            return;
        }

        final Location location = new Location(world, x, y, z);
        Optional<Grave> grave = SpawnedGraves.getGraves().stream()
                .filter(gr -> gr.getPlayer().getUniqueId().equals(sender.getUniqueId()))
                .filter(gr -> Objects.equals(gr.getLocation().getWorld(), location.getWorld()))
                .filter(gr -> gr.getLocation().distanceSquared(location) < 1)
                .findAny();

        if (!sender.hasPermission("axgraves.tp.bypass") && grave.isEmpty()) return;
        PaperUtils.teleportAsync(sender, grave.isEmpty() ? location : grave.get().getLocation());
    }
}
