package net.aimless.virtualcurrency.item;

import net.aimless.virtualcurrency.VirtualCurrency;

import net.aimless.virtualcurrency.component.ModDataComponent;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(VirtualCurrency.MODID);

    public static final DeferredItem<Item> MONEYBAG = ITEMS.register("moneybag",
            () -> new MoneyBagItem(new Item.Properties().durability(1).component(ModDataComponent.CURRENCY_AMOUNT, 1000)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
