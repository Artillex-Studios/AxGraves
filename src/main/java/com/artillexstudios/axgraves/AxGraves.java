package com.artillexstudios.axgraves;

import com.artillexstudios.axapi.AxPlugin;
import com.artillexstudios.axapi.utils.featureflags.FeatureFlags;
import com.artillexstudios.axgraves.commands.Commands;
import com.artillexstudios.axgraves.config.Config;
import com.artillexstudios.axgraves.config.Lang;
import com.artillexstudios.axgraves.grave.Grave;
import com.artillexstudios.axgraves.grave.SpawnedGraves;
import com.artillexstudios.axgraves.listeners.DeathListener;
import com.artillexstudios.axgraves.listeners.PlayerInteractListener;
import com.artillexstudios.axgraves.schedulers.SaveGraves;
import com.artillexstudios.axgraves.schedulers.TickGraves;
import com.artillexstudios.axgraves.utils.UpdateNotifier;
import org.bstats.bukkit.Metrics;
import revxrsal.commands.bukkit.BukkitCommandHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public final class AxGraves extends AxPlugin {
    private static AxPlugin instance;
    public static ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor();

    public static AxPlugin getInstance() {
        return instance;
    }

    public void enable() {
        instance = this;

        int pluginId = 20332;
        new Metrics(this, pluginId);

        Config.setup(getDataFolder().toPath().resolve("config.yml"));
        Lang.setup(getDataFolder().toPath().resolve("messages.yml"));

        getServer().getPluginManager().registerEvents(new DeathListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);

        final BukkitCommandHandler handler = BukkitCommandHandler.create(instance);
        handler.register(new Commands());

        if (Config.SaveGraves.enabled)
            SpawnedGraves.loadFromFile();

        TickGraves.start();
        SaveGraves.start();

        if (Config.UpdateNotifier.enabled) new UpdateNotifier(this, 5076);
    }

    public void disable() {
        TickGraves.stop();
        SaveGraves.stop();

        for (Grave grave : SpawnedGraves.getGraves()) {
            if (!Config.SaveGraves.enabled)
                grave.remove();

            if (grave.getEntity() != null)
                grave.getEntity().remove();
            if (grave.getHologram() != null)
                grave.getHologram().remove();
        }

        if (Config.SaveGraves.enabled)
            SpawnedGraves.saveToFile();

        EXECUTOR.shutdown();
    }

    public void updateFlags(FeatureFlags flags) {
        flags.USE_LEGACY_HEX_FORMATTER.set(true);
        flags.PACKET_ENTITY_TRACKER_ENABLED.set(true);
        flags.HOLOGRAM_UPDATE_TICKS.set(5L);
    }
}
