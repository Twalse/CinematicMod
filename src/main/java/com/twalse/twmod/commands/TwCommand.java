package com.twalse.twmod.commands;

import com.twalse.twmod.networking.PacketHandler;
import com.twalse.twmod.networking.message.OpenAdminScreenPacket;
import com.twalse.twmod.quest.PlayerQuestProvider;
import com.twalse.twmod.quest.WaypointData;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
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
                .then(Commands.literal("remove")
                    .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.argument("var_id", StringArgumentType.string())
                            .executes(ctx -> removeVariable(
                                ctx.getSource(),
                                EntityArgument.getPlayers(ctx, "targets"),
                                StringArgumentType.getString(ctx, "var_id")
                            ))
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

            // 3D Waypoints commands
            .then(Commands.literal("waypoint")
                .then(Commands.literal("add")
                    .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.argument("id", StringArgumentType.string())
                            .then(Commands.argument("x", DoubleArgumentType.doubleArg())
                                .then(Commands.argument("y", DoubleArgumentType.doubleArg())
                                    .then(Commands.argument("z", DoubleArgumentType.doubleArg())
                                        .then(Commands.argument("colorHex", StringArgumentType.string())
                                            .then(Commands.argument("name", StringArgumentType.string())
                                                .executes(ctx -> addWaypoint(
                                                    ctx.getSource(),
                                                    EntityArgument.getPlayers(ctx, "targets"),
                                                    StringArgumentType.getString(ctx, "id"),
                                                    DoubleArgumentType.getDouble(ctx, "x"),
                                                    DoubleArgumentType.getDouble(ctx, "y"),
                                                    DoubleArgumentType.getDouble(ctx, "z"),
                                                    StringArgumentType.getString(ctx, "colorHex"),
                                                    StringArgumentType.getString(ctx, "name")
                                                ))
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
                .then(Commands.literal("remove")
                    .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.argument("id", StringArgumentType.string())
                            .executes(ctx -> removeWaypoint(
                                ctx.getSource(),
                                EntityArgument.getPlayers(ctx, "targets"),
                                StringArgumentType.getString(ctx, "id")
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

    private static int removeVariable(CommandSourceStack source, Collection<ServerPlayer> targets, String varId) {
        for (ServerPlayer player : targets) {
            player.getCapability(PlayerQuestProvider.PLAYER_QUEST).ifPresent(data -> {
                data.removeVariable(varId);
                PacketHandler.syncQuestData(player);
            });
        }
        source.sendSuccess(() -> Component.literal("Removed variable '" + varId + "' for " + targets.size() + " player(s)."), true);
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

    private static int addWaypoint(CommandSourceStack source, Collection<ServerPlayer> targets, String id, double x, double y, double z, String colorHex, String name) {
        int color = 0xFFFFFF;
        try {
            String cleanHex = colorHex.replace("#", "").replace("0x", "");
            color = (int) Long.parseLong(cleanHex, 16);
        } catch (Exception ignored) {}

        final int finalColor = color;
        WaypointData waypoint = new WaypointData(id, x, y, z, finalColor, name);

        for (ServerPlayer player : targets) {
            player.getCapability(PlayerQuestProvider.PLAYER_QUEST).ifPresent(data -> {
                data.addWaypoint(waypoint);
                PacketHandler.syncQuestData(player);
            });
        }
        source.sendSuccess(() -> Component.literal("Added 3D waypoint '" + name + "' (ID: " + id + ") for " + targets.size() + " player(s)."), true);
        return targets.size();
    }

    private static int removeWaypoint(CommandSourceStack source, Collection<ServerPlayer> targets, String id) {
        for (ServerPlayer player : targets) {
            player.getCapability(PlayerQuestProvider.PLAYER_QUEST).ifPresent(data -> {
                data.removeWaypoint(id);
                PacketHandler.syncQuestData(player);
            });
        }
        source.sendSuccess(() -> Component.literal("Removed 3D waypoint '" + id + "' for " + targets.size() + " player(s)."), true);
        return targets.size();
    }
}
