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

    /**
     * Resolves the grave protection time in seconds for a player based on their permissions.
     * Permission format: axgraves.protection.<seconds>  (use 0 to disable protection)
     * The highest value wins.
     * Falls back to grave-protection-seconds in config if no permission matches.
     */
    public static int getProtectionSeconds(Player player) {
        int best = Integer.MIN_VALUE;
        boolean has = false;

        for (PermissionAttachmentInfo pai : player.getEffectivePermissions()) {
            if (!pai.getValue()) continue;
            if (!pai.getPermission().startsWith("axgraves.protection.")) continue;
            try {
                int value = Integer.parseInt(pai.getPermission().replace("axgraves.protection.", ""));
                if (value > best) best = value;
                has = true;
            } catch (NumberFormatException ignored) {}
        }

        if (!has) return CONFIG.getInt("grave-protection-seconds", 300);
        return best;
    }

    /**
     * Resolves the despawn time in seconds for a player based on their permissions.
     * Permission format: axgraves.despawn.<seconds>  (use -1 for never despawn)
     * The highest value wins; -1 (never) beats all positive values.
     * Falls back to the global despawn-time-seconds config if no permission matches.
     */
    public static int getDespawnSeconds(Player player) {
        int best = Integer.MIN_VALUE;
        boolean has = false;

        for (PermissionAttachmentInfo pai : player.getEffectivePermissions()) {
            if (!pai.getValue()) continue;
            if (!pai.getPermission().startsWith("axgraves.despawn.")) continue;
            try {
                int value = Integer.parseInt(pai.getPermission().replace("axgraves.despawn.", ""));
                // treat -1 (never despawn) as the highest possible value
                int effective = value == -1 ? Integer.MAX_VALUE : value;
                if (effective > best) best = effective;
                has = true;
            } catch (NumberFormatException ignored) {}
        }

        if (!has) return CONFIG.getInt("despawn-time-seconds", 180);
        return best == Integer.MAX_VALUE ? -1 : best;
    }
}
