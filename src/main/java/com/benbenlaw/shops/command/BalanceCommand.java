package com.benbenlaw.shops.command;

import com.benbenlaw.shops.capability.ShopsAttachments;
import com.benbenlaw.shops.network.packets.SyncPlayerBalanceToClient;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

public class BalanceCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("player_balance")
                .then(Commands.literal("add")
                        .requires(source -> source.hasPermission(4))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                        .executes(ctx -> addBalance(ctx, EntityArgument.getPlayer(ctx, "player"), IntegerArgumentType.getInteger(ctx, "amount")))
                                )
                        )
                )
                .then(Commands.literal("remove")
                        .requires(source -> source.hasPermission(4))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                        .executes(ctx -> removeBalance(ctx, EntityArgument.getPlayer(ctx, "player"), IntegerArgumentType.getInteger(ctx, "amount")))
                                )
                        )
                )
                .then(Commands.literal("set")
                        .requires(source -> source.hasPermission(4))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                        .executes(ctx -> setBalance(ctx, EntityArgument.getPlayer(ctx, "player"), IntegerArgumentType.getInteger(ctx, "amount")))
                                )
                        )
                )
                .then(Commands.literal("transfer")
                        .then(Commands.argument("to_player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                        .executes(ctx -> transferBalance(ctx, EntityArgument.getPlayer(ctx, "to_player"),  IntegerArgumentType.getInteger(ctx, "amount")))
                                )
                        )
                )
        );
    }

    private static int addBalance(CommandContext<CommandSourceStack> ctx, ServerPlayer player, int amount) {
        player.getData(ShopsAttachments.PLAYER_BALANCE.get()).addBalance(amount);
        ctx.getSource().sendSuccess(
                () -> net.minecraft.network.chat.Component.literal("Added " + amount + " to " + player.getName().getString()), true);

        PacketDistributor.sendToPlayer(player, new SyncPlayerBalanceToClient(player.getData(ShopsAttachments.PLAYER_BALANCE.get()).getBalance()));
        return Command.SINGLE_SUCCESS;
    }

    private static int removeBalance(CommandContext<CommandSourceStack> ctx, ServerPlayer player, int amount) {
        player.getData(ShopsAttachments.PLAYER_BALANCE.get()).subtractBalance(amount);
        ctx.getSource().sendSuccess(
                () -> net.minecraft.network.chat.Component.literal("Removed " + amount + " from " + player.getName().getString()), true);

        PacketDistributor.sendToPlayer(player, new SyncPlayerBalanceToClient(player.getData(ShopsAttachments.PLAYER_BALANCE.get()).getBalance()));
        return Command.SINGLE_SUCCESS;
    }

    private static int setBalance(CommandContext<CommandSourceStack> ctx, ServerPlayer player, int amount) {
        player.getData(ShopsAttachments.PLAYER_BALANCE.get()).setBalance(amount);
        ctx.getSource().sendSuccess(
                () -> net.minecraft.network.chat.Component.literal("Set " + player.getName().getString() + "'s balance to " + amount), true);

        PacketDistributor.sendToPlayer(player, new SyncPlayerBalanceToClient(player.getData(ShopsAttachments.PLAYER_BALANCE.get()).getBalance()));
        return Command.SINGLE_SUCCESS;
    }

    private static int transferBalance(CommandContext<CommandSourceStack> ctx, ServerPlayer toPlayer, int amount) {

       ServerPlayer fromPlayer = ctx.getSource().getPlayer();

        assert fromPlayer != null;
        if (fromPlayer.getData(ShopsAttachments.PLAYER_BALANCE.get()).getBalance() < amount) {
            ctx.getSource().sendFailure(net.minecraft.network.chat.Component.literal(fromPlayer.getName().getString() + " does not have enough balance to transfer " + amount));
            return 0;
        }

        fromPlayer.getData(ShopsAttachments.PLAYER_BALANCE.get()).subtractBalance(amount);
        toPlayer.getData(ShopsAttachments.PLAYER_BALANCE.get()).addBalance(amount);

        ctx.getSource().sendSuccess(
                () -> net.minecraft.network.chat.Component.literal("Transferred " + amount + " from " + fromPlayer.getName().getString() + " to " + toPlayer.getName().getString()), true);

        PacketDistributor.sendToPlayer(fromPlayer, new SyncPlayerBalanceToClient(fromPlayer.getData(ShopsAttachments.PLAYER_BALANCE.get()).getBalance()));
        PacketDistributor.sendToPlayer(toPlayer, new SyncPlayerBalanceToClient(toPlayer.getData(ShopsAttachments.PLAYER_BALANCE.get()).getBalance()));
        return Command.SINGLE_SUCCESS;
    }
}
