package com.artillexstudios.axgraves.grave;

import com.artillexstudios.axapi.hologram.Hologram;
import com.artillexstudios.axapi.hologram.HologramType;
import com.artillexstudios.axapi.hologram.HologramTypes;
import com.artillexstudios.axapi.hologram.page.HologramPage;
import com.artillexstudios.axapi.items.WrappedItemStack;
import com.artillexstudios.axapi.libs.boostedyaml.block.implementation.Section;
import com.artillexstudios.axapi.nms.NMSHandlers;
import com.artillexstudios.axapi.packet.wrapper.serverbound.ServerboundInteractWrapper;
import com.artillexstudios.axapi.packetentity.PacketEntity;
import com.artillexstudios.axapi.packetentity.meta.entity.ArmorStandMeta;
import com.artillexstudios.axapi.packetentity.meta.entity.DisplayMeta;
import com.artillexstudios.axapi.packetentity.meta.entity.TextDisplayMeta;
import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.utils.EquipmentSlot;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axgraves.api.events.GraveInteractEvent;
import com.artillexstudios.axgraves.api.events.GraveOpenEvent;
import com.artillexstudios.axgraves.utils.BlacklistUtils;
import com.artillexstudios.axgraves.utils.ExperienceUtils;
import com.artillexstudios.axgraves.utils.InventoryUtils;
import com.artillexstudios.axgraves.utils.LocationUtils;
import com.artillexstudios.axgraves.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.artillexstudios.axgraves.AxGraves.CONFIG;
import static com.artillexstudios.axgraves.AxGraves.LANG;
import static com.artillexstudios.axgraves.AxGraves.MESSAGEUTILS;

public class Grave {
    private static final Vector ZERO_VECTOR = new Vector(0, 0, 0);
    private final long spawned;
    private final Location location;
    private final OfflinePlayer player;
    private final String playerName;
    private final Inventory gui;
    private int storedXP;
    private final PacketEntity entity;
    private Hologram hologram;
    private boolean removed = false;

    public Grave(Location loc, @NotNull OfflinePlayer offlinePlayer, @NotNull List<ItemStack> items, int storedXP, long date) {
        items = new ArrayList<>(items);
        items.removeIf(it -> {
            if (it == null) return true;
            if (BlacklistUtils.isBlacklisted(it)) return true;
            return false;
        });
        items.replaceAll(ItemStack::clone); // clone all items

        System.out.println(loc);
        this.location = LocationUtils.getCenterOf(loc, true, false);
        System.out.println(location);
        this.player = offlinePlayer;
        this.playerName = offlinePlayer.getName() == null ? LANG.getString("unknown-player", "???") : offlinePlayer.getName();
        this.storedXP = storedXP;
        this.spawned = date;
        this.gui = Bukkit.createInventory(
                null,
                InventoryUtils.getRequiredRows(items.size()) * 9,
                StringUtils.formatToString(LANG.getString("gui-name").replace("%player%", playerName))
        );

        LocationUtils.clampLocation(location);
        System.out.println(location);

        Player pl = offlinePlayer.getPlayer();
        if (pl != null) {
            items = InventoryUtils.reorderInventory(pl.getInventory(), items);
            if (LANG.getBoolean("death-message.enabled", false)) {
                MESSAGEUTILS.sendLang(pl, "death-message.message", Map.of("%world%", LocationUtils.getWorldName(location.getWorld()), "%x%", "" + location.getBlockX(), "%y%", "" + location.getBlockY(), "%z%", "" + location.getBlockZ()));
            }
        }
        items.forEach(gui::addItem);

        this.entity = NMSHandlers.getNmsHandler().createEntity(EntityType.ARMOR_STAND, location.clone().add(0, 1 + CONFIG.getFloat("head-height", -1.2f), 0));
        entity.setItem(EquipmentSlot.HELMET, WrappedItemStack.wrap(Utils.getPlayerHead(offlinePlayer)));
        final ArmorStandMeta meta = (ArmorStandMeta) entity.meta();
        meta.small(true);
        meta.invisible(true);
        meta.setNoBasePlate(false);
        entity.spawn();

        if (CONFIG.getBoolean("rotate-head-360", true)) {
            entity.location().setYaw(location.getYaw());
            entity.teleport(entity.location());
        } else {
            entity.location().setYaw(LocationUtils.getNearestDirection(location.getYaw()));
            entity.teleport(entity.location());
        }

        entity.onInteract(event -> Scheduler.get().run(task -> interact(event.getPlayer(), event.getHand())));

        updateHologram();
    }

