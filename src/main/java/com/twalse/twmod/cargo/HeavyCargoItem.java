package com.twalse.twmod.cargo;

import net.minecraft.world.item.Item;

public class HeavyCargoItem extends Item {
    public HeavyCargoItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    // TODO: Custom heavy cargo logic, weight attributes, or lore
}
