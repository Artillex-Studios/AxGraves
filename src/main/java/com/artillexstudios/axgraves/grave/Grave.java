package com.artillexstudios.axgraves.grave;

import com.artillexstudios.axapi.entity.PacketEntityFactory;
import com.artillexstudios.axapi.entity.impl.PacketArmorStand;
import com.artillexstudios.axapi.hologram.Hologram;
import com.artillexstudios.axapi.hologram.HologramFactory;
import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.serializers.Serializers;
import com.artillexstudios.axapi.utils.EquipmentSlot;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axapi.utils.placeholder.Placeholder;
import com.artillexstudios.axgraves.api.events.GraveInteractEvent;
import com.artillexstudios.axgraves.api.events.GraveOpenEvent;
import com.artillexstudios.axgraves.utils.BlacklistUtils;
import com.artillexstudios.axgraves.utils.ExperienceUtils;
import com.artillexstudios.axgraves.utils.InventoryUtils;
import com.artillexstudios.axgraves.utils.LocationUtils;
import com.artillexstudios.axgraves.utils.Utils;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.StorageGui;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.artillexstudios.axgraves.AxGraves.CONFIG;
import static com.artillexstudios.axgraves.AxGraves.MESSAGES;
import static com.artillexstudios.axgraves.AxGraves.MESSAGEUTILS;

public class Grave {
    private static final Vector ZERO_VECTOR = new Vector(0, 0, 0);
    private final long spawned;
    private final Location location;
    private final OfflinePlayer player;
    private final String playerName;
    private final StorageGui gui;
    private int storedXP;
    private PacketArmorStand entity;
    private Hologram hologram;
    private boolean removed = false;

