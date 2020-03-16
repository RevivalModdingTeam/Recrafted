package toma.configlib.config.types;

import com.google.gson.JsonObject;
import toma.configlib.config.display.DisplayEntry;
import toma.configlib.config.util.Serializable;

public class ConfigTypeObject<T extends Serializable> extends ConfigEntry<T> {

    private ConfigTypeObject(String key, T _default) {
        super(key, _default);
    }

    @Override
    public void serializeData(DisplayEntry.Obj entry) {
        DisplayEntry.Obj sub = new DisplayEntry.Obj(new JsonObject());
        get().serialize(sub);
        entry.get().put(getKey(), sub);
    }

    @Override
    public void deserializeData(JsonObject jsonObject) {
        JsonObject sub = jsonObject.has(getKey()) ? jsonObject.getAsJsonObject(getKey()) : new JsonObject();
        T t = get();
        if(t == null) {
            t = getDefault();
        }
        t.deserialize(sub);
    }

    public static <T extends Serializable> ConfigTypeObject<T> create(String key, T value) {
        return new ConfigTypeObject<>(key, value);
    }
}
