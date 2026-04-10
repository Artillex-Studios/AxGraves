package com.artillexstudios.axgraves.utils;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {
    private static Economy economy = null;

    public static boolean setup() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        economy = rsp.getProvider();
        return economy != null;
    }

    public static boolean isEnabled() {
        return economy != null;
    }

    public static boolean hasBalance(Player player, double amount) {
        return economy.has(player, amount);
    }

    public static void withdraw(Player player, double amount) {
        economy.withdrawPlayer(player, amount);
    }
}
