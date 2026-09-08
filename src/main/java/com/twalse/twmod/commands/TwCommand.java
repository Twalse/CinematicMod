package com.twalse.twmod.commands;

import com.twalse.twmod.networking.PacketHandler;
import com.twalse.twmod.networking.message.OpenAdminScreenPacket;
import com.twalse.twmod.quest.PlayerQuestProvider;
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

            // Admin GUI Command
            .then(Commands.literal("admin")
                .executes(ctx -> openAdminGui(ctx.getSource()))
            )

            // Dynamic Variables commands
            .then(Commands.literal("var")
                .then(Commands.literal("add")
                    .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.argument("var_id", StringArgumentType.string())
                            .then(Commands.argument("value", IntegerArgumentType.integer())
                                .executes(ctx -> addVariable(
                                    ctx.getSource(),
                                    EntityArgument.getPlayers(ctx, "targets"),
                                    StringArgumentType.getString(ctx, "var_id"),
                                    IntegerArgumentType.getInteger(ctx, "value")
                                ))
                            )
                        )
                    )
                )
                .then(Commands.literal("set")
                    .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.argument("var_id", StringArgumentType.string())
                            .then(Commands.argument("value", IntegerArgumentType.integer())
                                .executes(ctx -> setVariable(
                                    ctx.getSource(),
                                    EntityArgument.getPlayers(ctx, "targets"),
                                    StringArgumentType.getString(ctx, "var_id"),
                                    IntegerArgumentType.getInteger(ctx, "value")
                                ))
                            )
                        )
                    )
                )
            )

            // Dynamic Quests commands
            .then(Commands.literal("quest")
                .then(Commands.literal("set")
                    .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.argument("quest_id", StringArgumentType.string())
                            .then(Commands.argument("description", StringArgumentType.string())
                                .then(Commands.argument("max_progress", IntegerArgumentType.integer(0))
                                    .executes(ctx -> setQuest(
                                        ctx.getSource(),
                                        EntityArgument.getPlayers(ctx, "targets"),
                                        StringArgumentType.getString(ctx, "quest_id"),
                                        StringArgumentType.getString(ctx, "description"),
                                        IntegerArgumentType.getInteger(ctx, "max_progress")
                                    ))
                                )
                                .executes(ctx -> setQuest(
                                    ctx.getSource(),
                                    EntityArgument.getPlayers(ctx, "targets"),
                                    StringArgumentType.getString(ctx, "quest_id"),
                                    StringArgumentType.getString(ctx, "description"),
                                    0
                                ))
                            )
                        )
                    )
                )
                .then(Commands.literal("add")
                    .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.argument("quest_id", StringArgumentType.string())
                            .then(Commands.argument("amount", IntegerArgumentType.integer())
                                .executes(ctx -> addQuestProgress(
                                    ctx.getSource(),
                                    EntityArgument.getPlayers(ctx, "targets"),
                                    StringArgumentType.getString(ctx, "quest_id"),
                                    IntegerArgumentType.getInteger(ctx, "amount")
                                ))
                            )
                        )
                    )
                )
                .then(Commands.literal("remove")
                    .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.argument("quest_id", StringArgumentType.string())
                            .executes(ctx -> removeQuest(
                                ctx.getSource(),
                                EntityArgument.getPlayers(ctx, "targets"),
                                StringArgumentType.getString(ctx, "quest_id")
                            ))
                        )
                    )
                )
            )
        );
    }

    private static int openAdminGui(CommandSourceStack source) {
        try {
            ServerPlayer player = source.getPlayerOrException();
            PacketHandler.sendToPlayer(new OpenAdminScreenPacket(), player);
            source.sendSuccess(() -> Component.literal("Opened TwMod Quest Admin Screen."), false);
            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("This command can only be executed by a player."));
            return 0;
        }
    }

    private static int addVariable(CommandSourceStack source, Collection<ServerPlayer> targets, String varId, int value) {
        for (ServerPlayer player : targets) {
            player.getCapability(PlayerQuestProvider.PLAYER_QUEST).ifPresent(data -> {
                data.addVariable(varId, value);
                PacketHandler.syncQuestData(player);
            });
        }
        source.sendSuccess(() -> Component.literal("Added " + value + " to variable '" + varId + "' for " + targets.size() + " player(s)."), true);
        return targets.size();
    }

    private static int setVariable(CommandSourceStack source, Collection<ServerPlayer> targets, String varId, int value) {
        for (ServerPlayer player : targets) {
            player.getCapability(PlayerQuestProvider.PLAYER_QUEST).ifPresent(data -> {
                data.setVariable(varId, value);
                PacketHandler.syncQuestData(player);
            });
        }
        source.sendSuccess(() -> Component.literal("Set variable '" + varId + "' to " + value + " for " + targets.size() + " player(s)."), true);
        return targets.size();
    }

    private static int setQuest(CommandSourceStack source, Collection<ServerPlayer> targets, String questId, String description, int maxProgress) {
        for (ServerPlayer player : targets) {
            player.getCapability(PlayerQuestProvider.PLAYER_QUEST).ifPresent(data -> {
                data.setQuest(questId, description, maxProgress);
                PacketHandler.syncQuestData(player);
            });
        }
        source.sendSuccess(() -> Component.literal("Set quest '" + questId + "' ('" + description + "') for " + targets.size() + " player(s)."), true);
        return targets.size();
    }

    private static int addQuestProgress(CommandSourceStack source, Collection<ServerPlayer> targets, String questId, int amount) {
        for (ServerPlayer player : targets) {
            player.getCapability(PlayerQuestProvider.PLAYER_QUEST).ifPresent(data -> {
                data.addQuestProgress(questId, amount);
                PacketHandler.syncQuestData(player);
            });
        }
        source.sendSuccess(() -> Component.literal("Updated quest progress for '" + questId + "' by " + amount + " for " + targets.size() + " player(s)."), true);
        return targets.size();
    }

    private static int removeQuest(CommandSourceStack source, Collection<ServerPlayer> targets, String questId) {
        for (ServerPlayer player : targets) {
            player.getCapability(PlayerQuestProvider.PLAYER_QUEST).ifPresent(data -> {
                data.removeQuest(questId);
                PacketHandler.syncQuestData(player);
            });
        }
        source.sendSuccess(() -> Component.literal("Removed quest '" + questId + "' for " + targets.size() + " player(s)."), true);
        return targets.size();
    }
}