    public Grave(Location loc, @NotNull Player player, @NotNull ItemStack[] itemsAr, int storedXP) {
        if (MESSAGES.getBoolean("death-message.enabled", false)) {
            MESSAGEUTILS.sendLang(player, "death-message.message", Map.of("%world%", loc.getWorld().getName(), "%x%", "" + loc.getBlockX(), "%y%", "" + loc.getBlockY(), "%z%", "" + loc.getBlockZ()));
        }

        if (loc.getWorld().getEnvironment().equals(World.Environment.NETHER) || loc.getWorld().getEnvironment().equals(World.Environment.THE_END)) {
            loc.setY(Math.max(loc.getY(), CONFIG.getDouble("spawn-height-limits." + loc.getWorld().getName() + ".min", 0)));
        } else {
            loc.setY(Math.max(loc.getY(), CONFIG.getDouble("spawn-height-limits." + loc.getWorld().getName() + ".min", -64)));
        }
        loc.setY(Math.min(loc.getY(), CONFIG.getDouble("spawn-height-limits." + loc.getWorld().getName() + ".max", 319)));

        this.location = LocationUtils.getCenterOf(loc);
        this.player = player;
        this.playerName = player.getName();

        final ItemStack[] items = Arrays.stream(InventoryUtils.reorderInventory(player.getInventory(), itemsAr)).filter(Objects::nonNull).toArray(ItemStack[]::new);
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

        int itemsAm = countItems();

        int time = CONFIG.getInt("despawn-time-seconds", 180);
        boolean outOfTime = time * 1_000L <= (System.currentTimeMillis() - spawned);
        boolean despawn = CONFIG.getBoolean("despawn-when-empty", true);
        boolean empty = itemsAm == 0 && storedXP == 0;
        if ((time != -1 && outOfTime) || (despawn && empty)) {
            remove();
            return;
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

        hologram.addPlaceholder(new Placeholder((player1, string) -> {
            string = string.replace("%player%", playerName);
            string = string.replace("%xp%", "" + storedXP);
            string = string.replace("%item%", "" + countItems());
            string = string.replace("%despawn-time%", StringUtils.formatTime(time != -1 ? (time * 1_000L - (System.currentTimeMillis() - spawned)) : System.currentTimeMillis() - spawned));
            return string;
        }));

        int ms = MESSAGES.getStringList("hologram").size();
        for (int i = 0; i < ms; i++) {
            hologram.addLine(StringUtils.format(MESSAGES.getStringList("hologram").get(i)));
        }
    }

    public void update() {
        int items = countItems();

        int time = CONFIG.getInt("despawn-time-seconds", 180);
        boolean outOfTime = time * 1_000L <= (System.currentTimeMillis() - spawned);
        boolean despawn = CONFIG.getBoolean("despawn-when-empty", true);
        boolean empty = items == 0 && storedXP == 0;
        if ((time != -1 && outOfTime) || (despawn && empty)) {
            remove();
            return;
        }

        if (CONFIG.getBoolean("auto-rotation.enabled", false)) {
            entity.getLocation().setYaw(entity.getLocation().getYaw() + CONFIG.getFloat("auto-rotation.speed", 10f));
            entity.teleport(entity.getLocation());
        }
    }
    public void interact(@NotNull Player opener, org.bukkit.inventory.EquipmentSlot slot) {
        if (CONFIG.getBoolean("interact-only-own", false) && !opener.getUniqueId().equals(player.getUniqueId()) && !opener.hasPermission("axgraves.admin")) {
            MESSAGEUTILS.sendLang(opener, "interact.not-your-grave");
            return;
        }

        final GraveInteractEvent graveInteractEvent = new GraveInteractEvent(opener, this);
        Bukkit.getPluginManager().callEvent(graveInteractEvent);
        if (graveInteractEvent.isCancelled()) return;

        if (this.storedXP != 0) {
            ExperienceUtils.changeExp(opener, this.storedXP);
            this.storedXP = 0;
        }

        if (slot.equals(org.bukkit.inventory.EquipmentSlot.HAND) && opener.isSneaking()) {
            if (!CONFIG.getBoolean("enable-instant-pickup", true)) return;
            if (CONFIG.getBoolean("instant-pickup-only-own", false) && !opener.getUniqueId().equals(player.getUniqueId())) return;

            for (ItemStack it : gui.getInventory().getContents()) {
                if (it == null) continue;

                if (CONFIG.getBoolean("auto-equip-armor", true)) {
                    if (it.getType().toString().endsWith("_HELMET") && opener.getInventory().getHelmet() == null) {
                        opener.getInventory().setHelmet(it);
                        it.setAmount(0);
                        continue;
                    }

                    if (it.getType().toString().endsWith("_CHESTPLATE") && opener.getInventory().getChestplate() == null) {
                        opener.getInventory().setChestplate(it);
                        it.setAmount(0);
                        continue;
                    }

                    if (it.getType().toString().endsWith("_LEGGINGS") && opener.getInventory().getLeggings() == null) {
                        opener.getInventory().setLeggings(it);
                        it.setAmount(0);
                        continue;
                    }

                    if (it.getType().toString().endsWith("_BOOTS") && opener.getInventory().getBoots() == null) {
                        opener.getInventory().setBoots(it);
                        it.setAmount(0);
                        continue;
                    }
                }

                final Collection<ItemStack> ar = opener.getInventory().addItem(it).values();
                if (ar.isEmpty()) {
                    it.setAmount(0);
                    continue;
                }

                it.setAmount(ar.iterator().next().getAmount());
            }

            update();
            return;
        }

        final GraveOpenEvent graveOpenEvent = new GraveOpenEvent(opener, this);
        Bukkit.getPluginManager().callEvent(graveOpenEvent);
        if (graveOpenEvent.isCancelled()) return;

        gui.open(opener);
    }

    public void reload() {
        int ms = MESSAGES.getStringList("hologram").size();

        for (int i = 0; i < ms; i++) {
            final String msg = MESSAGES.getStringList("hologram").get(i);
            if (i > hologram.getLines().size() - 1) {
                hologram.addLine(StringUtils.format(msg));
            } else {
                hologram.setLine(i, StringUtils.format(msg));
            }
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
        if (removed) return;
        removed = true;

        Scheduler.get().runAt(location, scheduledTask -> {
            SpawnedGraves.removeGrave(this);
            removeInventory();

            if (entity != null) entity.remove();
            if (hologram != null) hologram.remove();
        });

    }

    public void removeInventory() {
        closeInventory();

        if (CONFIG.getBoolean("drop-items", true)) {
            for (ItemStack it : gui.getInventory().getContents()) {
                if (it == null) continue;
                final Item item = location.getWorld().dropItem(location.clone(), it);
                if (CONFIG.getBoolean("dropped-item-velocity", true)) continue;
                item.setVelocity(ZERO_VECTOR);
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

    public String getPlayerName() {
        return playerName;
    }
}
