package com.artillexstudios.axgraves.grave;

import com.artillexstudios.axapi.entity.PacketEntityFactory;
import com.artillexstudios.axapi.entity.impl.PacketArmorStand;
import com.artillexstudios.axapi.hologram.Hologram;
import com.artillexstudios.axapi.hologram.HologramFactory;
import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.serializers.Serializers;
import com.artillexstudios.axapi.utils.EquipmentSlot;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axgraves.api.events.GraveInteractEvent;
import com.artillexstudios.axgraves.api.events.GraveOpenEvent;
import com.artillexstudios.axgraves.utils.BlacklistUtils;
import com.artillexstudios.axgraves.utils.LocationUtils;
import com.artillexstudios.axgraves.utils.Utils;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.StorageGui;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static com.artillexstudios.axgraves.AxGraves.CONFIG;
import static com.artillexstudios.axgraves.AxGraves.MESSAGES;
import static com.artillexstudios.axgraves.AxGraves.MESSAGEUTILS;

public class Grave {
    private final long spawned;
    private final Location location;
    private final OfflinePlayer player;
    private final String playerName;
    private final StorageGui gui;
    private int storedXP;
    private final PacketArmorStand entity;
    private final Hologram hologram;

    public Grave(Location loc, @NotNull Player player, @NotNull ItemStack[] itemsAr, int storedXP) {
        this.location = LocationUtils.getCenterOf(loc);
        this.player = player;
        this.playerName = player.getName();

        final ItemStack[] items = Arrays.stream(itemsAr).filter(Objects::nonNull).toArray(ItemStack[]::new);
        this.gui = Gui.storage()
                .title(StringUtils.format(MESSAGES.getString("gui-name").replace("%player%", playerName)))
                .rows(items.length % 9 == 0 ? items.length / 9 : 1 + (items.length / 9))
                .create();

        this.storedXP = storedXP;
        this.spawned = System.currentTimeMillis();

        for (ItemStack it : items) {
            if (it == null) continue;
            if (BlacklistUtils.isBlacklisted(it)) continue;

            gui.addItem(it);
        }

        entity = (PacketArmorStand) PacketEntityFactory.get().spawnEntity(location.clone().add(0, CONFIG.getFloat("head-height", -1.2f), 0), EntityType.ARMOR_STAND);
        entity.setItem(EquipmentSlot.HELMET, Utils.getPlayerHead(player));
        entity.setSmall(true);
        entity.setInvisible(true);
        entity.setHasBasePlate(false);

        if (CONFIG.getBoolean("rotate-head-360", true)) {
            entity.getLocation().setYaw(player.getLocation().getYaw());
            entity.teleport(entity.getLocation());
        } else {
            entity.getLocation().setYaw(LocationUtils.getNearestDirection(player.getLocation().getYaw()));
            entity.teleport(entity.getLocation());
        }

        entity.onClick(event -> Scheduler.get().run(task -> interact(event.getPlayer(), event.getHand())));

        hologram = HologramFactory.get().spawnHologram(location.clone().add(0, CONFIG.getFloat("hologram-height", 1.2f), 0), Serializers.LOCATION.serialize(location), 0.3);

        for (String msg : MESSAGES.getStringList("hologram")) {
            msg = msg.replace("%player%", playerName);
            msg = msg.replace("%xp%", "" + storedXP);
            msg = msg.replace("%item%", "" + countItems());
            msg = msg.replace("%despawn-time%", StringUtils.formatTime(CONFIG.getInt("despawn-time-seconds", 180) * 1_000L - (System.currentTimeMillis() - spawned)));
            hologram.addLine(StringUtils.format(msg));
        }
    }

