package com.artillexstudios.axgraves.commands;

import com.artillexstudios.axgraves.commands.subcommands.Help;
import com.artillexstudios.axgraves.commands.subcommands.List;
import com.artillexstudios.axgraves.commands.subcommands.Reload;
import com.artillexstudios.axgraves.commands.subcommands.Teleport;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"axgraves", "axgrave", "grave", "graves"})
public class Commands {

    @DefaultFor({"~", "~ help"})
    @CommandPermission("axgraves.help")
    public void help(@NotNull CommandSender sender) {
        Help.INSTANCE.execute(sender);
    }

    @Subcommand("reload")
    @CommandPermission("axgraves.reload")
    public void reload(@NotNull CommandSender sender) {
        Reload.INSTANCE.execute(sender);
    }

    @Subcommand("list")
    @CommandPermission("axgraves.list")
    public void list(@NotNull CommandSender sender) {
        List.INSTANCE.execute(sender);
    }

    @Subcommand("tp")
    @CommandPermission("axgraves.tp")
    public void tp(@NotNull Player sender, @Optional World world, @Optional Double x, @Optional Double y, @Optional Double z) {
        Teleport.INSTANCE.execute(sender, world, x, y, z);
    }
}
