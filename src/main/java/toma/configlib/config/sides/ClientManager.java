package toma.configlib.config.sides;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import toma.configlib.ConfigLib;
import toma.configlib.config.ConfigObject;
import toma.configlib.config.IConfig;
import toma.configlib.config.display.DisplayEntry;
import toma.configlib.config.util.ConfigHelper;
import toma.configlib.events.ConfigEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ClientManager extends Manager {

    @Override
    public void load() {
        Minecraft mc = Minecraft.getInstance();
        File configs = new File(mc.gameDir, "config");
        File[] entries = configs.listFiles();
        if(entries == null) {
            ConfigLib.log.fatal("Unknown config directory, using default values");
            return;
        }
        Iterator<Map.Entry<String, ConfigObject>> iterator = ConfigLib.CONFIGS.entrySet().iterator();
        List<Map.Entry<String, ConfigObject>> missingConfigs = new ArrayList<>();
        while (iterator.hasNext()) {
            Map.Entry<String, ConfigObject> configEntry = iterator.next();
            ConfigObject configObject = configEntry.getValue();
            IConfig config = configObject.getConfig();
            String modid = configEntry.getKey();
            String fileName = modid + ".json";
            boolean found = false;
            for(File entry : entries) {
                if(fileName.equals(entry.getName())) {
                    MinecraftForge.EVENT_BUS.post(new ConfigEvent.Load(entry, modid));
                    String contents = readFileToString(entry);
                    JsonParser parser = new JsonParser();
                    JsonObject object = parser.parse(contents).getAsJsonObject();
                    ConfigLib.log.info("Deserializing config for mod {} with {}", modid, config.getClass());
                    try {
                        config.deserializeClient(object.has("client") ? object.getAsJsonObject("client") : new JsonObject());
                        config.deserialize(object.has("common") ? object.getAsJsonObject("common") : new JsonObject());
                        ConfigLib.log.info("Successfully loaded config for mod {}", modid);
                        found = true;
                    } catch (Exception e) {
                        ConfigLib.log.fatal("Exception occurred while parsing config for mod {}", modid);
                        e.printStackTrace();
                    }
                    JsonObject _c = new JsonObject();
                    DisplayEntry.Obj clientEntry = new DisplayEntry.Obj(_c);
                    config.serializeClient(clientEntry);
                    JsonObject _s = new JsonObject();
                    DisplayEntry.Obj commonEntry = new DisplayEntry.Obj(_s);
                    config.serialize(commonEntry);
                    _c = ConfigHelper.convertToJson(clientEntry);
                    _s = ConfigHelper.convertToJson(commonEntry);
                    JsonObject finalObj = new JsonObject();
                    finalObj.add("client", _c);
                    finalObj.add("common", _s);
                    configObject.updateDataObject(clientEntry, commonEntry, finalObj);
                    rewrite(finalObj, entry);
                }
            }
            if(!found) {
                missingConfigs.add(configEntry);
            }
        }
        if(missingConfigs.isEmpty()) return;
        for (Map.Entry<String, ConfigObject> entry : missingConfigs) {
            IConfig config = entry.getValue().getConfig();
            config.deserializeClient(new JsonObject());
            config.deserialize(new JsonObject());
            File file = new File(configs.getAbsolutePath(), entry.getKey() + ".json");
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            JsonObject c_default = new JsonObject();
            DisplayEntry.Obj client = new DisplayEntry.Obj(c_default);
            config.serializeClient(client);
            JsonObject _default = new JsonObject();
            DisplayEntry.Obj common = new DisplayEntry.Obj(_default);
            config.serialize(common);
            JsonObject configObj = new JsonObject();
            configObj.add("client", ConfigHelper.convertToJson(client));
            configObj.add("common", ConfigHelper.convertToJson(common));
            //entry.getValue().updateDataObject(configObj);
            String json = GSON.toJson(configObj);
            rewrite(configObj, file);
        }
    }
}
