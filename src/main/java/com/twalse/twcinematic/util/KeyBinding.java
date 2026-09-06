package com.twalse.twcinematic.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;

public class KeyBinding {
    public static final String KEY_CATEGORY = "key.category.twcinematic.cinematic";
    public static final String KEY_EXIT = "key.twcinematic.exit";
    public static final String KEY_OPEN_MENU = "key.twcinematic.open_menu";

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
