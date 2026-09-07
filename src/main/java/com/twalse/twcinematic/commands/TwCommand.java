package com.twalse.twcinematic.commands;

import com.twalse.twcinematic.networking.PacketHandler;
import com.twalse.twcinematic.quest.PlayerQuestProvider;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class TwCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("tw")
            .requires(source -> source.hasPermission(2))

            // Mission commands
            .then(Commands.literal("mission")
                .then(Commands.literal("set")
                    .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.argument("objective", StringArgumentType.string())
                            .then(Commands.argument("maxProgress", IntegerArgumentType.integer(0))
                                .executes(ctx -> setMission(
                                    ctx.getSource(),
                                    EntityArgument.getPlayers(ctx, "targets"),
                                    StringArgumentType.getString(ctx, "objective"),
                                    IntegerArgumentType.getInteger(ctx, "maxProgress")
                                ))
                            )
                            .executes(ctx -> setMission(
                                ctx.getSource(),
                                EntityArgument.getPlayers(ctx, "targets"),
                                StringArgumentType.getString(ctx, "objective"),
                                0
                            ))
                        )
                    )
                )
                .then(Commands.literal("progress")
                    .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                            .executes(ctx -> addProgress(
                                ctx.getSource(),
                                EntityArgument.getPlayers(ctx, "targets"),
                                IntegerArgumentType.getInteger(ctx, "amount")
                            ))
                        )
                    )
                )
                .then(Commands.literal("clear")
                    .then(Commands.argument("targets", EntityArgument.players())
                        .executes(ctx -> clearMission(
                            ctx.getSource(),
                            EntityArgument.getPlayers(ctx, "targets")
                        ))
                    )
                )
            )

            // Money commands
            .then(Commands.literal("money")
                .then(Commands.literal("give")
                    .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                            .executes(ctx -> giveMoney(
                                ctx.getSource(),
                                EntityArgument.getPlayers(ctx, "targets"),
                                IntegerArgumentType.getInteger(ctx, "amount")
                            ))
                        )
                    )
                )
                .then(Commands.literal("set")
                    .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                            .executes(ctx -> setMoney(
                                ctx.getSource(),
                                EntityArgument.getPlayers(ctx, "targets"),
                                IntegerArgumentType.getInteger(ctx, "amount")
                            ))
                        )
                    )
                )
            )
        );
    }

    private static int setMission(CommandSourceStack source, Collection<ServerPlayer> targets, String objective, int maxProgress) {
        for (ServerPlayer player : targets) {
            player.getCapability(PlayerQuestProvider.PLAYER_QUEST).ifPresent(data -> {
                data.setCurrentObjective(objective);
                data.setObjectiveProgress(0);
                data.setObjectiveMax(maxProgress);
                PacketHandler.syncQuestData(player);
            });
        }
        source.sendSuccess(() -> Component.literal("Set mission '" + objective + "' for " + targets.size() + " player(s)."), true);
        return targets.size();
    }

    private static int addProgress(CommandSourceStack source, Collection<ServerPlayer> targets, int amount) {
        for (ServerPlayer player : targets) {
            player.getCapability(PlayerQuestProvider.PLAYER_QUEST).ifPresent(data -> {
                data.addObjectiveProgress(amount);
                PacketHandler.syncQuestData(player);
            });
        }
        source.sendSuccess(() -> Component.literal("Updated mission progress by " + amount + " for " + targets.size() + " player(s)."), true);
        return targets.size();
    }

    private static int clearMission(CommandSourceStack source, Collection<ServerPlayer> targets) {
        for (ServerPlayer player : targets) {
            player.getCapability(PlayerQuestProvider.PLAYER_QUEST).ifPresent(data -> {
                data.setCurrentObjective("");
                data.setObjectiveProgress(0);
                data.setObjectiveMax(0);
                PacketHandler.syncQuestData(player);
            });
        }
        source.sendSuccess(() -> Component.literal("Cleared mission for " + targets.size() + " player(s)."), true);
        return targets.size();
    }

    private static int giveMoney(CommandSourceStack source, Collection<ServerPlayer> targets, int amount) {
        for (ServerPlayer player : targets) {
            player.getCapability(PlayerQuestProvider.PLAYER_QUEST).ifPresent(data -> {
                data.addMoney(amount);
                PacketHandler.syncQuestData(player);
            });
        }
        source.sendSuccess(() -> Component.literal("Gave " + amount + " money to " + targets.size() + " player(s)."), true);
        return targets.size();
    }

    private static int setMoney(CommandSourceStack source, Collection<ServerPlayer> targets, int amount) {
        for (ServerPlayer player : targets) {
            player.getCapability(PlayerQuestProvider.PLAYER_QUEST).ifPresent(data -> {
                data.setMoney(amount);
                PacketHandler.syncQuestData(player);
            });
        }
        source.sendSuccess(() -> Component.literal("Set money to " + amount + " for " + targets.size() + " player(s)."), true);
        return targets.size();
    }
}
