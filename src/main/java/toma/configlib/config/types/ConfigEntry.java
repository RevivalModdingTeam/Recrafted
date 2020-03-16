package toma.configlib.config.types;

import com.google.gson.JsonObject;
import toma.configlib.config.display.DisplayEntry;
import toma.configlib.config.util.Serializable;

public abstract class ConfigEntry<T> implements IConfigType<T>, Serializable {

    private final String key;
    private final T _default;
    private String[] comment;
    private T value;

    protected ConfigEntry(String key, T _default) {
        this.key = key;
        this._default = _default;
    }

    public abstract void serializeData(DisplayEntry.Obj entry);

    public abstract void deserializeData(JsonObject jsonObject);

    @Override
    public final void serialize(DisplayEntry.Obj entry) {
        serializeData(entry);
    }

    @Override
    public final void deserialize(JsonObject jsonObject) {
        deserializeData(jsonObject);
    }

    @Override
    public T get() {
        return value == null ? value = getDefault() : value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getDefault() {
        return _default;
    }

    public String[] getComment() {
        return comment;
    }

    @Override
    public String getKey() {
        return key;
    }
}
