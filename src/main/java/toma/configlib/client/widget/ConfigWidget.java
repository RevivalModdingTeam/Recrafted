package toma.configlib.client.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import toma.configlib.config.display.DisplayEntry;

public class ConfigWidget<T extends DisplayEntry<?>> extends Widget {

    private final T entry;

    public ConfigWidget(int x, int y, int width, int height, String name, T value) {
        super(x, y, width, height, name);
        this.entry = value;
    }

    public T getEntry() {
        return entry;
    }

    public void updateHoveredState(int mouseX, int mouseY) {
        this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    }

    public static void drawRect(int x, int y, int w, int h, float r, float g, float b, float a) {
        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        builder.pos(x, y + h, 0).color(r, g, b, a).endVertex();
        builder.pos(x + w, y + h, 0).color(r, g, b, a).endVertex();
        builder.pos(x + w, y, 0).color(r, g, b, a).endVertex();
        builder.pos(x, y, 0).color(r, g, b, a).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture();
    }
}
