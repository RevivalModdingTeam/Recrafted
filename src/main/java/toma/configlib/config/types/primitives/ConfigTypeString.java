package toma.configlib.config.types.primitives;

import com.google.gson.JsonObject;
import toma.configlib.config.display.DisplayEntry;
import toma.configlib.config.types.ConfigEntry;

public class ConfigTypeString extends ConfigEntry<String> {

    public static ConfigTypeString create(String key, String value) {
        return new ConfigTypeString(key, value);
    }

    private ConfigTypeString(String key, String _default) {
        super(key, _default);
    }

    @Override
    public void serializeData(DisplayEntry.Obj entry) {
        entry.get().put(getKey(), new DisplayEntry.Str(get()));
    }

    @Override
    public void deserializeData(JsonObject jsonObject) {
        setValue(jsonObject.has(getKey()) ? jsonObject.get(getKey()).getAsString() : getDefault());
    }
}
