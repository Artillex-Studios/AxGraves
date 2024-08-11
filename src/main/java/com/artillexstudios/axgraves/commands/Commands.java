package com.artillexstudios.axgraves.commands;

import com.artillexstudios.axapi.utils.PaperUtils;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axgraves.commands.subcommands.SubCommandList;
import com.artillexstudios.axgraves.commands.subcommands.SubCommandReload;
import com.artillexstudios.axgraves.grave.SpawnedGraves;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
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
    public void help(@NotNull CommandSender sender) {
        for (String m : MESSAGES.getStringList("help")) {
            sender.sendMessage(StringUtils.formatToString(m));
        }
    }

    @Subcommand("reload")
    @CommandPermission("axgraves.reload")
    public void reload(@NotNull CommandSender sender) {
        new SubCommandReload().subCommand(sender);
    }

    @Subcommand("list")
    @CommandPermission("axgraves.list")
    public void list(@NotNull CommandSender sender) {
        new SubCommandList().subCommand(sender);
    }

    @Subcommand("tp")
    @CommandPermission("axgraves.tp")
    public void tp(@NotNull Player sender, World world, double x, double y, double z) {
        final Location location = new Location(world, x, y, z);
        if (!sender.hasPermission("axgraves.tp.bypass") && SpawnedGraves.getGraves().stream().filter(grave -> grave.getPlayer().getUniqueId().equals(sender.getUniqueId())).filter(grave -> grave.getLocation().equals(location)).findAny().isEmpty()) {
            return;
        }
        PaperUtils.teleportAsync(sender, location);
    }
}
