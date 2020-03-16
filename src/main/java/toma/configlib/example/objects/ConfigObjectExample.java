package toma.configlib.example.objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import toma.configlib.config.display.DisplayEntry;
import toma.configlib.config.types.primitives.ConfigTypeBoolean;
import toma.configlib.config.types.primitives.ConfigTypeInt;
import toma.configlib.config.types.primitives.ConfigTypeNumber;
import toma.configlib.config.util.Serializable;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom config object class
 * Every entry in custom objects should be non-static, otherwise it won't work for multiple objects ;)
 * But hey, you know Java, right? ^_^
 *
 * @author Toma, 08.03.2020
 */
public class ConfigObjectExample implements Serializable {

    /** Default list entries for {@link toma.configlib.config.types.ConfigTypeList} implementation */
    public static final List<ConfigObjectExample> DEFAULT = new ArrayList<>(0);

    /** Configurable boolean */
    public final ConfigTypeBoolean someBoolean;
    /** Configurable integer */
    public final ConfigTypeInt someInteger;

    /** Constructor */
    public ConfigObjectExample(boolean someBoolValue, int someIntValue) {
        someBoolean = ConfigTypeBoolean.create("some boolean", someBoolValue);
        someInteger = ConfigTypeInt.create("some int", someIntValue, ConfigTypeNumber.Limiter.of(0, 15));
    }

    /**
     * Implementation of {@link Serializable#serialize(DisplayEntry.Obj)}
     * @param displayEntry - entry
     */
    @Override
    public void serialize(DisplayEntry.Obj displayEntry) {
        someBoolean.serialize(displayEntry);
        someInteger.serialize(displayEntry);
    }

    /**
     * Implementation of {@link Serializable#deserialize(JsonObject)}
     * @param jsonObject - object
     */
    @Override
    public void deserialize(JsonObject jsonObject) {
        someBoolean.setValue(jsonObject.getAsJsonPrimitive("some boolean").getAsBoolean());
        someInteger.setValue(jsonObject.getAsJsonPrimitive("some int").getAsInt());
    }

    /** Serializer for {@link toma.configlib.config.types.ConfigTypeList} */
    public void asArrayEntry(DisplayEntry.Arr arr) {
        arr.get().add(asObject());
    }

    /** Deserializer for {@link toma.configlib.config.types.ConfigTypeList} */
    public static List<ConfigObjectExample> fromArray(JsonArray array) {
        List<ConfigObjectExample> list = new ArrayList<>();
        for(JsonElement element : array) {
            JsonObject jsonObject = element.getAsJsonObject();
            list.add(new ConfigObjectExample(jsonObject.get("some boolean").getAsBoolean(), jsonObject.get("some int").getAsInt()));
        }
        return list;
    }

    /**
     * Converts this instance into DisplayEntry
     */
    public DisplayEntry.Obj asObject() {
        DisplayEntry.Obj obj = new DisplayEntry.Obj();
        obj.put(someBoolean.getKey(), new DisplayEntry.Bool(someBoolean.get()));
        obj.put(someInteger.getKey(), new DisplayEntry.Num(someInteger.get()));
        return obj;
    }

    /** Used as list entry factory */
    public static DisplayEntry.Obj getDefault() {
        return new ConfigObjectExample(false, 10).asObject();
    }
}
