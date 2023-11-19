package com.artillexstudios.axgraves.api.events;

import com.artillexstudios.axgraves.grave.Grave;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GraveOpenEvent extends Event implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private final Player opener;
    private final Grave grave;
    private boolean isCancelled = false;

    public GraveOpenEvent(@NotNull Player opener, @NotNull Grave grave) {
        super(!Bukkit.isPrimaryThread());

        this.opener = opener;
        this.grave = grave;
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
    public Player getOpener() {
        return opener;
    }

    @NotNull
    public Grave getGrave() {
        return grave;
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
