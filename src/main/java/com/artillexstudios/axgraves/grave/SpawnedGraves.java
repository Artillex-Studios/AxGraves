package com.artillexstudios.axgraves.grave;

import com.artillexstudios.axgraves.AxGraves;
import com.artillexstudios.axgraves.utils.LimitUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.artillexstudios.axgraves.AxGraves.CONFIG;

public class SpawnedGraves {
    private static final ConcurrentLinkedQueue<Grave> graves = new ConcurrentLinkedQueue<>();
    private static final Map<UUID, Map<Long, List<Grave>>> index = new ConcurrentHashMap<>();
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
        indexGrave(grave);
    }

    public static void removeGrave(Grave grave) {
        graves.remove(grave);
        unindexGrave(grave);
    }

    private static void indexGrave(Grave grave) {
        Location location = grave.getLocation();
        if (location.getWorld() == null) return;
        index.computeIfAbsent(location.getWorld().getUID(), k -> new ConcurrentHashMap<>())
                .computeIfAbsent(pack(location.getBlockX(), location.getBlockY(), location.getBlockZ()), k -> new CopyOnWriteArrayList<>())
                .add(grave);
    }

    private static void unindexGrave(Grave grave) {
        Location location = grave.getLocation();
        if (location.getWorld() == null) return;
        Map<Long, List<Grave>> worldIndex = index.get(location.getWorld().getUID());
        if (worldIndex == null) return;
        worldIndex.computeIfPresent(pack(location.getBlockX(), location.getBlockY(), location.getBlockZ()), (k, list) -> {
            list.remove(grave);
            return list.isEmpty() ? null : list;
        });
    }

    public static boolean hasGraves(@NotNull World world) {
        Map<Long, List<Grave>> worldIndex = index.get(world.getUID());
        return worldIndex != null && !worldIndex.isEmpty();
    }

    public static boolean isGrave(@NotNull Block block) {
        return getGraveAt(block) != null;
    }

    // the head entity sticks out of the grave block, so the block above is protected too
    public static boolean isProtected(@NotNull Block block) {
        Map<Long, List<Grave>> worldIndex = index.get(block.getWorld().getUID());
        if (worldIndex == null) return false;
        return worldIndex.containsKey(pack(block.getX(), block.getY(), block.getZ()))
                || worldIndex.containsKey(pack(block.getX(), block.getY() - 1, block.getZ()));
    }

    @Nullable
    public static Grave getGraveAt(@NotNull Block block) {
        Map<Long, List<Grave>> worldIndex = index.get(block.getWorld().getUID());
        if (worldIndex == null) return null;
        List<Grave> list = worldIndex.get(pack(block.getX(), block.getY(), block.getZ()));
        if (list == null) return null;
        for (Grave grave : list) return grave;
        return null;
    }

    private static long pack(int x, int y, int z) {
        return ((long) (x & 0x3FFFFFF) << 38) | ((long) (z & 0x3FFFFFF) << 12) | (y & 0xFFF);
    }

    public static ConcurrentLinkedQueue<Grave> getGraves() {
        return graves;
    }

    public static void saveToFile() {
        List<SavedGrave> savedGraves = new ArrayList<>();
        for (Grave grave : graves) {
            SavedGrave savedGrave = SavedGrave.create(grave);
            savedGraves.add(savedGrave);
        }

        File file = new File(AxGraves.getInstance().getDataFolder(), "data.json");
        try (FileWriter fw = new FileWriter(file)) {
            gson.toJson(savedGraves, fw);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void loadFromFile() {
        SavedGrave[] savedGraves;
        File file = new File(AxGraves.getInstance().getDataFolder(), "data.json");
        if (!file.exists()) return;
        try (FileReader fw = new FileReader(file)) {
            savedGraves = gson.fromJson(fw, SavedGrave[].class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
        file.delete();
        if (savedGraves == null) return;

        for (SavedGrave savedGrave : savedGraves) {
            Grave grave = savedGrave.load();
            if (grave == null) continue;
            addGrave(grave);
        }
    }
}
