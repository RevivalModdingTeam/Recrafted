package toma.configlib.config.display;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import toma.configlib.client.ModConfigEditScreen;
import toma.configlib.client.widget.BooleanWidget;
import toma.configlib.client.widget.NumberEditWidget;
import toma.configlib.client.widget.StringEditWidget;
import toma.configlib.config.ConfigObject;
import toma.configlib.config.types.primitives.ConfigTypeNumber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class DisplayEntry<T> implements Supplier<T> {

    private T value;

    private DisplayEntry(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

    @OnlyIn(Dist.CLIENT)
    public void initInScreen(Minecraft mc, ModConfigEditScreen instance, String key, int offset) {
        throw new IllegalArgumentException("Missing entry initializer implementation!");
    }

    public static class Bool extends DisplayEntry<Boolean> {
        public Bool() {
            this(false);
        }
        public Bool(boolean value) {
            super(value);
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public void initInScreen(Minecraft mc, ModConfigEditScreen instance, String key, int offset) {
            instance.addWidgetEntry(new BooleanWidget(20, 20, instance.width - 40, 20, key, this));
        }
    }

    public static class Num extends DisplayEntry<Number> {
        private ConfigTypeNumber.Limiter limiter;
        public Num() {
            this(0);
        }
        public Num(Number value) {
            this(value, null);
        }
        public Num(Number value, ConfigTypeNumber.Limiter limiter) {
            super(value);
            this.limiter = limiter;
        }

        public ConfigTypeNumber.Limiter getLimiter() {
            return limiter;
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public void initInScreen(Minecraft mc, ModConfigEditScreen instance, String key, int offset) {
            instance.addWidgetEntry(new NumberEditWidget(20, 20, instance.width - 40, 20, key, this));
        }
    }

    public static class Str extends DisplayEntry<String> {
        public Str() {
            this("");
        }
        public Str(String value) {
            super(value);
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public void initInScreen(Minecraft mc, ModConfigEditScreen instance, String key, int offset) {
            instance.addWidgetEntry(new StringEditWidget(20, 20, instance.width - 40, 20, key, this));
        }
    }

    public static class Obj extends DisplayEntry<Map<String, DisplayEntry<?>>> {
        public Obj() {
            super(new HashMap<>());
        }
        public Obj(JsonObject object) {
            super(get(object));
        }
        private static Map<String, DisplayEntry<?>> get(JsonObject jsonObject) {
            Map<String, DisplayEntry<?>> entryMap = new HashMap<>();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                JsonElement value = entry.getValue();
                DisplayEntry<?> v = ConfigObject.getEntry(entry.getValue());
                entryMap.put(entry.getKey(), v);
            }
            return entryMap;
        }

        public DisplayEntry<?> put(String key, DisplayEntry<?> value) {
            return get().put(key, value);
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public void initInScreen(Minecraft mc, ModConfigEditScreen instance, String key, int n) {
            int i = 0;
            for (Map.Entry<String, DisplayEntry<?>> entry : get().entrySet()) {
                DisplayEntry<?> displayEntry = entry.getValue();
                if (displayEntry instanceof Obj || displayEntry instanceof Arr) {
                    instance.addWidgetEntry(new Arr.EntryButton(20, 20, instance.width - 40, 20, entry.getKey(), b -> mc.displayGuiScreen(new ModConfigEditScreen(instance, displayEntry))));
                } else {
                    displayEntry.initInScreen(mc, instance, entry.getKey(), n + i);
                }
                ++i;
            }
        }
    }

    public static class Arr extends DisplayEntry<List<DisplayEntry<?>>> {
        private Supplier<DisplayEntry<?>> factory;
        public Arr(JsonArray array, Supplier<DisplayEntry<?>> supplier) {
            super(get(array));
            this.factory = supplier;
        }

        public Supplier<DisplayEntry<?>> getFactory() {
            return factory;
        }

        private static List<DisplayEntry<?>> get(JsonArray array) {
            List<DisplayEntry<?>> list = new ArrayList<>();
            for (JsonElement element : array) {
                list.add(ConfigObject.getEntry(element));
            }
            return list;
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public void initInScreen(Minecraft mc, ModConfigEditScreen instance, String text, int n) {
            int j = 0;
            for (DisplayEntry<?> entry : get()) {
                if(entry instanceof Obj) {
                    instance.addWidgetEntry(new EntryButton(20, 20, instance.width - 40, 20, "Entry" + j, b -> mc.displayGuiScreen(new ModConfigEditScreen(instance, entry))));
                } else {
                    entry.initInScreen(mc, instance, "Entry " + j, j);
                }
                j++;
            }
        }

        @OnlyIn(Dist.CLIENT)
        private static class EntryButton extends Button {
            private EntryButton(int x, int y, int w, int h, String text, IPressable pressable) {
                super(x, y, w, h, text, pressable);
            }

            @Override
            public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
                Minecraft minecraft = Minecraft.getInstance();
                FontRenderer fontrenderer = minecraft.fontRenderer;
                minecraft.getTextureManager().bindTexture(WIDGETS_LOCATION);
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, this.alpha);
                int i = this.getYImage(this.isHovered());
                GlStateManager.enableBlend();
                GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                int texY = 46 + i * 20;
                customBlitFunc(x, x + width, y, y + height, 0, 0, 200 / 256.0D, texY / 256.0D, (texY + 20) / 256.0D);
                this.renderBg(minecraft, p_renderButton_1_, p_renderButton_2_);
                int j = getFGColor();

                this.drawCenteredString(fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
            }

            private static void customBlitFunc(int x1, int x2, int y1, int y2, int z, double texU1, double texU2, double texV1, double texV2) {
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder builder = tessellator.getBuffer();
                builder.begin(7, DefaultVertexFormats.POSITION_TEX);
                builder.pos(x1, y2, z).tex(texU1, texV2).endVertex();
                builder.pos(x2, y2, z).tex(texU2, texV2).endVertex();
                builder.pos(x2, y1, z).tex(texU2, texV1).endVertex();
                builder.pos(x1, y1, z).tex(texU1, texV1).endVertex();
                tessellator.draw();
            }
        }
    }
}
