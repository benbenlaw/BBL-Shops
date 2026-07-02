package com.benbenlaw.shops.command;

import com.benbenlaw.shops.attachments.PlayerBalanceData;
import com.benbenlaw.shops.attachments.ShopsAttachments;
import com.benbenlaw.shops.network.packets.SyncPlayerBalanceToClient;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public class BalanceCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("player_balance")

                .then(Commands.literal("add")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx -> addBalance(
                                                ctx,
                                                EntityArgument.getPlayer(ctx, "player"),
                                                IntegerArgumentType.getInteger(ctx, "amount")
                                        ))
                                )
                        )
                )

                .then(Commands.literal("remove")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx -> removeBalance(
                                                ctx,
                                                EntityArgument.getPlayer(ctx, "player"),
                                                IntegerArgumentType.getInteger(ctx, "amount")
                                        ))
                                )
                        )
                )

                .then(Commands.literal("set")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                        .executes(ctx -> setBalance(
                                                ctx,
                                                EntityArgument.getPlayer(ctx, "player"),
                                                IntegerArgumentType.getInteger(ctx, "amount")
                                        ))
                                )
                        )
                )

                .then(Commands.literal("transfer")
                        .then(Commands.argument("to_player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx -> transferBalance(
                                                ctx,
                                                EntityArgument.getPlayer(ctx, "to_player"),
                                                IntegerArgumentType.getInteger(ctx, "amount")
                                        ))
                                )
                        )
                )
        );
    }

    private static int addBalance(CommandContext<CommandSourceStack> ctx, ServerPlayer player, int amount) {
        PlayerBalanceData updated = player.getData(ShopsAttachments.PLAYER_BALANCE.get()).addBalance(amount);
        player.setData(ShopsAttachments.PLAYER_BALANCE.get(), updated);

        ctx.getSource().sendSuccess(
                () -> Component.literal("Added " + amount + " to " + player.getName().getString()),
                true
        );

        PacketDistributor.sendToPlayer(player, new SyncPlayerBalanceToClient(updated.getBalance()));

        return Command.SINGLE_SUCCESS;
    }

    private static int removeBalance(CommandContext<CommandSourceStack> ctx, ServerPlayer player, int amount) {
        PlayerBalanceData current = player.getData(ShopsAttachments.PLAYER_BALANCE.get());

        if (current.getBalance() < amount) {
            ctx.getSource().sendFailure(
                    Component.literal(player.getName().getString() + " does not have enough balance.")
            );
            return 0;
        }

        PlayerBalanceData updated = current.subtractBalance(amount);
        player.setData(ShopsAttachments.PLAYER_BALANCE.get(), updated);

        ctx.getSource().sendSuccess(
                () -> Component.literal("Removed " + amount + " from " + player.getName().getString()),
                true
        );

        PacketDistributor.sendToPlayer(player, new SyncPlayerBalanceToClient(updated.getBalance()));

        return Command.SINGLE_SUCCESS;
    }

    private static int setBalance(CommandContext<CommandSourceStack> ctx, ServerPlayer player, int amount) {
        PlayerBalanceData updated = player.getData(ShopsAttachments.PLAYER_BALANCE.get()).setBalance(amount);
        player.setData(ShopsAttachments.PLAYER_BALANCE.get(), updated);

        ctx.getSource().sendSuccess(
                () -> Component.literal("Set " + player.getName().getString() + "'s balance to " + amount),
                true
        );

        PacketDistributor.sendToPlayer(player, new SyncPlayerBalanceToClient(updated.getBalance()));

        return Command.SINGLE_SUCCESS;
    }

    private static int transferBalance(CommandContext<CommandSourceStack> ctx, ServerPlayer toPlayer, int amount) {
        ServerPlayer fromPlayer = ctx.getSource().getPlayer();

        if (fromPlayer == null) return 0;

        if (fromPlayer.equals(toPlayer)) {
            ctx.getSource().sendFailure(Component.literal("You cannot transfer balance to yourself."));
            return 0;
        }

        PlayerBalanceData fromData = fromPlayer.getData(ShopsAttachments.PLAYER_BALANCE.get());

        if (fromData.getBalance() < amount) {
            ctx.getSource().sendFailure(Component.literal("You do not have enough balance to transfer " + amount));
            return 0;
        }

        PlayerBalanceData fromUpdated = fromData.subtractBalance(amount);
        PlayerBalanceData toUpdated = toPlayer.getData(ShopsAttachments.PLAYER_BALANCE.get()).addBalance(amount);

        fromPlayer.setData(ShopsAttachments.PLAYER_BALANCE.get(), fromUpdated);
        toPlayer.setData(ShopsAttachments.PLAYER_BALANCE.get(), toUpdated);

        ctx.getSource().sendSuccess(
                () -> Component.literal("Transferred " + amount + " from " +
                        fromPlayer.getName().getString() + " to " + toPlayer.getName().getString()),
                true
        );

        PacketDistributor.sendToPlayer(fromPlayer, new SyncPlayerBalanceToClient(fromUpdated.getBalance()));
        PacketDistributor.sendToPlayer(toPlayer, new SyncPlayerBalanceToClient(toUpdated.getBalance()));

        return Command.SINGLE_SUCCESS;
    }
}