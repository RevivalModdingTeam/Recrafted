package toma.config.ui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import org.lwjgl.opengl.GL11;
import toma.config.datatypes.ConfigObject;
import toma.config.datatypes.IConfigType;
import toma.config.ui.widget.ConfigWidget;
import toma.config.util.ConfigUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModConfigScreen extends Screen {

    private ConfigObject object;
    private Map<String, IConfigType<?>> valueMap;
    private ModInfo info;
    private final Screen parent;

    private long lastTime;
    private int animationTick;

    private List<IConfigType<?>> typeList = new ArrayList<>();
    private List<ConfigWidget<?>> renderList = new ArrayList<>();
    private int index;
    private int entryCount;

    public ModConfigScreen(Map<String, IConfigType<?>> valueMap, Screen parent) {
        super(new StringTextComponent(""));
        this.valueMap = valueMap;
        this.parent = parent;
    }

    public ModConfigScreen(ConfigObject object, ModInfo modInfo, Screen parent) {
        this(object.value(), parent);
        this.object = object;
        this.info = modInfo;
    }

    @Override
    public void onClose() {
        if(this.object != null) {
            this.object.set(valueMap);
            if(!(parent instanceof ModConfigScreen)) {
                ConfigUtils.c_saveConfig(info.getModId(), this.object);
            }
        }
        minecraft.displayGuiScreen(parent);
    }

    @Override
    protected void init() {
        this.typeList.clear();
        this.renderList.clear();
        entryCount = (height - 20) / 30 + 1;
        typeList.addAll(valueMap.values());
        for(int i = index; i < index + entryCount; i++) {
            if(i >= typeList.size()) break;
            ConfigWidget<?> widget = typeList.get(i).create(30, 10 + (i - index) * 25, width - 60, 20, this);
            renderList.add(widget);
            widget.init();
        }
        addButton(new Button(30, height - 30, 100, 20, "Back", btn -> this.onClose()));
    }

    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        for(ConfigWidget<?> widget : renderList) {
            if(widget.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
                widget.playDownSound(minecraft.getSoundHandler());
                return true;
            }
        }
        return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
    }

    @Override
    public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
        int pos = p_mouseScrolled_5_ > 0 ? -1 : 1;
        int next = index + pos;
        if(next >= 0 && next <= typeList.size() - entryCount) {
            index = next;
            init();
        }
        return false;
    }

    @Override
    public boolean charTyped(char character, int code) {
        renderList.forEach(w -> w.onKeyPress(character, code));
        return false;
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        renderList.forEach(w -> w.pressedKey(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_));
        return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
    }

    @Override
    public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
        renderList.forEach(e -> e.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_));
        return false;
    }

    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        renderBackground();
        for(ConfigWidget<?> widget : this.renderList) {
            widget.render(p_render_1_, p_render_2_, p_render_3_);
        }
        long time = System.currentTimeMillis();
        float diff = 1200.0F;
        long l = time - lastTime;
        if(l > diff) lastTime = time;
        float f = (float) Math.cos(Math.toRadians(-90 + 180 * (l / diff)));
        if(index < typeList.size() - entryCount) drawArrow(width - 18, entryCount * 25 - 12 + 7 * f, 10, 10, false);
        if(typeList.size() > entryCount) {
            int height = 25 * (entryCount - 2) - 5;
            float perStep = height / (float) typeList.size();
            float start = 35 + index * perStep;
            float end = entryCount * perStep;
            drawColorShape(width - 23, 35, 16, height, 0f, 0f, 0f, 1.0F);
            drawColorShape(width - 23, start, 16, end, 1.0F, 1.0F, 1.0F, 1.0F);
        }
        if(index > 0) drawArrow(width - 18, 20 + 7 * -f, 10, 10, true);
        super.render(p_render_1_, p_render_2_, p_render_3_);
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
