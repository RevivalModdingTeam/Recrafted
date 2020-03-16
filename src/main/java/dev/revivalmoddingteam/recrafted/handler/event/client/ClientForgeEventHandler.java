package dev.revivalmoddingteam.recrafted.handler.event.client;

import dev.revivalmoddingteam.recrafted.Recrafted;
import dev.revivalmoddingteam.recrafted.handler.event.Action;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Recrafted.MODID, value = Dist.CLIENT)
public class ClientForgeEventHandler {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.END) {
            updateScheduler();
        }
    }

    private static final List<Action> scheduler = new ArrayList<>();

    public static void schedule(int afterTicks, Runnable runnable) {
        scheduler.add(new Action(afterTicks, runnable));
    }

    private static void updateScheduler() {
        scheduler.removeIf(Action::tick);
    }
}
