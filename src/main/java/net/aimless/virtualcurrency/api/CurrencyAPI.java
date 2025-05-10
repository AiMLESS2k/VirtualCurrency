package net.aimless.virtualcurrency.api;

import net.aimless.virtualcurrency.network.SyncBalance;
import net.aimless.virtualcurrency.player.PlayerAttachments;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public class CurrencyAPI {

    public static int getBalance(ServerPlayer player) {
        return player.getData(PlayerAttachments.CURRENCY.get());
    }

    public static void addBalance(ServerPlayer player, int amount) {
        int current = getBalance(player);
        int newBalance = current + amount;
        setBalance(player, newBalance);
        player.displayClientMessage(Component.literal("You received " + amount + " coins!"), true);
    }

    public static void removeBalance(ServerPlayer player, int amount) {
        int current = getBalance(player);
        int newBalance = Math.max(0, current - amount);
        player.displayClientMessage(Component.literal("You removed " + amount + " coins!"), true);
        setBalance(player, newBalance);
    }

    public static void setBalance(ServerPlayer player, int amount) {
        player.setData(PlayerAttachments.CURRENCY.get(), amount);
        PacketDistributor.sendToPlayer(player, new SyncBalance(amount));
    }
}
