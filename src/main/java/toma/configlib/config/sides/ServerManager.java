package toma.configlib.config.sides;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import toma.configlib.ConfigLib;
import toma.configlib.config.ConfigObject;
import toma.configlib.config.IConfig;
import toma.configlib.config.display.DisplayEntry;
import toma.configlib.config.util.ConfigHelper;
import toma.configlib.events.ConfigEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ServerManager extends Manager {

    public static void loadServerConfig() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        File file = new File(server.getDataDirectory(), "config");
        if(!file.exists()) {
            ConfigLib.log.fatal("\n====================================\nCouldn't locate configuration directory!\n====================================\n");
            return;
        }
        File[] entries = file.listFiles();
        Iterator<Map.Entry<String, ConfigObject>> configIt = ConfigLib.CONFIGS.entrySet().iterator();
        List<Map.Entry<String, ConfigObject>> missingConfigs = new ArrayList<>();
        while (configIt.hasNext()) {
            Map.Entry<String, ConfigObject> configEntry = configIt.next();
            String modid = configEntry.getKey();
            ConfigObject configObject = configEntry.getValue();
            IConfig implementation = configObject.getConfig();
            String fileName = modid + ".json";
            boolean foundEntry = false;
            for(File fileEntry : entries) {
                if(fileEntry.getName().equals(fileName)) {
                    MinecraftForge.EVENT_BUS.post(new ConfigEvent.Load(fileEntry, modid));
                    String content = Manager.readFileToString(fileEntry);
                    JsonObject contentObject = new JsonParser().parse(content).getAsJsonObject();
                    ConfigLib.log.info("Deserializing config for mod {} with {}", modid, implementation.getClass());
                    try {
                        implementation.deserializeClient(contentObject.has("client") ? contentObject.getAsJsonObject("client") : new JsonObject());
                        implementation.deserialize(contentObject.has("common") ? contentObject.getAsJsonObject("common") : new JsonObject());
                        ConfigLib.log.info("Successfully loaded config for mod {}", modid);
                        foundEntry = true;
                    } catch (Exception e) {
                        ConfigLib.log.fatal("Exception occurred while parsing config for mod {}", modid);
                        e.printStackTrace();
                    }
                    implementation.getListeners().forEach(Runnable::run);
                    JsonObject _c = new JsonObject();
                    DisplayEntry.Obj _cEntry = new DisplayEntry.Obj(_c);
                    JsonObject _s = new JsonObject();
                    DisplayEntry.Obj _sEntry = new DisplayEntry.Obj(_s);
                    implementation.serializeClient(_cEntry);
                    implementation.serialize(_sEntry);
                    _c = ConfigHelper.convertToJson(_cEntry);
                    _s = ConfigHelper.convertToJson(_sEntry);
                    JsonObject parentObject = new JsonObject();
                    parentObject.add("client", _c);
                    parentObject.add("common", _s);
                    configObject.updateDataObject(_cEntry, _sEntry, parentObject);
                    Manager.rewrite(parentObject, fileEntry);
                }
            }
            if(!foundEntry) {
                missingConfigs.add(configEntry);
            }
        }
        if(missingConfigs.isEmpty()) return;
        for(Map.Entry<String, ConfigObject> mapEntry : missingConfigs) {
            IConfig impl = mapEntry.getValue().getConfig();
            impl.deserialize(new JsonObject());
            File newfile = new File(file, mapEntry.getKey() + ".json");
            try {
                newfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            JsonObject c = new JsonObject();
            DisplayEntry.Obj entryC = new DisplayEntry.Obj(c);
            JsonObject s = new JsonObject();
            DisplayEntry.Obj entryS = new DisplayEntry.Obj(s);
            impl.serializeClient(entryC);
            impl.serialize(entryS);
            JsonObject parent = new JsonObject();
            parent.add("client", ConfigHelper.convertToJson(entryC));
            parent.add("common", ConfigHelper.convertToJson(entryS));
            rewrite(parent, newfile);
        }
    }

    @Override
    public void load() {
    }
}
