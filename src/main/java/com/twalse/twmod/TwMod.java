package com.twalse.twmod;

import com.twalse.twmod.commands.StartVideoCommand;
import com.twalse.twmod.commands.TwCommand;
import com.twalse.twmod.config.CinematicConfig;
import com.twalse.twmod.config.HudClientConfig;
import com.twalse.twmod.networking.PacketHandler;
import com.twalse.twmod.util.FileManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;

@Mod(TwMod.MODID)
public class TwMod {
    public static final String MODID = "twmod";
    public static final Logger LOGGER = LogManager.getLogger("twmod");
    private static TwMod instance;
    private FileManager fileManager;

    public TwMod() {
        instance = this;
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CinematicConfig.CLIENT_SPEC);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Initializing TwMod network handler...");
        PacketHandler.init();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("Initializing TwMod client setup...");
        event.enqueueWork(() -> {
            HudClientConfig.load();
            String path = CinematicConfig.VIDEO_FOLDER_PATH.get();
            File folder = new File(path);
            if (!folder.isAbsolute()) {
                folder = new File(FMLPaths.GAMEDIR.get().toFile(), path);
            }
            this.fileManager = new FileManager(folder);
        });
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        StartVideoCommand.register(event.getDispatcher());
        TwCommand.register(event.getDispatcher());
    }

    public static TwMod getInstance() {
        return instance;
    }

    public FileManager getFileManager() {
        return fileManager;
    }
}
