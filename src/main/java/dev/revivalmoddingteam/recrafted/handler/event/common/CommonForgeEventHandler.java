package dev.revivalmoddingteam.recrafted.handler.event.common;

import dev.revivalmoddingteam.recrafted.Recrafted;
import dev.revivalmoddingteam.recrafted.common.command.RecraftedCommand;
import dev.revivalmoddingteam.recrafted.common.entity.RecraftedItemEntity;
import dev.revivalmoddingteam.recrafted.handler.event.Action;
import dev.revivalmoddingteam.recrafted.network.NetworkHandler;
import dev.revivalmoddingteam.recrafted.network.client.CPacketSyncWorldData;
import dev.revivalmoddingteam.recrafted.player.PlayerCapProvider;
import dev.revivalmoddingteam.recrafted.util.helper.TemperatureHelper;
import dev.revivalmoddingteam.recrafted.world.capability.WorldCapFactory;
import dev.revivalmoddingteam.recrafted.world.capability.WorldCapProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Recrafted.MODID)
public class CommonForgeEventHandler {

    //@SubscribeEvent
    public static void entitySpawn(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        if(entity instanceof ItemEntity) {
            if(entity.world.isRemote) return;
            RecraftedItemEntity recraftedItemEntity = new RecraftedItemEntity(entity.world, entity.posX, entity.posY, entity.posZ, ((ItemEntity) entity).getItem().copy());
            recraftedItemEntity.setMotion(entity.getMotion());
            event.getWorld().addEntity(recraftedItemEntity);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        NetworkHandler.sendTo(player, new CPacketSyncWorldData(WorldCapFactory.getData(player.world).serializeNBT()));
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        WorldCapFactory.getData(event.world).tickWorld(event);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if(event.phase == TickEvent.Phase.END) {
            updateScheduler();
            PlayerEntity entity = event.player;
            if(!entity.world.isRemote && entity.world.getDayTime() % 20 == 0) {
                entity.sendStatusMessage(new StringTextComponent("Temperature: " + 30 * TemperatureHelper.getTemperatureAt(entity.world, entity.getPosition())), true);
            }
        }
    }

    @SubscribeEvent
    public static void attachWorldCap(AttachCapabilitiesEvent<World> event) {
        event.addCapability(Recrafted.getResource("worldcap"), new WorldCapProvider());
    }

    @SubscribeEvent
    public static void attachPlayerCap(AttachCapabilitiesEvent<PlayerEntity> event) {
        event.addCapability(Recrafted.getResource("playercap"), new PlayerCapProvider(event.getObject()));
    }

    @SubscribeEvent
    public static void registerCommands(FMLServerStartingEvent event) {
        RecraftedCommand.register(event.getCommandDispatcher());
    }

    private static final List<Action> scheduler = new ArrayList<>();

    public static void schedule(int afterTicks, Runnable runnable) {
        scheduler.add(new Action(afterTicks, runnable));
    }

    private static void updateScheduler() {
        scheduler.removeIf(Action::tick);
    }
}
