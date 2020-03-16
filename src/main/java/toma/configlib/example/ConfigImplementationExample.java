package toma.configlib.example;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import toma.configlib.config.IConfig;
import toma.configlib.config.display.DisplayEntry;
import toma.configlib.config.types.ConfigTypeList;
import toma.configlib.config.types.ConfigTypeObject;
import toma.configlib.config.types.IConfigType;
import toma.configlib.config.types.primitives.ConfigTypeString;
import toma.configlib.example.objects.ConfigObjectExample;

import java.util.Arrays;
import java.util.Collections;

/**
 * The main config class, must implement the {@link IConfig} interface in order to be registered
 *
 * @author Toma, 08.03.2020
 */
public class ConfigImplementationExample implements IConfig {

    /** String object, to actually get it you need reference to this field and call {@link IConfigType#get()} in order to get the string */
    public static ConfigTypeString configurableString = ConfigTypeString.create("string", "Hello world!");

    /** Configurable object, must implement the {@link toma.configlib.config.util.Serializable} interface
     *  Call {@link IConfigType#get()} to get the object */
    public static ConfigTypeObject<ConfigObjectExample> configurableObject = ConfigTypeObject.create("object", new ConfigObjectExample(false, 15));

    /** Configurable list of primitives, in this case it's {@link Double}
     *
     *  key = Name displayed in Screen
     *  serializer = converts every list entry into array entry
     *  deserializer = converts {@link com.google.gson.JsonArray} into {@link java.util.List} of proper type
     *  entry = creates new entry instance when user click Add button in Screen */
    public static ConfigTypeList.DecimalNum configurableDoubleList = new ConfigTypeList.DecimalNum("primitive list", Arrays.asList(0.0D, 1.0D));

    /** Same as above, but now with custom serialization for objects
     *  More info at {@link ConfigObjectExample#asArrayEntry(DisplayEntry.Arr)} and {@link ConfigObjectExample#fromArray(JsonArray)}*/
    public static ConfigTypeList.Obj<ConfigObjectExample> configurableObjectList = new ConfigTypeList.Obj<>("object list", Collections.emptyList(), ConfigObjectExample::asArrayEntry, ConfigObjectExample::fromArray, ConfigObjectExample::getDefault);

    /**
     * Serialize each client only entry here
     * @param entry - Client sided object entry
     */
    @Override
    public void serializeClient(DisplayEntry.Obj entry) {

    }

    /**
     * Deserialize each client only entry here
     * @param object - Client sided json entry
     */
    @Override
    public void deserializeClient(JsonObject object) {

    }

    /**
     * Serialize each non-client only entry here
     * @param entry - object entry
     */
    @Override
    public void serialize(DisplayEntry.Obj entry) {
        configurableString.serialize(entry);
        configurableObject.serialize(entry);
        configurableDoubleList.serialize(entry);
        configurableObjectList.serialize(entry);
    }

    /**
     * Deserialize each non-client only entry here
     * @param object - json entry
     */
    @Override
    public void deserialize(JsonObject object) {
        configurableString.deserialize(object);
        configurableObject.deserialize(object);
        configurableDoubleList.deserialize(object);
        configurableObjectList.deserialize(object);
    }
}
