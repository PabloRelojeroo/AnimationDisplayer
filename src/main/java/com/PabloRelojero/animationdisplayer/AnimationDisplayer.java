package com.tuusuario.animationdisplayer;

import com.tuusuario.animationdisplayer.commands.DisplayAnimationCommand;
import com.tuusuario.animationdisplayer.network.NetworkHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod("animationdisplayer")
public class AnimationDisplayer {
    public static final String MOD_ID = "animationdisplayer";
    public static final Logger LOGGER = LogManager.getLogger();
    public static File animationsDir;

    public AnimationDisplayer() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Animation Displayer mod is initializing");

        NetworkHandler.init();

        animationsDir = new File("config/animationdisplayer/animations");
        if (!animationsDir.exists()) {
            animationsDir.mkdirs();
        }

        LOGGER.info("Animation directory created at: " + animationsDir.getAbsolutePath());
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        LOGGER.info("Registering mod commands");
        DisplayAnimationCommand.register(event.getDispatcher());
    }
}