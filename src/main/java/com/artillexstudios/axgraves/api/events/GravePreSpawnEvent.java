package com.artillexstudios.axgraves.api.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GravePreSpawnEvent extends Event implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private final Player player;
    private final Location location;
    private boolean isCancelled = false;

    public GravePreSpawnEvent(@NotNull Player player, @NotNull Location location) {
        super(!Bukkit.isPrimaryThread());

        this.player = player;
        this.location = location;
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
    public Location getLocation() {
        return location;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }
}
