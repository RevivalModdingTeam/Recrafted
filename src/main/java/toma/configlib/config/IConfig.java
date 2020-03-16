package toma.configlib.config;

import com.google.gson.JsonObject;
import toma.configlib.config.display.DisplayEntry;

public interface IConfig {

    void serializeClient(DisplayEntry.Obj entry);

    void deserializeClient(JsonObject object);

    void serialize(DisplayEntry.Obj entry);

    void deserialize(JsonObject object);
}
