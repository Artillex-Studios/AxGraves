package com.artillexstudios.axgraves.commands.subcommands;

import com.artillexstudios.axgraves.grave.Grave;
import com.artillexstudios.axgraves.grave.SpawnedGraves;
import com.artillexstudios.axgraves.schedulers.SaveGraves;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.artillexstudios.axgraves.AxGraves.CONFIG;
import static com.artillexstudios.axgraves.AxGraves.EXECUTOR;
import static com.artillexstudios.axgraves.AxGraves.MESSAGES;
import static com.artillexstudios.axgraves.AxGraves.MESSAGEUTILS;

public enum SubCommandReload {
    INSTANCE;

    public void subCommand(@NotNull CommandSender sender) {
        final String errorMsg = CONFIG.getString("prefix") + MESSAGES.getString("reload.failed");

        if (!CONFIG.reload()) {
            MESSAGEUTILS.sendLang(sender, "reload.failed", Map.of("%file%", "config.yml"));
            return;
        }

        if (!MESSAGES.reload()) {
            MESSAGEUTILS.sendFormatted(sender, errorMsg, Map.of("%file%", "messages.yml"));
            return;
        }

        EXECUTOR.execute(() -> {
            for (Grave grave : SpawnedGraves.getGraves()) {
                grave.update();
                grave.updateHologram();
            }
        });

        SaveGraves.start();

        MESSAGEUTILS.sendLang(sender, "reload.success");
    }
}
