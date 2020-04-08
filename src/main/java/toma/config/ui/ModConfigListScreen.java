package toma.config.ui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.opengl.GL11;
import toma.config.datatypes.list.ConfigList;
import toma.config.datatypes.primitives.*;
import toma.config.ui.widget.ConfigWidget;

import java.util.ArrayList;
import java.util.List;

// TODO - make better, use List<IConfigType<T>> instead of List<T>
/**
 * Mess, don't look there
 */
public class ModConfigListScreen<T> extends Screen {

    private Screen parent;
    private ConfigList<T> list;
    private List<T> raw;

    private List<ConfigWidget<T>> widgets = new ArrayList<>();
    private int index;
    private int entryCount;
    private long lastTime;

    public ModConfigListScreen(ConfigList<T> list, Screen parent) {
        super(new StringTextComponent(""));
        this.list = list;
        this.raw = list.value();
        this.parent = parent;
    }

    @Override
    protected void init() {
        widgets.clear();
        entryCount = (height - 20) / 30 + 1;
        for(int i = index; i < index + entryCount; i++) {
            if(i >= raw.size()) break;
            widgets.add(this.widget(raw.get(i), 30, 10 + (i - index) * 25, width - 60, 20));
        }
        addButton(new Button(30, height - 30, 100, 20, "Back", btn -> this.onClose()));
    }

