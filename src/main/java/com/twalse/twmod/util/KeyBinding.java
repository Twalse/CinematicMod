package com.twalse.twmod.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;

public class KeyBinding {
    public static final String KEY_CATEGORY = "key.category.twmod.general";
    public static final String KEY_EXIT = "key.twmod.exit";
    public static final String KEY_OPEN_MENU = "key.twmod.open_menu";

    public static final KeyMapping EXIT_KEY = new KeyMapping(
            KEY_EXIT,
            KeyConflictContext.GUI,
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_K,
            KEY_CATEGORY
    );

    public static final KeyMapping OPEN_MENU_KEY = new KeyMapping(
            KEY_OPEN_MENU,
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_V,
            KEY_CATEGORY
    );
}
