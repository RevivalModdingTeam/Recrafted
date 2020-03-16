package toma.configlib.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.StringTextComponent;
import toma.configlib.config.display.DisplayEntry;

import java.util.ArrayList;
import java.util.List;

public class ModConfigEditScreen extends Screen {

    private final Screen parent;
    private final DisplayEntry<?> entry;
    private final List<Widget> entryList = new ArrayList<>();

    private int entriesOnPageAmount;
    private int scrollIndex;

    public ModConfigEditScreen(Screen parent, DisplayEntry<?> entry) {
        super(new StringTextComponent(""));
        this.parent = parent;
        this.entry = entry;
    }

    public void addWidgetEntry(Widget widget) {
        entryList.add(widget);
    }

    public void removeWidgetEntry(int index) {
        entryList.remove(index);
    }

    @Override
    public void onClose() {
        minecraft.displayGuiScreen(parent);
    }

    @Override
    protected void init() {
        entryList.clear();
        entriesOnPageAmount = (height - 40) / 25;
        entry.initInScreen(minecraft, this, "", 0);
        for(int i = scrollIndex; i < scrollIndex + entriesOnPageAmount; i++) {
            if(i >= entryList.size()) break;
            int idx = i - scrollIndex;
            Widget widget = entryList.get(i);
            widget.y = 20 + idx * 25;
            addButton(widget);
        }
        if(entry instanceof DisplayEntry.Arr) {
            int last = 0;
            for(int i = scrollIndex; i < scrollIndex + entriesOnPageAmount; i++) {
                if(i >= entryList.size()) break;
                int idx = i - scrollIndex;
                addButton(new Button(width - 20, 20 + idx * 25, 20, 20, "-", b -> {
                    DisplayEntry.Arr arr = (DisplayEntry.Arr) entry;
                    arr.get().remove(idx + scrollIndex);
                    init(minecraft, minecraft.mainWindow.getScaledWidth(), minecraft.mainWindow.getScaledHeight());
                }));
                last++;
            }
            addButton(new Button(20, 20 + last * 25, 20, 20, "+", b -> {
                DisplayEntry.Arr arr = (DisplayEntry.Arr) entry;
                arr.get().add(arr.getFactory().get());
                init(minecraft, minecraft.mainWindow.getScaledWidth(), minecraft.mainWindow.getScaledHeight());
            }));
        }
    }

    @Override
    public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
        int scrolled = p_mouseScrolled_5_ < 0 ? 1 : -1;
        if(scrolled < 0 && scrollIndex > 0) {
            --scrollIndex;
            init(minecraft, minecraft.mainWindow.getScaledWidth(), minecraft.mainWindow.getScaledHeight());
        } else if(scrolled > 0 && scrollIndex < entryList.size() - entriesOnPageAmount) {
            ++scrollIndex;
            init(minecraft, minecraft.mainWindow.getScaledWidth(), minecraft.mainWindow.getScaledHeight());
        }
        return false;
    }

    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground();
        if(entryList.isEmpty()) {
            minecraft.fontRenderer.drawStringWithShadow("Nothing in here... :( Press ESC to go back", 20, 20, 0xff5555);
        }
        renderScrollbar();
        super.render(p_render_1_, p_render_2_, p_render_3_);
    }

    protected void renderScrollbar() {
        if(entryList.size() <= entriesOnPageAmount) {
            return;
        }
        int steps = entryList.size();
        double stepSize = height / (double)steps;
        drawRect(0, 0, 10, height, 0f, 0f, 0f, 0.5F);
        drawRect(0, (int)(scrollIndex * stepSize), 10, (int)(scrollIndex * stepSize + entriesOnPageAmount * stepSize), 1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static void drawRect(int x, int y, int x2, int y2, float r, float g, float b, float a) {
        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        builder.pos(x, y2, 0).color(r, g, b, a).endVertex();
        builder.pos(x2, y2, 0).color(r, g, b, a).endVertex();
        builder.pos(x2, y, 0).color(r, g, b, a).endVertex();
        builder.pos(x, y, 0).color(r, g, b, a).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture();
    }
}
