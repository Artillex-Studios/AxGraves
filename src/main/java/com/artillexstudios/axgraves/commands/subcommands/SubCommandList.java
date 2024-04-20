package com.artillexstudios.axgraves.commands.subcommands;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axgraves.grave.Grave;
import com.artillexstudios.axgraves.grave.SpawnedGraves;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.artillexstudios.axgraves.AxGraves.CONFIG;
import static com.artillexstudios.axgraves.AxGraves.MESSAGES;
import static com.artillexstudios.axgraves.AxGraves.MESSAGEUTILS;

public class SubCommandList {

    public void subCommand(@NotNull CommandSender sender) {
        if (SpawnedGraves.getGraves().isEmpty()) {
            MESSAGEUTILS.sendLang(sender, "grave-list.no-graves");
            return;
        }

        MESSAGEUTILS.sendFormatted(sender, MESSAGES.getString("grave-list.header"));

        int dTime = CONFIG.getInt("despawn-time-seconds", 180);
        for (Grave grave : SpawnedGraves.getGraves()) {
            if (!sender.hasPermission("axgraves.list.other") && sender instanceof Player && grave.getPlayer().getUniqueId() != ((Player) sender).getUniqueId()) continue;
            final Location l = grave.getLocation();
            MESSAGEUTILS.sendFormatted(sender, MESSAGES.getString("grave-list.grave"), Map.of("%player%", grave.getPlayerName(), "%world%", l.getWorld().getName(), "%x%", "" + l.getBlockX(), "%y%", "" + l.getBlockY(), "%z%", "" + l.getBlockZ(), "%time%", StringUtils.formatTime(dTime != -1 ? (dTime * 1_000L - (System.currentTimeMillis() - grave.getSpawned())) : System.currentTimeMillis() - grave.getSpawned())));
        }
    }
}
