package com.artillexstudios.axgraves.grave;

import com.artillexstudios.axapi.serializers.Serializers;
import com.artillexstudios.axgraves.AxGraves;
import com.artillexstudios.axgraves.utils.LimitUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.artillexstudios.axgraves.AxGraves.CONFIG;

public class SpawnedGraves {
    private static final ConcurrentLinkedQueue<Grave> graves = new ConcurrentLinkedQueue<>();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void addGrave(Grave grave) {
        Player player = grave.getPlayer().getPlayer();
        int graveLimit = player == null ? CONFIG.getInt("grave-limit", -1) : LimitUtils.getGraveLimit(player);

        if (graveLimit != -1) {
            int num = 0;
            Grave oldest = grave;

            for (Grave grave2 : graves) {
                if (!grave2.getPlayer().equals(grave.getPlayer())) continue;
                if (oldest.getSpawned() > grave2.getSpawned()) oldest = grave2;
                num++;
            }

            if (num >= graveLimit) oldest.remove();
        }

        graves.add(grave);
    }

    public static void removeGrave(Grave grave) {
        graves.remove(grave);
    }

    public static ConcurrentLinkedQueue<Grave> getGraves() {
        return graves;
    }

    public static void saveToFile() {
        final JsonArray array = new JsonArray(graves.size());

        for (Grave grave : graves) {
            final JsonObject obj = new JsonObject();
            obj.addProperty("location", Serializers.LOCATION.serialize(grave.getLocation()));
            obj.addProperty("owner", grave.getPlayer().getUniqueId().toString());
            obj.addProperty("items", Base64.getEncoder().encodeToString(Serializers.ITEM_ARRAY.serialize(grave.getGui().getInventory().getContents())));
            obj.addProperty("xp", grave.getStoredXP());
            obj.addProperty("date", grave.getSpawned());

            array.add(obj);
        }

        File file = new File(AxGraves.getInstance().getDataFolder(), "data.json");
        try (FileWriter fw = new FileWriter(file)) {
            gson.toJson(array, fw);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void loadFromFile() {
        JsonArray array;
        File file = new File(AxGraves.getInstance().getDataFolder(), "data.json");
        try (FileReader fw = new FileReader(file)) {
            array = gson.fromJson(fw, JsonArray.class);
        } catch (Exception ex) {
            return;
        }
        file.delete();
        if (array == null) return;

        try {
            for (JsonElement el : array) {
                JsonObject obj = el.getAsJsonObject();
                Location location = Serializers.LOCATION.deserialize(obj.get("location").getAsString());
                if (location == null || location.getWorld() == null) continue;
                OfflinePlayer owner = Bukkit.getOfflinePlayer(UUID.fromString(obj.get("owner").getAsString()));
                String itStr = obj.get("items").getAsString();
                ItemStack[] items = Serializers.ITEM_ARRAY.deserialize(Base64.getDecoder().decode(itStr));
                int xp = obj.get("xp").getAsInt();
                long date = obj.get("date").getAsLong();
                addGrave(new Grave(location, owner, items, xp, date));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
