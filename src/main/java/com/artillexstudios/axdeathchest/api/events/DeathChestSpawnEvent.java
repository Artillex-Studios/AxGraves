package com.artillexstudios.axdeathchest.api.events;

import com.artillexstudios.axdeathchest.chests.DeathChest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class DeathChestSpawnEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    private final Player player;
    private final DeathChest deathChest;

    public DeathChestSpawnEvent(@NotNull Player player, @NotNull DeathChest deathChest) {
        super(!Bukkit.isPrimaryThread());

        this.player = player;
        this.deathChest = deathChest;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public DeathChest getDeathChest() {
        return deathChest;
    }
}
