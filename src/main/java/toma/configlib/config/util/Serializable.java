package toma.configlib.config.util;

import com.google.gson.JsonObject;
import toma.configlib.config.display.DisplayEntry;

public interface Serializable {

    void serialize(DisplayEntry.Obj displayEntry);

    void deserialize(JsonObject jsonObject);
}
