package com.tuusuario.animationdisplayer.network;

import com.tuusuario.animationdisplayer.AnimationDisplayer;
import com.tuusuario.animationdisplayer.client.AnimationRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AnimationPacket {
    private final String filename;

    public AnimationPacket(String filename) {
        this.filename = filename;
    }

    public static void encode(AnimationPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUtf(packet.filename);
    }

    public static AnimationPacket decode(FriendlyByteBuf buffer) {
        return new AnimationPacket(buffer.readUtf());
    }

    public static void handle(AnimationPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                AnimationDisplayer.LOGGER.info("Recibido paquete para mostrar animación: " + packet.filename);
                AnimationRenderer.displayAnimation(packet.filename);
            });
        });
        context.setPacketHandled(true);
    }
}