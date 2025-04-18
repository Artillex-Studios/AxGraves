package com.artillexstudios.axgraves.listeners;

import com.artillexstudios.axapi.packet.wrapper.serverbound.ServerboundInteractWrapper;
import com.artillexstudios.axgraves.grave.Grave;
import com.artillexstudios.axgraves.grave.SpawnedGraves;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerInteractListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getHand() == null) return;

        ServerboundInteractWrapper.InteractionHand hand = switch (event.getHand()) {
            case HAND -> ServerboundInteractWrapper.InteractionHand.MAIN_HAND;
            case OFF_HAND -> ServerboundInteractWrapper.InteractionHand.OFF_HAND;
            default -> null;
        };
        if (hand == null) return;

        for (Grave grave : SpawnedGraves.getGraves()) {
            if (!grave.getLocation().getBlock().equals(event.getClickedBlock())) continue;
            grave.interact(event.getPlayer(), hand);
            return;
        }
    }
}