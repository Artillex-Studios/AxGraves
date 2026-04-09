package com.artillexstudios.axgraves.commands.subcommands;

import com.artillexstudios.axapi.utils.PaperUtils;
import com.artillexstudios.axgraves.grave.Grave;
import com.artillexstudios.axgraves.grave.SpawnedGraves;
import com.artillexstudios.axgraves.utils.VaultHook;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.artillexstudios.axgraves.AxGraves.CONFIG;
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
            if (!chargeForTeleport(sender)) return;
            PaperUtils.teleportAsync(sender, grave.getLocation().clone().add(0, 0.5, 0));
            return;
        }

        final Location location = new Location(world, x, y, z);
        Optional<Grave> grave = SpawnedGraves.getGraves().stream()
                .filter(gr -> gr.getPlayer().getUniqueId().equals(sender.getUniqueId()))
                .filter(gr -> Objects.equals(gr.getLocation().getWorld(), location.getWorld()))
                .filter(gr -> gr.getLocation().distanceSquared(location) < 1)
                .findAny();

        if (!sender.hasPermission("axgraves.tp.bypass") && grave.isEmpty()) return;
        if (!chargeForTeleport(sender)) return;
        PaperUtils.teleportAsync(sender, grave.map(value -> value.getLocation().clone().add(0, 0.5, 0)).orElse(location));
    }

    private boolean chargeForTeleport(Player player) {
        if (!CONFIG.getBoolean("teleport-cost.enabled", false)) return true;
        if (player.hasPermission("axgraves.tp.free")) return true;
        if (!VaultHook.isEnabled()) return true;

        double cost = CONFIG.getDouble("teleport-cost.amount", 100.0);
        if (cost <= 0) return true;

        if (!VaultHook.hasBalance(player, cost)) {
            MESSAGEUTILS.sendLang(player, "teleport.not-enough-money", Map.of("%amount%", String.format("%,.2f", cost)));
            return false;
        }

        VaultHook.withdraw(player, cost);
        MESSAGEUTILS.sendLang(player, "teleport.cost-deducted", Map.of("%amount%", String.format("%,.2f", cost)));
        return true;
    }
}
