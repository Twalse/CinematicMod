package com.twalse.twmod.cargo;

import com.twalse.twmod.TwMod;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = TwMod.MODID)
public class CargoEventHandler {

    private static final UUID SLOWDOWN_UUID = UUID.fromString("77b31121-8664-4e2b-bb48-8f858e999901");
    private static final AttributeModifier SLOWDOWN_MODIFIER = new AttributeModifier(SLOWDOWN_UUID, "heavy_cargo_slowdown", -0.4D, AttributeModifier.Operation.MULTIPLY_TOTAL);

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }

        Player player = event.player;
        boolean carriesCargo = false;

        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() instanceof HeavyCargoItem) {
                carriesCargo = true;
                break;
            }
        }

        var speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            if (carriesCargo) {
                if (!speedAttr.hasModifier(SLOWDOWN_MODIFIER)) {
                    speedAttr.addTransientModifier(SLOWDOWN_MODIFIER);
                }
            } else {
                if (speedAttr.hasModifier(SLOWDOWN_MODIFIER)) {
                    speedAttr.removeModifier(SLOWDOWN_MODIFIER);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onItemToss(ItemTossEvent event) {
        // Prevent dropping heavy cargo
        if (event.getEntity().getItem().getItem() instanceof HeavyCargoItem) {
            event.setCanceled(true);
            event.getPlayer().getInventory().add(event.getEntity().getItem());
            // TODO: Optional notification message
        }
    }
}
