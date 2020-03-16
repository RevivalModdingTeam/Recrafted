package toma.configlib.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.RenderComponentsUtil;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.ForgeI18n;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.client.ConfigGuiHandler;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import toma.configlib.ConfigLib;
import toma.configlib.config.ConfigObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class ModListScreen extends Screen {

    public int displayedEntryAmount;
    public List<ModInfo> mods;
    public ModInfo[] displayList;
    private ModDisplayInfo compiledDisplayData;
    private ModInfo selected = null;
    private Button config;
    private int modIdx = 0;
    private int infoIdx = 0;

    public ModListScreen() {
        super(new TranslationTextComponent("fml.menu.mods.title"));
        mods = ModList.get().getMods();
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

    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        renderBackground();
        drawRect(20, 20, 150, 20 + displayedEntryAmount * 20, 0, 0, 0, 0.5F);
        if (displayList == null) return;
        if (p_render_1_ >= 20 && p_render_1_ <= 150 && p_render_2_ >= 20 && p_render_2_ < 20 + displayedEntryAmount * 20) {
            int entry = (p_render_2_ - 20) / 20;
            if (displayList[entry] != null) {
                drawRect(20, 20 + entry * 20, 150, 20 + (entry + 1) * 20, 1.0F, 1.0F, 1.0F, 0.25F);
            }
        }
        if (selected != null) {
            for (int i = 0; i < displayList.length; i++) {
                if (displayList[i] != null && displayList[i].getModId().equals(selected.getModId())) {
                    drawRect(20, 20 + i * 20, 150, 20 + (i + 1) * 20, 1.0F, 1.0F, 1.0F, 0.4F);
                    break;
                }
            }
        }
        if (mods.size() > displayedEntryAmount) {
            drawRect(140, 20, 150, 20 + displayedEntryAmount * 20, 0, 0, 0, 1.0F);
            int steps = mods.size();
            double stepSize = displayedEntryAmount * 20 / 20.0D;
            drawRect(140, (int) (20 + modIdx * stepSize), 150, (int) (20 + modIdx * stepSize) + (int) (displayedEntryAmount * stepSize), 1.0f, 1.0f, 1.0f, 1.0f);
        }
        for (int i = 0; i < displayList.length; i++) {
            ModInfo info = displayList[i];
            if (info == null) break;
            minecraft.fontRenderer.drawStringWithShadow(info.getDisplayName(), 23, 25 + i * 20, 0xFFFFFFFF);
        }
        drawRect(170, 20, width - 20, 25 + ((displayedEntryAmount - 1) * 20), 0, 0, 0, 0.5F);
        if (compiledDisplayData != null) {
            int y = 25;
            int lines = ((displayedEntryAmount - 1) * 20) / 10;
            for (int i = infoIdx; i < infoIdx + lines; i++) {
                if (i >= compiledDisplayData.list.size()) break;
                ITextComponent line = compiledDisplayData.list.get(i);
                if (line != null) {
                    GlStateManager.enableBlend();
                    minecraft.fontRenderer.drawStringWithShadow(line.getFormattedText(), 174, y, 0xFFFFFF);
                    GlStateManager.disableAlphaTest();
                    GlStateManager.disableBlend();
                }
                y += 10;
            }
            int steps = compiledDisplayData.list.size();
            if (steps > lines) {
                drawRect(width - 30, 20, width - 20, 25 + lines * 10, 0, 0, 0, 1.0F);
                double stepSize = lines * 10.0D / steps;
                drawRect(width - 30, 20 + (int) (infoIdx * stepSize), width - 20, 25 + (int) ((infoIdx + lines + 1) * stepSize), 1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
        super.render(p_render_1_, p_render_2_, p_render_3_);
    }

    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        if (p_mouseClicked_5_ == 0) {
            if (p_mouseClicked_1_ >= 20 && p_mouseClicked_1_ <= 150 && p_mouseClicked_3_ >= 20 && p_mouseClicked_3_ < 20 + displayedEntryAmount * 20) {
                int entry = (int) (p_mouseClicked_3_ - 20) / 20;
                ModInfo info = displayList[entry];
                if (info != null) {
                    this.selected = info;
                    this.compiledDisplayData = ModDisplayInfo.compileFrom(info, width - 205);
                    config.active = selected != null && (ConfigLib.CONFIGS.get(selected.getModId()) != null || ConfigGuiHandler.getGuiFactoryFor(selected).isPresent());
                    config.setMessage(config.active ? TextFormatting.GREEN + "Config" : TextFormatting.RED + "Config");
                }
            }
        }
        return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
    }

    @Override
    public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
        int diff = p_mouseScrolled_5_ > 0 ? -1 : 1;
        if (p_mouseScrolled_1_ >= 20 && p_mouseScrolled_3_ >= 20 && p_mouseScrolled_1_ <= 150 && p_mouseScrolled_3_ <= 20 + displayedEntryAmount * 20) {
            // on scroll in mod list
            if ((modIdx > 0 && diff < 0) || (modIdx < mods.size() - displayedEntryAmount && diff > 0)) {
                modIdx += diff;
                init(minecraft, minecraft.mainWindow.getScaledWidth(), minecraft.mainWindow.getScaledHeight());
            }
        } else if (p_mouseScrolled_1_ >= 170 && p_mouseScrolled_3_ >= 20 && p_mouseScrolled_1_ <= width - 20 && p_mouseScrolled_3_ <= 20 + ((displayedEntryAmount - 1) * 20)) {
            // on scroll in mod info
            if (compiledDisplayData == null) return false;
            int h = ((displayedEntryAmount - 1) * 20) / minecraft.fontRenderer.FONT_HEIGHT;
            if ((infoIdx > 0 && diff < 0) || (infoIdx < compiledDisplayData.list.size() - h + 1 && diff > 0)) {
                infoIdx += diff;
            }
        }
        return false;
    }

    @Override
    protected void init() {
        displayedEntryAmount = (minecraft.mainWindow.getScaledHeight() - 40) / 20;
        updateDisplayList();
        int w = (width - 220) / 3;
        addButton(new Button(170, 10 + displayedEntryAmount * 20, w + 5, 20, "Back to menu", b -> minecraft.displayGuiScreen(new MainMenuScreen())));
        addButton(config = new Button(185 + w, 10 + displayedEntryAmount * 20, w + 5, 20, TextFormatting.GREEN + "Config", b -> openConfig()));
        addButton(new Button(200 + 2 * w, 10 + displayedEntryAmount * 20, w + 5, 20, "Open folder", b -> Util.getOSType().openFile(FMLPaths.MODSDIR.get().toFile())));
        config.active = selected != null && (ConfigLib.CONFIGS.get(selected.getModId()) != null || ConfigGuiHandler.getGuiFactoryFor(selected).isPresent());
        config.setMessage(config.active ? TextFormatting.GREEN + "Config" : TextFormatting.RED + "Config");
        if (selected != null) {
            compiledDisplayData = ModDisplayInfo.compileFrom(selected, width - 205);
        }
    }

    protected void openConfig() {
        ConfigObject object = ConfigLib.CONFIGS.get(selected.getModId());
        if (object != null) {
            minecraft.displayGuiScreen(new ModConfigScreen(object, selected));
        } else {
            ConfigGuiHandler.getGuiFactoryFor(selected).map(fun -> fun.apply(this.minecraft, this)).ifPresent(scr -> this.minecraft.displayGuiScreen(scr));
        }
    }

    private void updateDisplayList() {
        displayList = new ModInfo[displayedEntryAmount];
        for (int i = modIdx; i < modIdx + displayedEntryAmount; i++) {
            if (i >= mods.size()) break;
            int index = i - modIdx;
            displayList[index] = mods.get(i);
        }

    }

    private static class ModDisplayInfo {

        private final List<ITextComponent> list;

        private ModDisplayInfo(List<ITextComponent> list) {
            this.list = list;
        }

        private static ModDisplayInfo compileFrom(ModInfo info, int fieldWidth) {
            List<String> lines = new ArrayList<>();
            lines.add(TextFormatting.BOLD + info.getDisplayName());
            lines.add("ID: " + info.getModId());
            info.getModConfig().getOptional("authors").ifPresent(s -> lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.authors", s)));
            info.getModConfig().getOptional("credits").ifPresent(s -> lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.credits", s)));
            info.getModConfig().getOptional("displayURL").ifPresent(s -> lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.displayurl", s)));
            VersionChecker.CheckResult result = VersionChecker.getResult(info);
            if (result.status == VersionChecker.Status.OUTDATED || result.status == VersionChecker.Status.BETA_OUTDATED) {
                lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.updateavailable", result.url == null ? "" : result.url));
            }
            lines.add("");
            lines.add(info.getDescription());
            List<ITextComponent> list = new ArrayList<>();
            for (String line : lines) {
                if (line == null) {
                    list.add(null);
                    continue;
                }
                ITextComponent component = ForgeHooks.newChatWithLinks(line, false);
                list.addAll(RenderComponentsUtil.splitText(component, fieldWidth, Minecraft.getInstance().fontRenderer, false, true));
            }
            return new ModDisplayInfo(list);
        }

        private List<ITextComponent> getList() {
            return list;
        }
    }
}
