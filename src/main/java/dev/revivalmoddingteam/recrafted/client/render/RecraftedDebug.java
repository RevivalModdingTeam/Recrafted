package dev.revivalmoddingteam.recrafted.client.render;

import dev.revivalmoddingteam.recrafted.Recrafted;
import dev.revivalmoddingteam.recrafted.util.helper.ModHelper;
import dev.revivalmoddingteam.recrafted.world.capability.WorldCapFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

public class RecraftedDebug {

    private static boolean debugMode = false;
    private static List<DebugEntry> entryList;

    public static void toggleDebugMode() {
        debugMode = !debugMode;
        modeChanged();
    }

    public static boolean isDebugMode() {
        return debugMode;
    }

    private static void modeChanged() {
        if(debugMode) {
            entryList = new ArrayList<>();
            entryList.add(new DebugEntry("Season", (world, player) -> TextFormatting.GREEN + WorldCapFactory.getData(world).getSeasonData().getSeason().getName().getFormattedText()));
            entryList.add(new DebugEntry("Biome temp", (world, player) -> TextFormatting.AQUA.toString() + world.getBiome(player.getPosition()).getTemperature(player.getPosition()) + ""));
            entryList.add(new DebugEntry("Actual temp", (world, player) -> TextFormatting.AQUA.toString() + ModHelper.getTemperatureAt(world, player.getPosition()) * 20 + ""));
        } else entryList = null;
    }

    @Mod.EventBusSubscriber(modid = Recrafted.MODID, value = Dist.CLIENT)
    public static class EventListener {

        @SubscribeEvent
        public static void drawDebugOverlay(RenderGameOverlayEvent.Post event) {
            if(!debugMode) return;
            if(event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
                Minecraft mc = Minecraft.getInstance();
                if(mc.debugRenderer.shouldRender()) return;
                FontRenderer renderer = mc.fontRenderer;
                PlayerEntity player = mc.player;
                World world = mc.world;
                for(int i = 0; i < entryList.size(); i++) {
                    DebugEntry debugEntry = entryList.get(i);
                    if(debugEntry == null) break;
                    debugEntry.update(world, player);
                    renderer.drawStringWithShadow(entryList.get(i).toString(), 10, 10 + i * 12, 0xffffff);
                }
            }
        }
    }

    private static class DebugEntry {

        private String name;
        private DebugFunction function;
        private String cachedValue;

        private DebugEntry(String name, DebugFunction function) {
            this.name = name;
            this.function = function;
        }

        private void update(World world, PlayerEntity player) {
            cachedValue = function.get(world, player);
        }

        @Override
        public String toString() {
            return name + ": " + (cachedValue != null ? cachedValue : "???");
        }
    }

    private interface DebugFunction {

        String get(World world, PlayerEntity player);
    }
}
