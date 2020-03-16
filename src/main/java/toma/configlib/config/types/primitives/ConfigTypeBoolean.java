package toma.configlib.config.types.primitives;

import com.google.gson.JsonObject;
import toma.configlib.config.display.DisplayEntry;
import toma.configlib.config.types.ConfigEntry;

public class ConfigTypeBoolean extends ConfigEntry<Boolean> {

    private ConfigTypeBoolean(String name, boolean _default) {
        super(name, _default);
    }

    @Override
    public void serializeData(DisplayEntry.Obj entry) {
        entry.get().put(getKey(), new DisplayEntry.Bool(get()));
    }

    @Override
    public void deserializeData(JsonObject jsonObject) {
        setValue(jsonObject.has(getKey()) ? jsonObject.get(getKey()).getAsBoolean() : getDefault());
    }

    public static ConfigTypeBoolean create(String name, boolean defaultValue) {
        return new ConfigTypeBoolean(name, defaultValue);
    }
}
