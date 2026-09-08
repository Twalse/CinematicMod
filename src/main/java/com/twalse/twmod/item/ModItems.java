package com.twalse.twmod.item;

import com.twalse.twmod.TwMod;
import com.twalse.twmod.cargo.HeavyCargoItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TwMod.MODID);

    public static final RegistryObject<Item> SMARTPHONE = ITEMS.register("smartphone",
            () -> new SmartphoneItem(new Item.Properties()));

    public static final RegistryObject<Item> HEAVY_CARGO = ITEMS.register("heavy_cargo",
            () -> new HeavyCargoItem(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
