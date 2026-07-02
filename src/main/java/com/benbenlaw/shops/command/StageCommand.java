package com.benbenlaw.shops.command;

import com.benbenlaw.shops.attachments.PlayerBalanceData;
import com.benbenlaw.shops.attachments.ShopsAttachments;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class StageCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("player_stage")

                // ADD
                .then(Commands.literal("add")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("stage", StringArgumentType.word())
                                        .executes(ctx -> addStage(
                                                ctx,
                                                EntityArgument.getPlayer(ctx, "player"),
                                                StringArgumentType.getString(ctx, "stage")
                                        ))
                                )
                        )
                )

                // REMOVE
                .then(Commands.literal("remove")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("stage", StringArgumentType.word())
                                        .executes(ctx -> removeStage(
                                                ctx,
                                                EntityArgument.getPlayer(ctx, "player"),
                                                StringArgumentType.getString(ctx, "stage")
                                        ))
                                )
                        )
                )

                // LIST
                .then(Commands.literal("list")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ctx -> listStages(
                                        ctx,
                                        EntityArgument.getPlayer(ctx, "player")
                                ))
                        )
                )
        );
    }

    private static int addStage(CommandContext<CommandSourceStack> ctx, ServerPlayer player, String stage) {
        PlayerBalanceData current = player.getData(ShopsAttachments.PLAYER_BALANCE.get());

        if (current.hasStage(stage)) {
            ctx.getSource().sendFailure(
                    Component.literal(player.getName().getString() + " already has stage \"" + stage + "\".")
            );
            return 0;
        }

        PlayerBalanceData updated = current.addStage(stage);
        player.setData(ShopsAttachments.PLAYER_BALANCE.get(), updated);

        ctx.getSource().sendSuccess(
                () -> Component.literal("Added stage \"" + stage + "\" to " + player.getName().getString()),
                true
        );

        return Command.SINGLE_SUCCESS;
    }

    private static int removeStage(CommandContext<CommandSourceStack> ctx, ServerPlayer player, String stage) {
        PlayerBalanceData current = player.getData(ShopsAttachments.PLAYER_BALANCE.get());

        if (!current.hasStage(stage)) {
            ctx.getSource().sendFailure(
                    Component.literal(player.getName().getString() + " does not have stage \"" + stage + "\".")
            );
            return 0;
        }

        PlayerBalanceData updated = current.removeStage(stage);
        player.setData(ShopsAttachments.PLAYER_BALANCE.get(), updated);

        ctx.getSource().sendSuccess(
                () -> Component.literal("Removed stage \"" + stage + "\" from " + player.getName().getString()),
                true
        );

        return Command.SINGLE_SUCCESS;
    }

    private static int listStages(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {
        String[] stages = player.getData(ShopsAttachments.PLAYER_BALANCE.get()).getStages();

        String joined = stages.length == 0 ? "(none)" : String.join(", ", stages);

        ctx.getSource().sendSuccess(
                () -> Component.literal(player.getName().getString() + "'s stages: " + joined),
                false
        );

        return Command.SINGLE_SUCCESS;
    }
}