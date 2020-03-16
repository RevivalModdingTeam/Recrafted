package toma.configlib.client.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import toma.configlib.ConfigLib;
import toma.configlib.config.display.DisplayEntry;
import toma.configlib.config.types.primitives.ConfigTypeNumber;

import java.util.function.BiPredicate;

public class NumberEditWidget extends EditableWidget<DisplayEntry.Num> {

    private final Type type;

    public NumberEditWidget(int x, int y, int w, int h, String key, DisplayEntry.Num entry) {
        super(x, y, w, h, key, entry, getFilter(entry));
        this.type = entry.getLimiter() != null ? Type.SLIDER : Type.INPUT_FIELD;
    }

    @Override
    public void onClick(double p_onClick_1_, double p_onClick_3_) {
        type.handleMouseClicked(p_onClick_1_, p_onClick_3_, this);
    }

    @Override
    protected void onDrag(double p_onDrag_1_, double p_onDrag_3_, double p_onDrag_5_, double p_onDrag_7_) {
        this.onClick(p_onDrag_1_, p_onDrag_3_);
    }

    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        type.render(p_render_1_, p_render_2_, p_render_3_, this);
    }

    @Override
    public void updateEntry() {
        Number number = getEntry().get();
        if(text.isEmpty() || (text.length() == 1 && text.startsWith("-"))) {
            text = "0";
        }
        if(number instanceof Double) {
            getEntry().set(Double.parseDouble(text));
        } else getEntry().set(Integer.parseInt(text));
    }

    private static BiPredicate<Character, String> getFilter(DisplayEntry.Num entry) {
        Number n = entry.get();
        if(n instanceof Double) {
            return Filters.DECIMAL_NUMBER_FILTER;
        } else return Filters.WHOLE_NUMBER_FILTER;
    }

    private enum Type {

        INPUT_FIELD((mouseX, mouseY, widget) -> {
            int x = widget.x + widget.width - 2 * (widget.width / 3);
            if(mouseX >= x && mouseY >= widget.y && mouseX <= x + 2 * widget.width / 3 && mouseY <= widget.y + widget.height) {
                widget.selected = !widget.selected;
                if(!widget.selected) {
                    try {
                        widget.updateEntry();
                    } catch (Exception e) {
                        ConfigLib.log.fatal("Exception occurred when updating config: {}", e.toString());
                    }
                }
            }
        }, (mouseX, mouseY, partialTicks, widget) -> {
            widget.updateHoveredState(mouseX, mouseY);
            drawRect(widget.x + widget.width - 2 * (widget.width / 3), widget.y + widget.height - 20, 2 * widget.width / 3, 20, 1.0F, 1.0F, 1.0F, 1.0F);
            drawRect(widget.x + widget.width - 2 * (widget.width / 3) + 1, widget.y + widget.height - 19, 2 * widget.width / 3 - 2, 18, 0.35F, 0.35F, 0.35F, 1.0F);
            FontRenderer renderer = Minecraft.getInstance().fontRenderer;
            if(widget.selected) {
                long time = System.currentTimeMillis();
                if(time % 1000 >= 500) {
                    int width = renderer.getStringWidth(widget.text);
                    drawRect(widget.x + widget.width / 3 + 5 + width, widget.y + 4, 2, 12, 1.0F, 1.0F, 1.0F, 1.0F);
                }
            }
            renderer.drawString(widget.getMessage(), widget.x + 2, widget.y + 5, 0xFFFFFFFF);
            renderer.drawString(widget.text, widget.x + widget.width / 3.0F + 5, widget.y + 5, widget.selected ? 0xFFFFFF00 : 0xFFFFFFFF);
        }),

        SLIDER((mouseX, mouseY, widget) -> {
            int sliderX = widget.x + widget.width - 2 * widget.width / 3;
            int sliderWidth = 2 * widget.width / 3;
            if(mouseX >= sliderX && mouseY >= widget.y && mouseX <= sliderX + sliderWidth && mouseY <= widget.y + widget.height) {
                double pct = (mouseX - sliderX) / sliderWidth;
                ConfigTypeNumber.Limiter limiter = widget.getEntry().getLimiter();
                double value = limiter.getMin().doubleValue() + (limiter.getMax().doubleValue() - limiter.getMin().doubleValue()) * pct;
                int intVal = (int) value;
                widget.getEntry().set(widget.filter == Filters.WHOLE_NUMBER_FILTER ? intVal : value);
            }
        }, (mouseX, mouseY, partialTicks, widget) -> {
            int sliderX = widget.x + widget.width - 2 * widget.width / 3;
            int sliderWidth = 2 * widget.width / 3;
            drawRect(sliderX, widget.y, sliderWidth, 20, 0.3f, 0.3f, 0.3f, 1.0f);
            ConfigTypeNumber.Limiter limiter = widget.getEntry().getLimiter();
            double value = widget.getEntry().get().doubleValue();
            if(value < limiter.getMin().doubleValue()) {
                widget.getEntry().set(widget.filter == Filters.WHOLE_NUMBER_FILTER ? (int)limiter.getMin() : limiter.getMin());
            } else if(value > limiter.getMax().doubleValue()) {
                widget.getEntry().set(widget.filter == Filters.WHOLE_NUMBER_FILTER ? (int)limiter.getMax() : limiter.getMax());
            }
            double percent = (value - limiter.getMin().doubleValue()) / (limiter.getMax().floatValue() - limiter.getMin().floatValue());
            drawRect(sliderX + (int)(sliderWidth * percent) - 2, widget.y, 5, 20, 1.0F, 1.0F, 1.0F, 1.0F);
            FontRenderer renderer = Minecraft.getInstance().fontRenderer;
            renderer.drawString(widget.getMessage(), widget.x + 2, widget.y + 5, 0xffffff);
            String message = widget.getEntry().get().toString();
            renderer.drawString(message, sliderX + (sliderWidth - renderer.getStringWidth(message)) / 2, widget.y + 6, 0x00ffff);
        });

        private final TriConsumer<Double, Double, NumberEditWidget> mouseClickCons;
        private final TypeConsumer<Integer, Integer, Float, NumberEditWidget> renderConsumer;

        Type(TriConsumer<Double, Double, NumberEditWidget> func, TypeConsumer<Integer, Integer, Float, NumberEditWidget> renderConsumer) {
            this.mouseClickCons = func;
            this.renderConsumer = renderConsumer;
        }

        private void render(int mx, int my, float partial, NumberEditWidget widget) {
            renderConsumer.accept(mx, my, partial, widget);
        }

        private void handleMouseClicked(double mouseX, double mouseY, NumberEditWidget widget) {
            mouseClickCons.accept(mouseX, mouseY, widget);
        }

        private interface TriConsumer<A, B, C> {
            void accept(A a, B b, C c);
        }

        private interface TypeConsumer<A, B, C, D> {
            void accept(A a, B b, C c, D d);
        }
    }
}
