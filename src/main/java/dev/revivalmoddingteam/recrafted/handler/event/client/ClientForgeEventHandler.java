package dev.revivalmoddingteam.recrafted.handler.event.client;

import com.mojang.blaze3d.platform.GlStateManager;
import dev.revivalmoddingteam.recrafted.Recrafted;
import dev.revivalmoddingteam.recrafted.handler.Registry;
import dev.revivalmoddingteam.recrafted.handler.event.Action;
import dev.revivalmoddingteam.recrafted.handler.event.common.CommonForgeEventHandler;
import dev.revivalmoddingteam.recrafted.player.IPlayerCap;
import dev.revivalmoddingteam.recrafted.player.PlayerCapFactory;
import dev.revivalmoddingteam.recrafted.player.objects.PlayerStatData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Recrafted.MODID, value = Dist.CLIENT)
public class ClientForgeEventHandler {

    private static final ResourceLocation OVERLAYS_X8 = Recrafted.makeResource("textures/overlay/overlays_x8.png");
    private static final ResourceLocation OVERLAYS_X16 = Recrafted.makeResource("textures/overlay/overlays_x16.png");

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();
            PlayerEntity player = mc.player;
            if(player != null) {
                ++CommonForgeEventHandler.tickCounter;
                IPlayerCap cap = PlayerCapFactory.get(player);
                if(player.isSprinting() && cap.getStats().getThirstLevel() <= 6) {
                    player.setSprinting(false);
                }
            }
            updateScheduler();
        }
    }

    @SubscribeEvent
    public static void renderOverlayPre(RenderGameOverlayEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        PlayerEntity player = mc.player;
        if(event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            // draw thirst overlay
            if(player.isCreative() || player.isSpectator()) {
                return;
            }
            PlayerStatData stats = PlayerCapFactory.get(player).getStats();
            int thirstLevel = stats.getThirstLevel();
            int left = event.getWindow().getScaledWidth() / 2 + 10;
            int top = event.getWindow().getScaledHeight() - (player.areEyesInFluid(FluidTags.WATER) || player.getAir() < 300 ? 58 : 48);
            int atlastX = player.getActivePotionEffect(Registry.REffects.THIRST) != null ? 3 : 0;
            for(int i = 1; i <= 10; i++) {
                int pos = i * 2;
                int j = 10 - i;
                if(thirstLevel >= pos) {
                    x8_blit(left + 8 * j, top, atlastX, 0);
                } else if(pos - thirstLevel == 1) {
                    x8_blit(left + 8 * j, top, atlastX + 1, 0);
                } else {
                    x8_blit(left + 8 * j, top, atlastX + 2, 0);
                }
            }
        }
    }

    private static void x8_blit(int left, int top, int atlasX, int atlasY) {
        atlastBlit(left, top, 8, atlasX, atlasY, OVERLAYS_X8);
    }

    private static void x16_blit(int left, int top, int atlasX, int atlasY) {
        atlastBlit(left, top, 16, atlasX, atlasY, OVERLAYS_X16);
    }

    private static void atlastBlit(int left, int top, int size, int atlasX, int atlasY, ResourceLocation location) {
        double xTex = size * atlasX / 256.0D;
        double yTex = size * atlasY / 256.0D;
        double xTexe = xTex + size / 256.0D;
        double yTexe = yTex + size / 256.0D;
        Minecraft.getInstance().getTextureManager().bindTexture(location);
        GlStateManager.enableBlend();
        GlStateManager.enableTexture();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION_TEX);
        builder.pos(left, top + size, 0).tex(xTex, yTexe).endVertex();
        builder.pos(left + size, top + size, 0).tex(xTexe, yTexe).endVertex();
        builder.pos(left + size, top, 0).tex(xTexe, yTex).endVertex();
        builder.pos(left, top, 0).tex(xTex, yTex).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
    }

    private static final List<Action> scheduler = new ArrayList<>();

    public static void schedule(int afterTicks, Runnable runnable) {
        scheduler.add(new Action(afterTicks, runnable));
    }

    private static void updateScheduler() {
        scheduler.removeIf(Action::tick);
    }
}
