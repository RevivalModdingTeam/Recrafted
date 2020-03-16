package toma.configlib.client;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.GuiModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import toma.configlib.ConfigLib;
import toma.configlib.config.ConfigObject;
import toma.configlib.config.IConfig;
import toma.configlib.config.display.DisplayEntry;
import toma.configlib.config.sides.ClientManager;
import toma.configlib.config.util.ConfigHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class ModConfigScreen extends Screen {

    private final ConfigObject object;
    private final ModInfo info;

    public ModConfigScreen(ConfigObject object, ModInfo modInfo) {
        super(new StringTextComponent(object.toString()));
        this.object = object;
        this.info = modInfo;
    }

    @Override
    public void onClose() {
        DisplayEntry.Obj clientObj = (DisplayEntry.Obj) this.object.getEntryMap().get("client");
        DisplayEntry.Obj commonObj = (DisplayEntry.Obj) this.object.getEntryMap().get("common");
        JsonObject client = ConfigHelper.convertToJson(clientObj);
        JsonObject common = ConfigHelper.convertToJson(commonObj);
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("client", client);
        jsonObject.add("common", common);
        String modid = info.getModId();
        File file = new File(minecraft.gameDir, "config/" + modid + ".json");
        try {
            FileWriter writer = new FileWriter(file);
            String json = ClientManager.GSON.toJson(jsonObject);
            writer.write(json);
            writer.close();
            ConfigLib.log.debug("Config for {} has been updated", modid);
            IConfig config = object.getConfig();
            config.deserializeClient(client);
            config.deserialize(common);
            object.updateDataObject(clientObj, commonObj, jsonObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
        minecraft.displayGuiScreen(new GuiModList(new MainMenuScreen()));
    }

    @Override
    protected void init() {
        Map<String, DisplayEntry<?>> map = object.getEntryMap();
        int j = 0;
        for(Map.Entry<String, DisplayEntry<?>> entry : map.entrySet()) {
            addButton(new Button(30, 30 + j * 25, 100, 20, entry.getKey(), button -> minecraft.displayGuiScreen(new ModConfigEditScreen(this, entry.getValue()))));
            ++j;
        }
        addButton(new Button(30, height - 30, 100, 20, "Back", btn -> minecraft.displayGuiScreen(new ModListScreen())));
    }

    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        renderBackground();
        minecraft.fontRenderer.drawStringWithShadow(info.getModId() + " config", 30, 10, 0xFFFFFFFF);
        super.render(p_render_1_, p_render_2_, p_render_3_);
    }
}
