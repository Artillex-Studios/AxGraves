package com.artillexstudios.axgraves.utils;

import com.artillexstudios.axapi.nms.wrapper.ServerPlayerWrapper;
import com.artillexstudios.axapi.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.artillexstudios.axgraves.AxGraves.CONFIG;

public class Utils {

    @NotNull
    public static ItemStack getPlayerHead(@NotNull OfflinePlayer player) {
        ItemBuilder builder = ItemBuilder.create(Material.PLAYER_HEAD);

        String texture = null;
        if (CONFIG.getBoolean("custom-grave-skull.enabled", false)) {
            texture = CONFIG.getString("custom-grave-skull.base64");
        } else if (player.getPlayer() != null) {
            ServerPlayerWrapper wrapper = ServerPlayerWrapper.wrap(player);
            texture = wrapper.textures().texture();
        }

        if (texture != null) builder.setTextureValue(texture);

        return builder.get();
    }
}
