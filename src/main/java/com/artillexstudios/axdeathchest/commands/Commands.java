package com.artillexstudios.axdeathchest.commands;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axdeathchest.commands.subcommands.SubCommandReload;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import static com.artillexstudios.axdeathchest.AxDeathChest.MESSAGES;

@Command({"axdeathchest", "deathchest", "axdc", "dchest", "axdchest"})
@CommandPermission("axdeathchest.admin")
public class Commands {

    @Subcommand("help")
    public void help(@NotNull Player sender) {
        for (String m : MESSAGES.getStringList("help")) {
            sender.sendMessage(StringUtils.formatToString(m));
        }
    }

    @Subcommand("reload")
    public void reload(@NotNull Player sender) {
        new SubCommandReload().subCommand(sender);
    }
}
