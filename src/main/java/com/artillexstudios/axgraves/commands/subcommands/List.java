package com.artillexstudios.axgraves.commands.subcommands;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axgraves.grave.Grave;
import com.artillexstudios.axgraves.grave.SpawnedGraves;
import com.artillexstudios.axgraves.utils.LocationUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.artillexstudios.axgraves.AxGraves.CONFIG;
import static com.artillexstudios.axgraves.AxGraves.LANG;
import static com.artillexstudios.axgraves.AxGraves.MESSAGEUTILS;

public enum List {
    INSTANCE;

    public void execute(CommandSender sender) {
        if (SpawnedGraves.getGraves().isEmpty()) {
            MESSAGEUTILS.sendLang(sender, "grave-list.no-graves");
            return;
        }

        MESSAGEUTILS.sendFormatted(sender, LANG.getString("grave-list.header"));

        int dTime = CONFIG.getInt("despawn-time-seconds", 180);
        for (Grave grave : SpawnedGraves.getGraves()) {
            // skip grave if player doesn't have permission to view others' graves
            if (sender instanceof Player player && !grave.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                if (!sender.hasPermission("axgraves.list.other")) continue;
            }

            final Location l = grave.getLocation();

            final Map<String, String> map = Map.of(
                    "%player%", grave.getPlayerName(),
                    "%world%", LocationUtils.getWorldName(l.getWorld()),
                    "%x%", "" + l.getBlockX(),
                    "%y%", "" + l.getBlockY(),
                    "%z%", "" + l.getBlockZ(),
                    "%time%", StringUtils.formatTime(dTime != -1 ? (dTime * 1_000L - (System.currentTimeMillis() - grave.getSpawned())) : System.currentTimeMillis() - grave.getSpawned())
            );

            BaseComponent[] text = TextComponent.fromLegacyText(StringUtils.formatToString(LANG.getString("grave-list.grave"), new HashMap<>(map)));
            for (BaseComponent component : text) {
                component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format(Locale.ENGLISH, "/axgraves tp %s %f %f %f", l.getWorld().getName(), l.getX(), l.getY(), l.getZ())));
            }
            sender.spigot().sendMessage(text);
        }
    }
}
