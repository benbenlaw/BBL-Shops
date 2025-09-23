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
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                        .executes(ctx -> addBalance(ctx, EntityArgument.getPlayer(ctx, "player"), IntegerArgumentType.getInteger(ctx, "amount")))
                                )
                        )
                )
                .then(Commands.literal("remove")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                        .executes(ctx -> removeBalance(ctx, EntityArgument.getPlayer(ctx, "player"), IntegerArgumentType.getInteger(ctx, "amount")))
                                )
                        )
                )
                .then(Commands.literal("set")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                        .executes(ctx -> setBalance(ctx, EntityArgument.getPlayer(ctx, "player"), IntegerArgumentType.getInteger(ctx, "amount")))
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
}
