package com.artillexstudios.axgraves.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;
import java.util.logging.Level;

public class Utils {

    @NotNull
    public static ItemStack getPlayerHead(@NotNull OfflinePlayer player) {
        final ItemStack it = new ItemStack(Material.PLAYER_HEAD);
        if (it.getItemMeta() instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(player);
            it.setItemMeta(skullMeta);
        }
        return it;
    }


    @Nullable
    public static ItemStack getBase64Skull(@NotNull String b64Texture) {
        ItemStack fallbackHead = new ItemStack(Material.PLAYER_HEAD);
        try {
            JsonObject jsonObject = new Gson().fromJson(new String(Base64.getDecoder().decode(b64Texture)), JsonObject.class);

            if (!jsonObject.has("textures")
                    || !jsonObject.getAsJsonObject("textures").has("SKIN")
                    || !jsonObject.getAsJsonObject("textures").getAsJsonObject("SKIN").has("url")) {
                Bukkit.getLogger().log(Level.WARNING, "[AxGraves] Missing data in custom base64 skull texture when decoded-to JSON.");
                return fallbackHead;
            }

            String urlString = jsonObject.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
            URL urlObject = URI.create(urlString).toURL();

            PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
            PlayerTextures texturesProfile = profile.getTextures();
            texturesProfile.setSkin(urlObject);
            profile.setTextures(texturesProfile);

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            if (head.getItemMeta() instanceof SkullMeta meta) {
                meta.setOwnerProfile(profile);
                head.setItemMeta(meta);
                return head;
            }

            // if we reach here, it the item meta is not SkullMeta for some reason
            Bukkit.getLogger().log(Level.WARNING, "[AxGraves] Failed to set custom skull texture.");
            return fallbackHead;

        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().log(Level.WARNING, "[AxGraves] Invalid Base64 custom skull texture string provided.", e);
            return fallbackHead;
        } catch (JsonSyntaxException e) {
            Bukkit.getLogger().log(Level.WARNING, "[AxGraves] Invalid JSON in decoded custom skull texture data.", e);
            return fallbackHead;
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "[AxGraves] An unexpected error occurred while creating custom texture skull.", e);
            return fallbackHead;
        }
    }
}
