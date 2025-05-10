package net.aimless.virtualcurrency.item;

import net.aimless.virtualcurrency.api.CurrencyAPI;
import net.aimless.virtualcurrency.component.ModDataComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;

public class MoneyBagItem extends Item {

    public MoneyBagItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            Integer amount = stack.get(ModDataComponent.CURRENCY_AMOUNT.get());

            if (amount != null && amount > 0) {
                // Add currency to the player using your API
                CurrencyAPI.addBalance(serverPlayer, amount);

                // Feedback
                player.displayClientMessage(Component.literal("You received " + amount + " coins!"), true);
                level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 1.0f);

                // Consume the item
                stack.shrink(1);
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if(stack.get(ModDataComponent.CURRENCY_AMOUNT) != null) {
            tooltipComponents.add(Component.literal("Amount: §6" + stack.get(ModDataComponent.CURRENCY_AMOUNT)));
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
