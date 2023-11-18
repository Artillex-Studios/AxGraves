package com.artillexstudios.axdeathchest.api.events;

import com.artillexstudios.axdeathchest.chests.DeathChest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class DeathChestPreSpawnEvent extends Event implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private final Player player;
    private final DeathChest deathChest;
    private boolean isCancelled = false;

    public DeathChestPreSpawnEvent(@NotNull Player player, @NotNull DeathChest deathChest) {
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

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }
}
