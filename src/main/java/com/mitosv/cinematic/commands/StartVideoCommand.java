package com.mitosv.cinematic.commands;

import com.mitosv.cinematic.networking.PacketHandler;
import com.mitosv.cinematic.networking.message.SendVideoPlayer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.Collections;

public class StartVideoCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("cinematic")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("play")
                .then(Commands.argument("filename", StringArgumentType.string())
                    .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.argument("volume", IntegerArgumentType.integer(0, 100))
                            .executes(ctx -> execute(
                                ctx.getSource(),
                                StringArgumentType.getString(ctx, "filename"),
                                EntityArgument.getPlayers(ctx, "targets"),
                                IntegerArgumentType.getInteger(ctx, "volume")
                            ))
                        )
                        .executes(ctx -> execute(
                            ctx.getSource(),
                            StringArgumentType.getString(ctx, "filename"),
                            EntityArgument.getPlayers(ctx, "targets"),
                            100
                        ))
                    )
                    .executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        return execute(
                            ctx.getSource(),
                            StringArgumentType.getString(ctx, "filename"),
                            Collections.singletonList(player),
                            100
                        );
                    })
                )
            )
        );
    }

    private static int execute(CommandSourceStack source, String fileName, Collection<ServerPlayer> targets, int volume) {
        SendVideoPlayer packet = new SendVideoPlayer(fileName, volume);
        for (ServerPlayer player : targets) {
            PacketHandler.sendToPlayer(packet, player);
        }
        source.sendSuccess(() -> Component.literal("Playing cinematic '" + fileName + "' for " + targets.size() + " player(s)."), true);
        return targets.size();
    }
}
