package com.tuusuario.animationdisplayer.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tuusuario.animationdisplayer.AnimationDisplayer;
import com.tuusuario.animationdisplayer.network.AnimationPacket;
import com.tuusuario.animationdisplayer.network.NetworkHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

import java.io.File;

public class DisplayAnimationCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("displayanimation")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("filename", StringArgumentType.word())
                                .executes(context -> executeCommand(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "filename")
                                ))
                        )
        );
    }

    private static int executeCommand(CommandSourceStack source, String filename) {
        File animationFile = new File(AnimationDisplayer.animationsDir, filename);
        if (!animationFile.exists()) {
            source.sendFailure(new TextComponent("La animación '" + filename + "' no existe"));
            return 0;
        }

        String extension = getFileExtension(filename);
        if (!extension.equalsIgnoreCase("gif") && !extension.equalsIgnoreCase("mp4")) {
            source.sendFailure(new TextComponent("El archivo debe ser un GIF o MP4"));
            return 0;
        }

        try {
            NetworkHandler.CHANNEL.send(
                    PacketDistributor.ALL.noArg(),
                    new AnimationPacket(filename)
            );

            source.sendSuccess(new TextComponent("Mostrando animación '" + filename + "' a todos los jugadores"), true);
            return 1;
        } catch (Exception e) {
            AnimationDisplayer.LOGGER.error("Error al mostrar la animación", e);
            source.sendFailure(new TextComponent("Error al mostrar la animación: " + e.getMessage()));
            return 0;
        }
    }

    private static String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex > 0) {
            return filename.substring(lastDotIndex + 1);
        }
        return "";
    }
}