package dev.revivalmoddingteam.recrafted.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class RecraftedCommand {

    public static void register(CommandDispatcher<CommandSource> commandDispatcher) {
        LiteralCommandNode<CommandSource> node = commandDispatcher.register(Commands.literal("recrafted")
                .requires(src -> src.hasPermissionLevel(4))
                .executes(ctx -> {
                    ctx.getSource().getEntity().sendMessage(new StringTextComponent("executed"));
                    return 0;
                })
        );
    }
}
