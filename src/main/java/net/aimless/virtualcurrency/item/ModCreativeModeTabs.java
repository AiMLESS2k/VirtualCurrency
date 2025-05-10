package net.aimless.virtualcurrency.item;

import net.aimless.virtualcurrency.VirtualCurrency;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, VirtualCurrency.MODID);

    public static final Supplier<CreativeModeTab> VIRTUALCURRENCY_ITEMS_TAB = CREATIVE_MODE_TAB.register("virtualcurrency_items_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.MONEYBAG.get()))
                    .title(Component.translatable("creative.virtualcurrency.items"))
                    .displayItems((itemDisplayParameters, output) -> {
                        // Custom Items goes here
                        output.accept(ModItems.MONEYBAG);
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
