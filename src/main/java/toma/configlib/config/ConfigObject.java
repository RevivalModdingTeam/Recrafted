package toma.configlib.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import toma.configlib.ConfigLib;
import toma.configlib.config.display.DisplayEntry;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ConfigObject {

    private final IConfig config;
    private JsonObject object;
    private Map<String, DisplayEntry<?>> entryMap = new HashMap<>();

    private ConfigObject(Builder builder) {
        this.config = builder.config;
        this.object = builder.data;
    }

    public IConfig getConfig() {
        return config;
    }

    public JsonObject getObject() {
        return object;
    }

    public Map<String, DisplayEntry<?>> getEntryMap() {
        return entryMap;
    }

    public void updateDataObject(@Nullable DisplayEntry.Obj client, DisplayEntry.Obj common, JsonObject object) {
        this.object = object;
        entryMap.put("client", client);
        entryMap.put("common", common);
    }

    private void refreshDisplayData() {
        long time = System.currentTimeMillis();
        ConfigLib.log.debug("Started parsing display data");
        for(Map.Entry<String, JsonElement> entry : object.entrySet()) {
            entryMap.put(entry.getKey(), getEntry(entry.getValue()));
        }
        ConfigLib.log.debug("Data parsing finished after {} ms", System.currentTimeMillis() - time);
    }

    public static DisplayEntry<?> getEntry(JsonElement element) {
        if(element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if(primitive.isBoolean()) {
                return new DisplayEntry.Bool(primitive.getAsBoolean());
            } else if(primitive.isString()) {
                return new DisplayEntry.Str(primitive.getAsString());
            } else if(primitive.isNumber()) {
                return new DisplayEntry.Num(primitive.getAsNumber());
            } else {
                throw new IllegalArgumentException("Invalid config entry: " + element.toString());
            }
        } else if(element.isJsonArray()) {
            return new DisplayEntry.Arr(element.getAsJsonArray(), null);
        } else if(element.isJsonObject()) {
            return new DisplayEntry.Obj(element.getAsJsonObject());
        } else {
            throw new IllegalArgumentException("Invalid config entry: " + element.toString());
        }
    }

    public static class Builder {

        private IConfig config;
        private JsonObject data;

        public Builder impl(IConfig instance) {
            this.config = instance;
            return this;
        }

        public Builder onBuild(JsonObject data) {
            this.data = data;
            return this;
        }

        public ConfigObject build() {
            return new ConfigObject(this);
        }
    }
}
