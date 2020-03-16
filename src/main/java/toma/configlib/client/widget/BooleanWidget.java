package toma.configlib.client.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import toma.configlib.config.display.DisplayEntry;

public class BooleanWidget extends ConfigWidget<DisplayEntry.Bool> {

    public BooleanWidget(int x, int y, int width, int height, String name, DisplayEntry.Bool value) {
        super(x, y, width, height, name, value);
    }

    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        updateHoveredState(p_render_1_, p_render_2_);
        FontRenderer renderer = Minecraft.getInstance().fontRenderer;
        renderer.drawString(getMessage(), x + 2, y + 4, 0xFFFFFF);
        drawRect(x + width - 50, y + height - 20, 50, 20, 1.0F, 1.0F, 1.0F, 1.0F);
        drawRect(x + width - 49, y + height - 19, 48, 18, 0.35F, 0.35F, 0.35F, 1.0F);
        if(isHovered) {
            GlStateManager.enableBlend();
            drawRect(x + width - 50, y + height - 20, 50, 20, 0.6F, 0.7F, 1.0F, 0.6F);
            GlStateManager.disableBlend();
        }
        String text = getEntry().get().toString();
        drawCenteredString(renderer, text, x + width - 25, y + 5, getEntry().get() ? 0x00FF00 : 0xFF0000);
    }

    @Override
    public void onClick(double p_onClick_1_, double p_onClick_3_) {
        this.getEntry().set(!getEntry().get());
    }
}
