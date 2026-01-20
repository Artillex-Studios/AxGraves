package com.artillexstudios.axgraves.api;

import com.artillexstudios.axgraves.utils.LimitUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AxGravesAPI {

    public static int getGraveLimit(@NotNull Player player) {
        return LimitUtils.getGraveLimit(player);
    }
}
