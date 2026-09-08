package com.twalse.twmod.client.gui;

import com.twalse.twmod.config.HudClientConfig;
import dev.isxander.yacl3.api.Binding;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.awt.Color;

public class ConfigScreen {

    public static Screen createScreen(Screen parent) {
        HudClientConfig config = HudClientConfig.get();

        // Option 1: HUD Position X
        Option<Integer> hudXOption = Option.<Integer>createBuilder()
                .name(Component.literal("HUD Position X"))
                .description(OptionDescription.of(Component.literal("Horizontal offset of the HUD overlay")))
                .binding(Binding.generic(
                        10,
                        () -> config.hudX,
                        val -> config.hudX = val
                ))
                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 1000).step(5))
                .build();

        // Option 2: HUD Position Y
        Option<Integer> hudYOption = Option.<Integer>createBuilder()
                .name(Component.literal("HUD Position Y"))
                .description(OptionDescription.of(Component.literal("Vertical offset of the HUD overlay")))
                .binding(Binding.generic(
                        10,
                        () -> config.hudY,
                        val -> config.hudY = val
                ))
                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 1000).step(5))
                .build();

        // Option 3: HUD Scale
        Option<Float> scaleOption = Option.<Float>createBuilder()
                .name(Component.literal("HUD Scale"))
                .description(OptionDescription.of(Component.literal("Scaling factor for the HUD overlay")))
                .binding(Binding.generic(
                        1.0f,
                        () -> config.hudScale,
                        val -> config.hudScale = val
                ))
                .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0.5f, 3.0f).step(0.1f))
                .build();

        // Option 4: Background Opacity
        Option<Float> opacityOption = Option.<Float>createBuilder()
                .name(Component.literal("Background Opacity"))
                .description(OptionDescription.of(Component.literal("Opacity level for the HUD box background")))
                .binding(Binding.generic(
                        0.5f,
                        () -> config.bgOpacity,
                        val -> config.bgOpacity = val
                ))
                .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0.0f, 1.0f).step(0.05f))
                .build();

        // Option 5: Text Color
        Option<Color> textColorOption = Option.<Color>createBuilder()
                .name(Component.literal("Text Color"))
                .description(OptionDescription.of(Component.literal("Color picker for standard HUD text")))
                .binding(Binding.generic(
                        Color.WHITE,
                        () -> new Color(config.textColor),
                        color -> config.textColor = color.getRGB() & 0xFFFFFF
                ))
                .controller(ColorControllerBuilder::create)
                .build();

        // Option 6: Header Color
        Option<Color> headerColorOption = Option.<Color>createBuilder()
                .name(Component.literal("Header Color"))
                .description(OptionDescription.of(Component.literal("Color picker for section headers (TASKS/STATS)")))
                .binding(Binding.generic(
                        new Color(0xD4AF37),
                        () -> new Color(config.headerColor),
                        color -> config.headerColor = color.getRGB() & 0xFFFFFF
                ))
                .controller(ColorControllerBuilder::create)
                .build();

        // Option 7: Border Color
        Option<Color> borderColorOption = Option.<Color>createBuilder()
                .name(Component.literal("Border Color"))
                .description(OptionDescription.of(Component.literal("Color picker for the HUD box border")))
                .binding(Binding.generic(
                        new Color(0xD4AF37),
                        () -> new Color(config.borderColor),
                        color -> config.borderColor = color.getRGB() & 0xFFFFFF
                ))
                .controller(ColorControllerBuilder::create)
                .build();

        ConfigCategory category = ConfigCategory.createBuilder()
                .name(Component.literal("HUD Settings"))
                .option(hudXOption)
                .option(hudYOption)
                .option(scaleOption)
                .option(opacityOption)
                .option(textColorOption)
                .option(headerColorOption)
                .option(borderColorOption)
                .build();

        YetAnotherConfigLib yacl = YetAnotherConfigLib.createBuilder()
                .title(Component.literal("TwMod Client Settings"))
                .category(category)
                .save(HudClientConfig::save)
                .build();

        return yacl.generateScreen(parent);
    }
}
