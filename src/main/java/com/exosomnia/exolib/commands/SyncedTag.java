package com.exosomnia.exolib.commands;

import com.exosomnia.exolib.networking.PacketHandler;
import com.exosomnia.exolib.networking.packets.TagUpdatePacket;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.CompletableFuture;

public class SyncedTag {

    private static final String[] VALID_OPERATIONS = {"add", "remove"};

    public static void register(CommandDispatcher dispatcher) {
        dispatcher.register(Commands.literal("syncedtag")
                .requires(sourceStack -> sourceStack.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("operation", StringArgumentType.word())
                    .suggests(SyncedTag::suggestOperation)
                    .then(Commands.argument("tag", StringArgumentType.word())
                    .executes(context -> execute(context.getSource(), EntityArgument.getPlayer(context, "player"),
                        StringArgumentType.getString(context, "operation"),
                        StringArgumentType.getString(context, "tag")))))));
    }

    private static CompletableFuture<Suggestions> suggestOperation(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        for (String operation : VALID_OPERATIONS) {
            if (operation.startsWith(builder.getRemaining().toLowerCase())) { builder.suggest(operation); }
        }
        return builder.buildFuture();
    }

    private static int execute(CommandSourceStack sourceStack, ServerPlayer player, String operation, String tag) {
        String playerName = player.getName().getString();
        switch (operation.toLowerCase()) {
            case "add":
                if (player.getTags().add(tag)) {
                    PacketHandler.sendToPlayer(new TagUpdatePacket(tag, true), player);
                    sourceStack.sendSuccess(() -> {
                        return Component.literal(String.format("Successfully applied the tag %1$s to %2$s", tag, playerName));
                    }, false);
                    break;
                }
                sourceStack.sendFailure(Component.literal(String.format("Was unable to apply the tag %1$s to %2$s", tag, playerName)));
                break;
            case "remove":
                if (player.getTags().remove(tag)) {
                    PacketHandler.sendToPlayer(new TagUpdatePacket(tag, false), player);
                    sourceStack.sendSuccess(() -> {
                        return Component.literal(String.format("Successfully removed the tag %1$s from %2$s", tag, playerName));
                    }, false);
                    break;
                }
                sourceStack.sendFailure(Component.literal(String.format("Was unable to remove the tag %1$s from %2$s", tag, playerName)));
                break;
            default:
                sourceStack.sendFailure(Component.literal("Invalid operation specified."));
                break;
        }
        return Command.SINGLE_SUCCESS;
    }
}
