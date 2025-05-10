package net.aimless.virtualcurrency.network;

import io.netty.buffer.ByteBuf;
import net.aimless.virtualcurrency.VirtualCurrency;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SyncBalance(int balance) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncBalance> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(VirtualCurrency.MODID, "sync_balance"));

    public static final StreamCodec<ByteBuf, SyncBalance> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SyncBalance::balance,
            SyncBalance::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
