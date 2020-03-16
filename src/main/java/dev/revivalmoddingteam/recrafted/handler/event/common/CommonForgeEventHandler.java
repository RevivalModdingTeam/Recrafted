package dev.revivalmoddingteam.recrafted.handler.event.common;

import dev.revivalmoddingteam.recrafted.Recrafted;
import dev.revivalmoddingteam.recrafted.handler.event.Action;
import dev.revivalmoddingteam.recrafted.network.NetworkHandler;
import dev.revivalmoddingteam.recrafted.network.client.CPacketSyncWorldData;
import dev.revivalmoddingteam.recrafted.world.capability.WorldCapFactory;
import dev.revivalmoddingteam.recrafted.world.capability.WorldCapProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Recrafted.MODID)
public class CommonForgeEventHandler {

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
        }
    }

    @SubscribeEvent
    public static void attachWorldCap(AttachCapabilitiesEvent<World> event) {
        event.addCapability(Recrafted.getResource("worldcap"), new WorldCapProvider());
    }

    private static final List<Action> scheduler = new ArrayList<>();

    public static void schedule(int afterTicks, Runnable runnable) {
        scheduler.add(new Action(afterTicks, runnable));
    }

    private static void updateScheduler() {
        scheduler.removeIf(Action::tick);
    }
}
