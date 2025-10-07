package com.artillexstudios.axgraves.commands.subcommands;

import com.artillexstudios.axapi.utils.StringUtils;
import org.bukkit.command.CommandSender;

import static com.artillexstudios.axgraves.AxGraves.MESSAGES;

public enum Help {
    INSTANCE;

    public void execute(CommandSender sender) {
        for (String m : MESSAGES.getStringList("help")) {
            sender.sendMessage(StringUtils.formatToString(m));
        }
    }
}