    @Override
    public void onClose() {
        list.set(raw);
        Minecraft.getInstance().displayGuiScreen(parent);
    }

    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        for(int i = 0; i < widgets.size(); i++) {
            ConfigWidget<T> widget = widgets.get(i);
            if(widget.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
                widget.playDownSound(minecraft.getSoundHandler());
            }
            int id = i + index;
            raw.set(id, widget.getType().value());
        }
        return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
    }

    @Override
    public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
        int pos = p_mouseScrolled_5_ > 0 ? -1 : 1;
        int next = index + pos;
        if(next >= 0 && next <= widgets.size() - entryCount) {
            index = next;
            init();
        }
        return false;
    }

    @Override
    public boolean charTyped(char character, int code) {
        widgets.forEach(w -> w.onKeyPress(character, code));
        return false;
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        widgets.forEach(w -> w.pressedKey(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_));
        return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
    }

    @Override
    public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
        widgets.forEach(e -> e.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_));
        return false;
    }

    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        renderBackground();
        widgets.forEach(w -> w.render(p_render_1_, p_render_2_, p_render_3_));
        long time = System.currentTimeMillis();
        float diff = 1200.0F;
        long l = time - lastTime;
        if(l > diff) lastTime = time;
        float f = (float) Math.cos(Math.toRadians(-90 + 180 * (l / diff)));
        if(index < widgets.size() - entryCount) drawArrow(width - 18, entryCount * 25 - 12 + 7 * f, 10, 10, false);
        if(widgets.size() > entryCount) {
            int height = 25 * (entryCount - 2) - 5;
            float perStep = height / (float) widgets.size();
            float start = 35 + index * perStep;
            float end = entryCount * perStep;
            drawColorShape(width - 23, 35, 16, height, 0f, 0f, 0f, 1.0F);
            drawColorShape(width - 23, start, 16, end, 1.0F, 1.0F, 1.0F, 1.0F);
        }
        if(index > 0) drawArrow(width - 18, 20 + 7 * -f, 10, 10, true);
        super.render(p_render_1_, p_render_2_, p_render_3_);
    }

    // TODO delete [1.1.0
    @SuppressWarnings("unchecked")
    public ConfigWidget<T> widget(T entry, int x, int y, int w, int h) {
        if(entry instanceof Boolean) {
            return (ConfigWidget<T>) new ConfigBoolean.BooleanWidget(x, y, w, h, new ConfigBoolean("Boolean", "", (Boolean) entry, this::empty));
        } else if(entry instanceof Byte) {
            ConfigByte configByte = new ConfigByte("Byte", "", (Byte) entry, Byte.MIN_VALUE, Byte.MAX_VALUE, false, this::empty);
            return (ConfigWidget<T>) configByte.create(x, y, w, h, null);
        } else if(entry instanceof Short) {
            ConfigShort configShort = new ConfigShort("Short", "", (Short) entry, Short.MIN_VALUE, Short.MAX_VALUE, false, this::empty);
            return (ConfigWidget<T>) configShort.create(x, y, w, h, null);
        } else if(entry instanceof Integer) {
            ConfigInteger configInteger = new ConfigInteger("Int", "", (Integer) entry, Integer.MIN_VALUE, Integer.MAX_VALUE, false, this::empty);
            return (ConfigWidget<T>) configInteger.create(x, y, w, h, null);
        } else if(entry instanceof Long) {
            ConfigLong configLong = new ConfigLong("Long", "", (Long) entry, Long.MIN_VALUE, Long.MAX_VALUE, false, this::empty);
            return (ConfigWidget<T>) configLong.create(x, y, w, h, null);
        } else if(entry instanceof Float) {
            ConfigFloat configFloat = new ConfigFloat("Float", "", (Float) entry, Float.MIN_VALUE, Float.MAX_VALUE, false, this::empty);
            return (ConfigWidget<T>) configFloat.create(x, y, w, h, null);
        } else if(entry instanceof Double) {
            ConfigDouble configDouble = new ConfigDouble("Double", "", (Double) entry, Double.MIN_VALUE, Double.MAX_VALUE, false, this::empty);
            return (ConfigWidget<T>) configDouble.create(x, y, w, h, null);
        } else if(entry instanceof String) {
            return (ConfigWidget<T>) new ConfigString.StringWidget(x, y, w, h, new ConfigString("String", "", (String) entry, this::empty));
        } else return (ConfigWidget<T>) new DefaultWidget(x, y, w, h);
    }

    private static class DefaultWidget extends ConfigWidget<Object> {

        private static String text = "This type cannot be modified in GUI (yet)";
        private int w;

        private DefaultWidget(int x, int y, int w, int h) {
            super(x, y, w, h, null);
            this.w = Minecraft.getInstance().fontRenderer.getStringWidth(text) / 2;
        }

        @Override
        public void doRender(double mouseX, double mouseY, float partial) {
            FontRenderer renderer = Minecraft.getInstance().fontRenderer;
            blit(x, y, width - 30, 20, 0, 40, 200, 60);
            int ww = (width - 30) / 2;
            renderer.drawStringWithShadow(text, x - w + ww, y + 6, 0xffffff);
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return isMouseIn(mouseX, mouseY, x, y, width - 30, 20);
        }
    }

    private <A> void empty(A a) {
    }

    private void drawArrow(int x, float y, int width, int height, boolean invert) {
        Tessellator tessellator = Tessellator.getInstance();
        GlStateManager.color3f(1f, 1f, 1f);
        double d = width / 3d;
        BufferBuilder builder = tessellator.getBuffer();
        GlStateManager.disableTexture();
        builder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        GlStateManager.lineWidth(5);
        if(invert) {
            builder.pos(x, y + height, 0).color(1f, 1f, 1f, 1f).endVertex();
            builder.pos(d + x, y, 0).color(1f, 1f, 1f, 1f).endVertex();
            builder.pos(d + x, y, 0).color(1f, 1f, 1f, 1f).endVertex();
            builder.pos(2*d + x, y + height, 0).color(1f, 1f, 1f, 1f).endVertex();
        } else {
            builder.pos(x, y, 0).color(1f, 1f, 1f, 1f).endVertex();
            builder.pos(d + x, y + height, 0).color(1f, 1f, 1f, 1f).endVertex();
            builder.pos(d + x, y + height, 0).color(1f, 1f, 1f, 1f).endVertex();
            builder.pos(2*d + x, y, 0).color(1f, 1f, 1f, 1f).endVertex();
        }
        tessellator.draw();
        GlStateManager.lineWidth(1);
        GlStateManager.enableTexture();
    }

    private void drawColorShape(int x, float y, int w, float h, float r, float g, float b, float a) {
        GlStateManager.color3f(1f, 1f, 1f);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        builder.pos(x, y+h, 0).color(r, g, b, a).endVertex();
        builder.pos(x+w, y+h, 0).color(r, g, b, a).endVertex();
        builder.pos(x+w, y, 0).color(r, g, b, a).endVertex();
        builder.pos(x, y, 0).color(r, g, b, a).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture();
    }
}
