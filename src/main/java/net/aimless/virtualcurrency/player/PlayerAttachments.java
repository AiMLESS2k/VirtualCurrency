package net.aimless.virtualcurrency.player;

import com.mojang.serialization.Codec;
import net.aimless.virtualcurrency.VirtualCurrency;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class PlayerAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, VirtualCurrency.MODID);

    public static final Supplier<AttachmentType<Integer>> CURRENCY = ATTACHMENT_TYPES.register(
            "currency",
            () -> AttachmentType.builder(() -> 5000)
                    .serialize(Codec.INT)
                    .copyOnDeath()
                    .build()
    );
}
