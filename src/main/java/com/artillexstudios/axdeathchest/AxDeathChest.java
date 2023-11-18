package com.artillexstudios.axdeathchest;

import com.artillexstudios.axapi.AxPlugin;
import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.dvs.versioning.BasicVersioning;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.dumper.DumperSettings;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.general.GeneralSettings;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.loader.LoaderSettings;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.updater.UpdaterSettings;
import com.artillexstudios.axapi.utils.MessageUtils;
import com.artillexstudios.axdeathchest.chests.DeathChest;
import com.artillexstudios.axdeathchest.chests.SpawnedChests;
import com.artillexstudios.axdeathchest.commands.Commands;
import com.artillexstudios.axdeathchest.listeners.DeathListener;
import com.artillexstudios.axdeathchest.schedulers.TickChests;
import org.bstats.bukkit.Metrics;
import revxrsal.commands.bukkit.BukkitCommandHandler;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class AxDeathChest extends AxPlugin {
    private static AxPlugin instance;
    public static Config CONFIG;
    public static Config MESSAGES;
    public static MessageUtils MESSAGEUTILS;
    public static ExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor();

    public static AxPlugin getInstance() {
        return instance;
    }

    public void enable() {
        instance = this;

        int pluginId = 20325;
        new Metrics(this, pluginId);

        CONFIG = new Config(new File(getDataFolder(), "config.yml"), getResource("config.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.DEFAULT, DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("version")).build());
        MESSAGES = new Config(new File(getDataFolder(), "messages.yml"), getResource("messages.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.DEFAULT, DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("version")).build());

        MESSAGEUTILS = new MessageUtils(MESSAGES.getBackingDocument(), "prefix", CONFIG.getBackingDocument());

        getServer().getPluginManager().registerEvents(new DeathListener(), this);

        final BukkitCommandHandler handler = BukkitCommandHandler.create(this);

        handler.register(new Commands());
        handler.registerBrigadier();

        new TickChests().start();
    }

    public void disable() {
        for (DeathChest deathChest : SpawnedChests.getChests()) {
            deathChest.removeInventory();
            deathChest.getEntity().remove();
            deathChest.getHologram().remove();
        }
    }
}
