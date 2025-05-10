package net.aimless.virtualcurrency.item;

import net.aimless.virtualcurrency.VirtualCurrency;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public class ModItemProperties {
    public static void addCustomItemProperties() {
        ItemProperties.register(ModItems.MONEYBAG.get(), ResourceLocation.fromNamespaceAndPath(VirtualCurrency.MODID, "amount"),
                (itemStack, clientLevel, livingEntity, i) -> 1000);
    }
}
