package com.artillexstudios.axgraves.commands.subcommands;

import com.artillexstudios.axapi.utils.MessageUtils;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axgraves.config.Config;
import com.artillexstudios.axgraves.config.Lang;
import com.artillexstudios.axgraves.grave.Grave;
import com.artillexstudios.axgraves.grave.SpawnedGraves;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public enum SubCommandList {
    INSTANCE;

    public void subCommand(@NotNull CommandSender sender) {
        if (SpawnedGraves.getGraves().isEmpty()) {
            MessageUtils.sendMessage(sender, Config.prefix, Lang.GraveList.noGraves);
            return;
        }

        MessageUtils.sendMessage(sender, Lang.GraveList.header);

        int dTime = Config.despawnTimeSeconds;
        for (Grave grave : SpawnedGraves.getGraves()) {
            if (!sender.hasPermission("axgraves.list.other") &&
                    sender instanceof Player &&
                    !grave.getPlayer().equals(sender)
            ) continue;

            final Location l = grave.getLocation();

            final Map<String, String> map = Map.of("%player%", grave.getPlayerName(),
                    "%world%", l.getWorld().getName(),
                    "%x%", "" + l.getBlockX(),
                    "%y%", "" + l.getBlockY(),
                    "%z%", "" + l.getBlockZ(),
                    "%time%", StringUtils.formatTime(dTime != -1 ? (dTime * 1_000L - (System.currentTimeMillis() - grave.getSpawned())) : System.currentTimeMillis() - grave.getSpawned()));

            BaseComponent[] text = TextComponent.fromLegacyText(StringUtils.formatToString(Lang.GraveList.grave, new HashMap<>(map)));
            for (BaseComponent component : text) {
                component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/axgraves tp %s %f %f %f", l.getWorld().getName(), l.getX(), l.getY(), l.getZ())));
            }
            sender.spigot().sendMessage(text);
        }
    }
}
