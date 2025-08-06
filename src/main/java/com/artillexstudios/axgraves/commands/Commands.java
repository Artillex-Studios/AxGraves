package com.artillexstudios.axgraves.commands;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axgraves.commands.subcommands.SubCommandList;
import com.artillexstudios.axgraves.commands.subcommands.SubCommandReload;
import com.artillexstudios.axgraves.commands.subcommands.SubCommandTeleport;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import static com.artillexstudios.axgraves.AxGraves.MESSAGES;

@Command({"axgraves", "axgrave", "grave", "graves"})
public class Commands {

    @DefaultFor({"~", "~ help"})
    @CommandPermission("axgraves.help")
    public void help(@NotNull CommandSender sender) {
        for (String m : MESSAGES.getStringList("help")) {
            sender.sendMessage(StringUtils.formatToString(m));
        }
    }

    @Subcommand("reload")
    @CommandPermission("axgraves.reload")
    public void reload(@NotNull CommandSender sender) {
        SubCommandReload.INSTANCE.subCommand(sender);
    }

    @Subcommand("list")
    @CommandPermission("axgraves.list")
    public void list(@NotNull CommandSender sender) {
        SubCommandList.INSTANCE.subCommand(sender);
    }

    @Subcommand("tp")
    @CommandPermission("axgraves.tp")
    public void tp(@NotNull Player sender, @Optional World world, @Optional Double x, @Optional Double y, @Optional Double z) {
        SubCommandTeleport.INSTANCE.subCommand(sender, world, x, y, z);
    }
}
