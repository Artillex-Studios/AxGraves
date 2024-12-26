package com.artillexstudios.axgraves.commands.subcommands;

import com.artillexstudios.axapi.utils.MessageUtils;
import com.artillexstudios.axgraves.config.Config;
import com.artillexstudios.axgraves.config.Lang;
import com.artillexstudios.axgraves.grave.Grave;
import com.artillexstudios.axgraves.grave.SpawnedGraves;
import com.artillexstudios.axgraves.schedulers.SaveGraves;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static com.artillexstudios.axgraves.AxGraves.EXECUTOR;

public enum SubCommandReload {
    INSTANCE;

    public void subCommand(@NotNull CommandSender sender) {
        if (!Config.reload()) {
            MessageUtils.sendMessage(sender, Config.prefix, Lang.Reload.failed, Placeholder.parsed("file", "config.yml"));
            return;
        }

        if (!Lang.reload()) {
            MessageUtils.sendMessage(sender, Config.prefix, Lang.Reload.failed, Placeholder.parsed("file", "messages.yml"));
            return;
        }

        EXECUTOR.execute(() -> {
            for (Grave grave : SpawnedGraves.getGraves()) {
                grave.reload();
                grave.update();
            }
        });

        SaveGraves.start();

        MessageUtils.sendMessage(sender, Config.prefix, Lang.Reload.success);
    }
}
