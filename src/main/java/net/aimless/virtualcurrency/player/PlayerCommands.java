package net.aimless.virtualcurrency.player;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import net.aimless.virtualcurrency.api.CurrencyAPI;
import net.aimless.virtualcurrency.component.ModDataComponent;
import net.aimless.virtualcurrency.item.ModItems;
import net.aimless.virtualcurrency.network.SyncBalance;

public class PlayerCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("currency")
                        .then(Commands.literal("add")
                        .requires(source -> source.hasPermission(2)) // Require OP
                                .then(Commands.argument("target", EntityArgument.player())
                                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                                .executes(ctx -> {
                                                    ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
                                                    int amount = IntegerArgumentType.getInteger(ctx, "amount");

                                                    int current = target.getData(PlayerAttachments.CURRENCY.get());
                                                    target.setData(PlayerAttachments.CURRENCY.get(), current + amount);
                                                    PacketDistributor.sendToPlayer(target, new SyncBalance(current + amount));

                                                    ctx.getSource().sendSuccess(
                                                            () -> Component.literal("Added " + amount + " to " + target.getName().getString() +
                                                                    ". New balance: " + (current + amount)),
                                                            false
                                                    );
                                                    return 1;
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("set")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("target", EntityArgument.player())
                                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                                .executes(ctx -> {
                                                    ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
                                                    int amount = IntegerArgumentType.getInteger(ctx, "amount");

                                                    target.setData(PlayerAttachments.CURRENCY.get(), amount);
                                                    PacketDistributor.sendToPlayer(target, new SyncBalance(amount));

                                                    ctx.getSource().sendSuccess(
                                                            () -> Component.literal("Set " + target.getName().getString() + "'s currency to " + amount),
                                                            false
                                                    );
                                                    return 1;
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("get")
                                .then(Commands.argument("target", EntityArgument.player())
                                        .executes(ctx -> {
                                            ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
                                            int amount = target.getData(PlayerAttachments.CURRENCY.get());

                                            ctx.getSource().sendSuccess(
                                                    () -> Component.literal(target.getName().getString() + "'s current balance is " + amount),
                                                    false
                                            );
                                            return 1;
                                        })
                                )
                        )
                        .then(Commands.literal("withdraw")
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                        .executes(ctx -> {
                                            withdrawCurrency(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "amount"));
                                            return 1;
                                        })
                                )
                        )
        );
    }

    public static int withdrawCurrency(CommandSourceStack source, int amount) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Only players can use this command."));
            return Command.SINGLE_SUCCESS;
        }

        int currentBalance = CurrencyAPI.getBalance(player);
        if (currentBalance < amount) {
            source.sendFailure(Component.literal("You do not have enough coins to withdraw " + amount + "."));
            return Command.SINGLE_SUCCESS;
        }

        ItemStack moneyBag = new ItemStack(ModItems.MONEYBAG.get());
        moneyBag.set(ModDataComponent.CURRENCY_AMOUNT.get(), amount);

        if (!player.getInventory().add(moneyBag)) {
            source.sendFailure(Component.literal("Your inventory is full. Could not withdraw the Money."));
            return Command.SINGLE_SUCCESS;
        }

        CurrencyAPI.removeBalance(player, amount); // This now also sends SyncBalance

        // Confirm to player
        source.sendSuccess(() -> Component.literal("You have withdrawn " + amount + " coins."), true);

        return Command.SINGLE_SUCCESS;
    }
}
