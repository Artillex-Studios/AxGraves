package com.artillexstudios.axgraves.commands;

import com.artillexstudios.axapi.utils.PaperUtils;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axgraves.commands.subcommands.SubCommandList;
import com.artillexstudios.axgraves.commands.subcommands.SubCommandReload;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import static com.artillexstudios.axgraves.AxGraves.MESSAGES;

@Command({"axgraves", "axgrave", "grave", "graves"})
public class Commands {

    @DefaultFor({"~", "~ help"})
    @CommandPermission("axgraves.help")
    public void help(@NotNull Player sender) {
        for (String m : MESSAGES.getStringList("help")) {
            sender.sendMessage(StringUtils.formatToString(m));
        }
    }

    @Subcommand("reload")
    @CommandPermission("axgraves.reload")
    public void reload(@NotNull Player sender) {
        new SubCommandReload().subCommand(sender);
    }

    @Subcommand("list")
    @CommandPermission("axgraves.list")
    public void list(@NotNull Player sender) {
        new SubCommandList().subCommand(sender);
    }

    @Subcommand("tp")
    @CommandPermission("axgraves.tp")
    public void tp(@NotNull Player sender, World world, double x, double y, double z) {
        PaperUtils.teleportAsync(sender, new Location(world, x, y, z));
    }
}