    public void update() {
        if (CONFIG.getBoolean("auto-rotation.enabled", false)) {
            entity.getLocation().setYaw(entity.getLocation().getYaw() + CONFIG.getFloat("auto-rotation.speed", 10f));
            entity.teleport(entity.getLocation());
        }

        int items = countItems();

        int dTime = CONFIG.getInt("despawn-time-seconds", 180);
        if (dTime != -1 && (dTime * 1_000L <= (System.currentTimeMillis() - spawned) || items == 0)) {
            remove();
            return;
        }

        int ms = MESSAGES.getStringList("hologram").size();
        for (int i = 0; i < ms; i++) {
            String msg = MESSAGES.getStringList("hologram").get(i);
            msg = msg.replace("%player%", playerName);
            msg = msg.replace("%xp%", "" + storedXP);
            msg = msg.replace("%item%", "" + items);
            msg = msg.replace("%despawn-time%", StringUtils.formatTime(dTime != -1 ? (dTime * 1_000L - (System.currentTimeMillis() - spawned)) : System.currentTimeMillis() - spawned));

            if (i > hologram.getLines().size() - 1) {
                hologram.addLine(StringUtils.format(msg));
            } else {
                hologram.setLine(i, StringUtils.format(msg));
            }
        }
    }

    public void interact(@NotNull Player player, org.bukkit.inventory.EquipmentSlot slot) {
        if (CONFIG.getBoolean("interact-only-own", false) && !player.getUniqueId().equals(player.getUniqueId()) && !player.hasPermission("axgraves.admin")) {
            MESSAGEUTILS.sendLang(player, "interact.not-your-grave");
            return;
        }

        final GraveInteractEvent deathChestInteractEvent = new GraveInteractEvent(player, this);
        Bukkit.getPluginManager().callEvent(deathChestInteractEvent);
        if (deathChestInteractEvent.isCancelled()) return;

        if (this.storedXP != 0) {
            player.giveExp(this.storedXP);
            this.storedXP = 0;
        }

        if (slot.equals(org.bukkit.inventory.EquipmentSlot.HAND) && player.isSneaking()) {
            if (!CONFIG.getBoolean("enable-instant-pickup", true)) return;
            if (CONFIG.getBoolean("instant-pickup-only-own", false) && !player.getUniqueId().equals(player.getUniqueId())) return;

            for (ItemStack it : gui.getInventory().getContents()) {
                if (it == null) continue;

                final Collection<ItemStack> ar = player.getInventory().addItem(it).values();
                if (ar.isEmpty()) {
                    it.setAmount(0);
                    continue;
                }

                it.setAmount(ar.iterator().next().getAmount());
            }

            update();
            return;
        }

        final GraveOpenEvent deathChestOpenEvent = new GraveOpenEvent(player, this);
        Bukkit.getPluginManager().callEvent(deathChestOpenEvent);
        if (deathChestOpenEvent.isCancelled()) return;

        gui.open(player);
    }

    public void reload() {
        for (int i = 0; i < hologram.getLines().size(); i++) {
            hologram.removeLine(i);
        }
    }

    public int countItems() {
        int am = 0;
        for (ItemStack it : gui.getInventory().getContents()) {
            if (it == null) continue;
            am++;
        }
        return am;
    }

    public void remove() {
        SpawnedGrave.removeGrave(Grave.this);

        Scheduler.get().runAt(location, scheduledTask -> {
            removeInventory();

            entity.remove();
            hologram.remove();
        });

    }

    public void removeInventory() {
        closeInventory();

        if (CONFIG.getBoolean("drop-items", true)) {
            for (ItemStack it : gui.getInventory().getContents()) {
                if (it == null) continue;
                location.getWorld().dropItem(location.clone().add(0, -1.0, 0), it);
            }
        }

        if (storedXP == 0) return;
        final ExperienceOrb exp = (ExperienceOrb) location.getWorld().spawnEntity(location, EntityType.EXPERIENCE_ORB);
        exp.setExperience(storedXP);
    }

    private void closeInventory() {
        final List<HumanEntity> viewers = new ArrayList<>(gui.getInventory().getViewers());
        final Iterator<HumanEntity> viewerIterator = viewers.iterator();

        while (viewerIterator.hasNext()) {
            viewerIterator.next().closeInventory();
            viewerIterator.remove();
        }
    }

    public Location getLocation() {
        return location;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public long getSpawned() {
        return spawned;
    }

    public StorageGui getGui() {
        return gui;
    }

    public int getStoredXP() {
        return storedXP;
    }

    public PacketArmorStand getEntity() {
        return entity;
    }

    public Hologram getHologram() {
        return hologram;
    }
}
