package com.mitosv.cinematic;

import com.mitosv.cinematic.commands.StartVideoCommand;
import com.mitosv.cinematic.networking.PacketHandler;
import com.mitosv.cinematic.util.FileManager;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.io.File;

@Mod(Cinematic.MODID)
public class Cinematic {
    public static final String MODID = "cinematic";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static Cinematic instance;
    private FileManager fileManager;

    public Cinematic() {
        instance = this;
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Initializing Cinematic network handler...");
        PacketHandler.init();

        File gameDir = FMLPaths.GAMEDIR.get().toFile();
        File cinematicDir = new File(gameDir, "cinematic");
        this.fileManager = new FileManager(cinematicDir);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("Initializing Cinematic client setup...");
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        StartVideoCommand.register(event.getDispatcher());
    }

    public static Cinematic getInstance() {
        return instance;
    }

    public FileManager getFileManager() {
        return fileManager;
    }
}
