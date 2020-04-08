package toma.config.ui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import toma.config.datatypes.IConfigType;

public class ConfigWidget<T> extends Widget {

    protected static final ResourceLocation WIDGET = new ResourceLocation("configlib:textures/ui/widget.png");
    protected IConfigType<T> type;
    protected String displayedName;
    protected boolean isHovered;

    public ConfigWidget(int x, int y, int w, int h, IConfigType<T> type) {
        super(x, y, w, h, "");
        this.type = type;
    }

    public IConfigType<T> getType() {
        return type;
    }

    public void init() {

    }

    public void onKeyPress(char character, int code) {

    }

    public void pressedKey(int k1, int k2, int k3) {

    }

    public void doRender(double mouseX, double mouseY, float partial) {

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return isMouseIn(mouseX, mouseY, x + 100, y, width - 130, height);
    }

    public final boolean isMouseIn(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public static void blit(int x, int y, int width, int height, int px0, int px1, int px2, int px3) {
        drawTexture(x, y, width, height, px0 / 256.0D, px1 / 256.0D, px2 / 256.0D, px3 / 256.0D, WIDGET);
    }

    public static void drawColor(int x, int y, int width, int height, float r, float g, float b, float a) {
        GlStateManager.color3f(1f, 1f, 1f);
        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        builder.pos(x, y + height, 0).color(r, g, b, a).endVertex();
        builder.pos(x + width, y + height, 0).color(r, g, b, a).endVertex();
        builder.pos(x + width, y, 0).color(r, g, b, a).endVertex();
        builder.pos(x, y, 0).color(r, g, b, a).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture();
    }

    public static void drawTexture(int x, int y, int width, int height, double txs, double tys, double txe, double tye, ResourceLocation texture) {
        GlStateManager.enableBlend();
        Minecraft.getInstance().textureManager.bindTexture(texture);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION_TEX);
        builder.pos(x, y + height, 0).tex(txs, tye).endVertex();
        builder.pos(x + width, y + height, 0).tex(txe, tye).endVertex();
        builder.pos(x + width, y, 0).tex(txe, tys).endVertex();
        builder.pos(x, y, 0).tex(txs, tys).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
    }

    public static void drawTexture(int x, int y, int width, int height, ResourceLocation texture) {
        drawTexture(x, y, width, height, 0.0D, 0.0D, 1.0D, 1.0D, texture);
    }

    @Override
    public final void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        GlStateManager.color3f(1.0F, 1.0F, 1.0F);
        isHovered = isMouseOver(p_render_1_, p_render_2_);
        this.doRender(p_render_1_, p_render_2_, p_render_3_);
    }

    public static class SubWidget {

        protected int x, y, width, height;

    }
}
