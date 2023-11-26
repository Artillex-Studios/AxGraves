package com.artillexstudios.axgraves.commands;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axgraves.commands.subcommands.SubCommandReload;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import static com.artillexstudios.axgraves.AxGraves.MESSAGES;

@Command({"axgraves", "axgrave", "grave", "graves"})
@CommandPermission("axgraves.admin")
public class Commands {

    @DefaultFor({"~", "~ help"})
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