    public void update() {
        int items = countItems();

        int time = CONFIG.getInt("despawn-time-seconds", 180);
        boolean outOfTime = time * 1_000L <= (System.currentTimeMillis() - spawned);
        boolean despawn = CONFIG.getBoolean("despawn-when-empty", true);
        boolean empty = items == 0 && storedXP == 0;
        if ((time != -1 && outOfTime) || (despawn && empty)) {
            Scheduler.get().runAt(location, this::remove);
            return;
        }

        if (CONFIG.getBoolean("auto-rotation.enabled", false)) {
            entity.location().setYaw(entity.location().getYaw() + CONFIG.getFloat("auto-rotation.speed", 10f));
            entity.teleport(entity.location());
        }
    }

    public void interact(@NotNull Player opener, ServerboundInteractWrapper.InteractionHand slot) {
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

        if (slot != null && slot.equals(ServerboundInteractWrapper.InteractionHand.MAIN_HAND) && opener.isSneaking()) {
            if (opener.getGameMode() == GameMode.SPECTATOR) return;
            if (!CONFIG.getBoolean("enable-instant-pickup", true)) return;
            if (CONFIG.getBoolean("instant-pickup-only-own", false) && !opener.getUniqueId().equals(player.getUniqueId())) return;

            for (ItemStack it : gui.getContents()) {
                if (it == null) continue;

                if (CONFIG.getBoolean("auto-equip-armor", true)) {
                    if ((EnchantmentTarget.ARMOR_HEAD.includes(it) || it.getType().equals(Material.TURTLE_HELMET)) && opener.getInventory().getHelmet() == null) {
                        opener.getInventory().setHelmet(it);
                        it.setAmount(0);
                        continue;
                    }

                    if ((EnchantmentTarget.ARMOR_TORSO.includes(it) || it.getType().equals(Material.ELYTRA)) && opener.getInventory().getChestplate() == null) {
                        opener.getInventory().setChestplate(it);
                        it.setAmount(0);
                        continue;
                    }

                    if (EnchantmentTarget.ARMOR_LEGS.includes(it) && opener.getInventory().getLeggings() == null) {
                        opener.getInventory().setLeggings(it);
                        it.setAmount(0);
                        continue;
                    }

                    if (EnchantmentTarget.ARMOR_FEET.includes(it) && opener.getInventory().getBoots() == null) {
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

        opener.openInventory(gui);
    }

    public void updateHologram() {
        if (hologram != null) hologram.remove();

        List<String> lines = LANG.getStringList("hologram");

        double hologramHeight = CONFIG.getFloat("hologram-height", 0.75f) + 1;
        hologram = new Hologram(location.clone().add(0, getNewHeight(hologramHeight, lines.size(), 0.3f), 0));

        HologramPage<String, HologramType<String>> page = hologram.createPage(HologramTypes.TEXT);
        page.getParameters().withParameter(Grave.class, this);

        Section section = CONFIG.getSection("holograms");
        page.setEntityMetaHandler(m -> {
            TextDisplayMeta meta = (TextDisplayMeta) m;
            meta.seeThrough(section.getBoolean("see-through"));
            meta.alignment(TextDisplayMeta.Alignment.valueOf(section.getString("alignment").toUpperCase()));
            meta.backgroundColor(Integer.parseInt(section.getString("background-color"), 16));
            meta.lineWidth(1000);
            meta.billboardConstrain(DisplayMeta.BillboardConstrain.valueOf(section.getString("billboard").toUpperCase()));
        });

        page.setContent(String.join("<reset><br>", lines));
        page.spawn();
    }

    private static double getNewHeight(double y, int lines, float lineHeight) {
        return y - lineHeight * (lines - 1) + 0.25;
    }

    public int countItems() {
        int am = 0;
        for (ItemStack it : gui.getContents()) {
            if (it == null) continue;
            am++;
        }
        return am;
    }

    public void remove() {
        if (removed) return;
        removed = true;

        Runnable runnable = () -> {
            SpawnedGraves.removeGrave(this);
            removeInventory();

            if (entity != null) entity.remove();
            if (hologram != null) hologram.remove();
        };

        if (Bukkit.isPrimaryThread()) runnable.run();
        else Scheduler.get().runAt(location, runnable);
    }

    public void removeInventory() {
        closeInventory();

        if (CONFIG.getBoolean("drop-items", true)) {
            for (ItemStack it : gui.getContents()) {
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
        final List<HumanEntity> viewers = new ArrayList<>(gui.getViewers());
        for (HumanEntity viewer : viewers) {
            viewer.closeInventory();
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

    public Inventory getGui() {
        return gui;
    }

    public int getStoredXP() {
        return storedXP;
    }

    public PacketEntity getEntity() {
        return entity;
    }

    public Hologram getHologram() {
        return hologram;
    }

    public String getPlayerName() {
        return playerName;
    }
}
