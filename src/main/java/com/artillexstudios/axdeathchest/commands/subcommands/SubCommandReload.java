package com.artillexstudios.axdeathchest.commands.subcommands;

import com.artillexstudios.axdeathchest.chests.DeathChest;
import com.artillexstudios.axdeathchest.chests.SpawnedChests;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.artillexstudios.axdeathchest.AxDeathChest.CONFIG;
import static com.artillexstudios.axdeathchest.AxDeathChest.EXECUTOR;
import static com.artillexstudios.axdeathchest.AxDeathChest.MESSAGES;
import static com.artillexstudios.axdeathchest.AxDeathChest.MESSAGEUTILS;

public class SubCommandReload {

    public void subCommand(@NotNull CommandSender sender) {

        final String errorMsg = CONFIG.getString("prefix") + MESSAGES.getString("reload.failed");

        if (!CONFIG.reload()) {
            MESSAGEUTILS.sendFormatted(sender, "reload.failed", Map.of("%file%", "config.yml"));
            return;
        }

        if (!MESSAGES.reload()) {
            MESSAGEUTILS.sendFormatted(sender, errorMsg, Map.of("%file%", "messages.yml"));
            return;
        }


        EXECUTOR.execute(() -> {
            for (DeathChest deathChest : SpawnedChests.getChests()) {
                deathChest.reload();
                deathChest.update();
            }
        });
        MESSAGEUTILS.sendLang(sender, "reload.success");
    }
}
