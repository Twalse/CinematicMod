package com.twalse.twmod.block;

import com.twalse.twmod.TwMod;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, TwMod.MODID);

    public static final RegistryObject<MenuType<GeneratorMenu>> GENERATOR_MENU = MENUS.register("generator_menu",
            () -> IForgeMenuType.create((windowId, inv, data) -> new GeneratorMenu(windowId, inv, data)));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
