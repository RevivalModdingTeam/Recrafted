package dev.revivalmoddingteam.recrafted.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.revivalmoddingteam.recrafted.network.NetworkHandler;
import dev.revivalmoddingteam.recrafted.network.client.CPacketToggleDebug;
import dev.revivalmoddingteam.recrafted.world.capability.IWorldCap;
import dev.revivalmoddingteam.recrafted.world.capability.WorldCapFactory;
import dev.revivalmoddingteam.recrafted.world.season.Seasons;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class RecraftedCommand {

    public static void register(CommandDispatcher<CommandSource> commandDispatcher) {
        commandDispatcher.register(Commands.literal("recrafted")
                .requires(src -> src.hasPermissionLevel(4))
                .then(Commands.literal("debug").executes(RecraftedCommand::toggleDebug))
                .then(Commands.literal("season").then(Commands.argument("seasonID", IntegerArgumentType.integer(0, 3)).executes(ctx -> setSeason(ctx, IntegerArgumentType.getInteger(ctx, "seasonID")))))
                .executes(RecraftedCommand::noargExec)
        );
    }

    private static int setSeason(CommandContext<CommandSource> ctx, int seasonID) {
        World world = ctx.getSource().func_197023_e();
        IWorldCap worldCap = WorldCapFactory.getData(world);
        worldCap.getSeasonData().setSeasonID(seasonID, world);
        worldCap.updateClients(world);
        ctx.getSource().sendFeedback(new StringTextComponent("Season has been changed to " + Seasons.REGISTRY[seasonID].getName()), true);
        return seasonID;
    }

    private static int toggleDebug(CommandContext<CommandSource> ctx) {
        if(ctx.getSource().getEntity() instanceof ServerPlayerEntity) {
            NetworkHandler.sendTo((ServerPlayerEntity) ctx.getSource().getEntity(), new CPacketToggleDebug());
            ctx.getSource().sendFeedback(new StringTextComponent("Toggled debug mode"), false);
        }
        return 0;
    }

    private static int noargExec(CommandContext<CommandSource> ctx) {
        ctx.getSource().sendFeedback(new TranslationTextComponent("recrafted.commands.recrafted.missingarg", "Unknown argument!"), true);
        return 0;
    }
}
