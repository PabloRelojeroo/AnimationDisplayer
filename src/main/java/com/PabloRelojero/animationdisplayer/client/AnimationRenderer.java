package com.tuusuario.animationdisplayer.client;

import com.madgag.gif.fmsware.AnimatedGifDecoder;
import com.tuusuario.animationdisplayer.AnimationDisplayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AnimationRenderer {
    private static boolean isPlaying = false;
    private static List<BufferedImage> frames = new ArrayList<>();
    private static int currentFrame = 0;
    private static long lastFrameTime = 0;
    private static final int FRAME_DELAY = 100;
    public static void displayAnimation(String filename) {
        try {
            loadAnimation(filename);
            isPlaying = true;
            currentFrame = 0;
            lastFrameTime = System.currentTimeMillis();

            MinecraftForge.EVENT_BUS.register(AnimationRenderer.class);

        } catch (Exception e) {
            AnimationDisplayer.LOGGER.error("Error al cargar la animación", e);
            Minecraft.getInstance().player.sendMessage(
                    new TextComponent("Error al mostrar la animación: " + e.getMessage()),
                    Minecraft.getInstance().player.getUUID()
            );
        }
    }

    private static void loadAnimation(String filename) throws IOException, JCodecException {
        frames.clear();
        File file = new File(AnimationDisplayer.animationsDir, filename);

        if (filename.toLowerCase().endsWith(".gif")) {
            loadGif(file);
        } else if (filename.toLowerCase().endsWith(".mp4")) {
            loadMp4(file);
        }
    }

    private static void loadGif(File file) throws IOException {
        AnimatedGifDecoder decoder = new AnimatedGifDecoder();
        decoder.read(file.getAbsolutePath());

        int frameCount = decoder.getFrameCount();
        for (int i = 0; i < frameCount; i++) {
            BufferedImage frame = decoder.getFrame(i);
            frames.add(frame);
        }

        AnimationDisplayer.LOGGER.info("Cargados " + frameCount + " frames de GIF");
    }

    private static void loadMp4(File file) throws IOException, JCodecException {
        FrameGrab frameGrab = FrameGrab.createFrameGrab(file.toPath());
        Picture picture;
        int frameCount = 0;

        while ((picture = frameGrab.getNativeFrame()) != null) {
            BufferedImage frame = AWTUtil.toBufferedImage(picture);
            frames.add(frame);
            frameCount++;

            if (frameCount >= 100) break;
        }

        AnimationDisplayer.LOGGER.info("Cargados " + frameCount + " frames de MP4");
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (!isPlaying || frames.isEmpty() || event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime > FRAME_DELAY) {
            currentFrame = (currentFrame + 1) % frames.size();
            lastFrameTime = currentTime;

            if (currentFrame == 0) {
                stop();
                return;
            }
        }

        BufferedImage image = frames.get(currentFrame);



        if (Minecraft.getInstance().options.keyInventory.isDown()) {
            stop();
        }
    }

    private static void stop() {
        isPlaying = false;
        frames.clear();
        MinecraftForge.EVENT_BUS.unregister(AnimationRenderer.class);
    }

    public static class AnimationScreen extends Screen {
        public AnimationScreen() {
            super(new TextComponent("Animation"));
        }

        @Override
        public boolean isPauseScreen() {
            return false;
        }
    }
}