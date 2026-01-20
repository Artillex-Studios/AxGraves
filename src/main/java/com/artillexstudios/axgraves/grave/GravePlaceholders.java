package com.artillexstudios.axgraves.grave;

import com.artillexstudios.axapi.placeholders.PlaceholderHandler;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axgraves.utils.LimitUtils;
import org.bukkit.entity.Player;

import static com.artillexstudios.axgraves.AxGraves.CONFIG;

public class GravePlaceholders {
    private static int time;

    public static void reload() {
        time = CONFIG.getInt("despawn-time-seconds", 180);
    }

    public static void register() {
        reload();

        String empty = "";
        PlaceholderHandler.register("player", handler -> {
            Grave grave = handler.raw(Grave.class);
            if (grave == null) return empty;
            return grave.getPlayerName();
        }, false);

        PlaceholderHandler.register("xp", handler -> {
            Grave grave = handler.raw(Grave.class);
            if (grave == null) return empty;
            return String.valueOf(grave.getStoredXP());
        }, false);

        PlaceholderHandler.register("item", handler -> {
            Grave grave = handler.raw(Grave.class);
            if (grave == null) return empty;
            return String.valueOf(grave.countItems());
        }, false);

        PlaceholderHandler.register("despawn-time", handler -> {
            Grave grave = handler.raw(Grave.class);
            if (grave == null) return empty;
            long spawned = grave.getSpawned();
            return StringUtils.formatTime(time != -1 ? (time * 1_000L - (System.currentTimeMillis() - spawned)) : System.currentTimeMillis() - spawned);
        }, false);

        PlaceholderHandler.register("grave_count", handler -> {
            return String.valueOf(SpawnedGraves.getGraves().size());
        }, true);

        PlaceholderHandler.register("grave_limit", handler -> {
            Player player = handler.resolve(Player.class);
            if (player == null) return empty;
            int limit = LimitUtils.getGraveLimit(player);
            return String.valueOf(limit == -1 ? "∞" : limit);
        }, true);
    }
}
