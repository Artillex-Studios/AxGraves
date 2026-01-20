package com.artillexstudios.axgraves.utils;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import static com.artillexstudios.axgraves.AxGraves.CONFIG;

public class LimitUtils {

    public static int getGraveLimit(Player player) {
        int am = 0;
        boolean has = false;

        for (PermissionAttachmentInfo pai : player.getEffectivePermissions()) {
            if (!pai.getValue()) continue;
            if (!pai.getPermission().startsWith("axgraves.limit.")) continue;
            int value = Integer.parseInt(pai.getPermission().replace("axgraves.limit.", ""));
            am = Math.max(am, value);
            has = true;
        }

        if (!has) return CONFIG.getInt("grave-limit", -1);
        return am;
    }
}
