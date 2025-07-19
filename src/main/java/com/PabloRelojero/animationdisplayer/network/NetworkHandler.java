package com.tuusuario.animationdisplayer.network;

import com.tuusuario.animationdisplayer.AnimationDisplayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(AnimationDisplayer.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        int id = 0;
        CHANNEL.registerMessage(
                id++,
                AnimationPacket.class,
                AnimationPacket::encode,
                AnimationPacket::decode,
                AnimationPacket::handle
        );
    }
}