package toma.configlib.client.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.SharedConstants;
import toma.configlib.ConfigLib;
import toma.configlib.config.display.DisplayEntry;

import java.util.function.BiPredicate;

public abstract class EditableWidget<T extends DisplayEntry<?>> extends ConfigWidget<T> {

    protected final BiPredicate<Character, String> filter;
    protected boolean selected;
    protected String text;

    public EditableWidget(int x, int y, int w, int h, String text, T entry, BiPredicate<Character, String> filter) {
        super(x, y, w, h, text, entry);
        this.filter = filter;
        this.text = entry.get().toString();
    }

    public abstract void updateEntry();

    @Override
    public void onClick(double p_onClick_1_, double p_onClick_3_) {
        selected = !selected;
        if(!selected) {
            try {
                updateEntry();
            } catch (Exception e) {
                ConfigLib.log.fatal("Exception occurred when updating config: {}", e.toString());
            }
        }
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if(selected) {
            if(text.length() > 0 && p_keyPressed_1_ == 259) {
                text = text.substring(0, text.length() - 1);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
        if(selected && SharedConstants.isAllowedCharacter(p_charTyped_1_)) {
            if(filter.test(p_charTyped_1_, text)) {
                text = text + p_charTyped_1_;
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        updateHoveredState(p_render_1_, p_render_2_);
        drawRect(x + width - 2 * (width / 3), y + height - 20, 2 * width / 3, 20, 1.0F, 1.0F, 1.0F, 1.0F);
        drawRect(x + width - 2 * (width / 3) + 1, y + height - 19, 2 * width / 3 - 2, 18, 0.35F, 0.35F, 0.35F, 1.0F);
        FontRenderer renderer = Minecraft.getInstance().fontRenderer;
        if(selected) {
            long time = System.currentTimeMillis();
            if(time % 1000 >= 500) {
                int width = renderer.getStringWidth(text);
                drawRect(x + this.width / 3 + 5 + width, y + 4, 2, 12, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
        renderer.drawString(getMessage(), x + 2, y + 5, 0xFFFFFFFF);
        renderer.drawString(text, x + width / 3.0F + 5, y + 5, selected ? 0xFFFFFF00 : 0xFFFFFFFF);
    }

    public static final class Filters {

        public static BiPredicate<Character, String> WHOLE_NUMBER_FILTER = (character, s) -> Character.isDigit(character) || (s.length() == 0 && character == '-');

        public static BiPredicate<Character, String> DECIMAL_NUMBER_FILTER = (character, s) -> Character.isDigit(character) || (s.length() == 0 && character == '-') || (s.length() > 0 && s.charAt(s.length() - 1) != '-' && !s.contains(".") && character == '.');

        public static BiPredicate<Character, String> STRING_FILTER = (character, s) -> true;
    }
}
