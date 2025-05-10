package net.aimless.virtualcurrency.client;

import net.aimless.virtualcurrency.VirtualCurrency;
import net.aimless.virtualcurrency.player.PlayerAttachments;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import java.text.NumberFormat;
import java.util.Locale;

@EventBusSubscriber(modid = VirtualCurrency.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class HUDOverlay {

    private static final ResourceLocation GOLDCOIN_ICON = ResourceLocation.fromNamespaceAndPath(
            VirtualCurrency.MODID,
            "textures/gui/goldcoin_icon.png");

    private static int lastBalance = -1;
    private static long changeTime = 0;
    private static int changeAmount = 0;

    // Optional configurable offsets (can later be loaded from a config file)
    private static final int OFFSET_X = 91; // pixels left from center
    private static final int OFFSET_Y = 50; // pixels up from bottom

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (!(mc.player instanceof LocalPlayer player)) return;
        if (mc.options.hideGui || player.isCreative()) return;

        int current = player.getData(PlayerAttachments.CURRENCY.get());

        // Detect balance change
        if (lastBalance != -1 && current != lastBalance) {
            changeAmount = current - lastBalance;
            changeTime = System.currentTimeMillis();
        }

        lastBalance = current;

        // Format currency to (1.000 - 10.000 etc.)
        NumberFormat formatter = NumberFormat.getInstance(Locale.GERMANY);
        String formattedCurrency = formatter.format(current);

        GuiGraphics guiGraphics = event.getGuiGraphics();
        Font font = mc.font;

        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();

        // Anchor to bottom center, offset to right
        int x = screenWidth / 2 - OFFSET_X;
        int y = screenHeight - OFFSET_Y;

        boolean wearingArmor = player.getInventory().armor.stream().anyMatch(stack -> !stack.isEmpty());
        if (wearingArmor) {
            y -= 10;
        }

        guiGraphics.blit(GOLDCOIN_ICON, x, y, 0, 0, 9, 9, 9, 9);

        // Draw current balance
        Component text = Component.literal(formattedCurrency);
        guiGraphics.drawString(font, text, x + 11, y + 1, 0xFFFFFF, true);

        // Floating number on change
        long elapsed = System.currentTimeMillis() - changeTime;
        if (elapsed < 1000 && changeAmount != 0) {
            double progress = elapsed / 1000.0;
            int floatY = y - (int)(progress * 20);
            int color = changeAmount > 0 ? 0x00FF00 : 0xFF5555;
            String prefix = changeAmount > 0 ? "+" : "";
            Component changeText = Component.literal(prefix + changeAmount);
            guiGraphics.drawString(font, changeText, x + 35, floatY, color, true);
        }
    }
}
