package net.aimless.virtualcurrency;

import net.aimless.virtualcurrency.component.ModDataComponent;
import net.aimless.virtualcurrency.item.ModCreativeModeTabs;
import net.aimless.virtualcurrency.item.ModItemProperties;
import net.aimless.virtualcurrency.item.ModItems;
import net.aimless.virtualcurrency.network.SyncBalance;
import net.aimless.virtualcurrency.player.PlayerAttachments;
import net.aimless.virtualcurrency.player.PlayerCommands;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(VirtualCurrency.MODID)
public class VirtualCurrency
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "virtualcurrency";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public VirtualCurrency(IEventBus modEventBus, ModContainer modContainer)
    {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerPayloadHandlers);
        modEventBus.addListener(this::addCreative);
        ModCreativeModeTabs.register(modEventBus);

        PlayerAttachments.ATTACHMENT_TYPES.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);

        /*modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);*/

        ModDataComponent.register(modEventBus);
        ModItems.register(modEventBus);
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        PlayerCommands.register(event.getDispatcher());
        LOGGER.info("Commands registered.");
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.OP_BLOCKS) {
            event.accept(ModItems.MONEYBAG);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {}

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            ModItemProperties.addCustomItemProperties();
        }
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;

        int balance = serverPlayer.getData(PlayerAttachments.CURRENCY.get());
        PacketDistributor.sendToPlayer(serverPlayer, new SyncBalance(balance));
    }

    private void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(VirtualCurrency.MODID);

        registrar.playToClient(
                SyncBalance.TYPE,
                SyncBalance.STREAM_CODEC,
                (payload, context) -> {
                    Minecraft.getInstance().execute(() -> {
                        if (Minecraft.getInstance().player != null) {
                            Minecraft.getInstance().player.setData(PlayerAttachments.CURRENCY.get(), payload.balance());
                        }
                    });
                }
        );
    }

}
