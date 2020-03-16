package toma.configlib.config.util;

import com.google.gson.*;
import toma.configlib.ConfigLib;
import toma.configlib.config.ConfigObject;
import toma.configlib.config.display.DisplayEntry;

import java.util.List;
import java.util.Map;

public class ConfigHelper {

    public static JsonObject convertToJson(DisplayEntry.Obj displayEntry) {
        JsonObject jsonObject = new JsonObject();
        Map<String, DisplayEntry<?>> map = displayEntry.get();
        for (Map.Entry<String, DisplayEntry<?>> entry : map.entrySet()) {
            jsonObject.add(entry.getKey(), getObject(entry.getKey(), entry.getValue()));
        }
        return jsonObject;
    }

    public static void reloadConfigs() {
        for(Map.Entry<String, ConfigObject> mapEntry : ConfigLib.CONFIGS.entrySet()) {

        }
    }

    public static JsonElement getObject(String key, DisplayEntry<?> entry) {
        if (entry instanceof DisplayEntry.Obj) {
            JsonObject inn = new JsonObject();
            Map<String, DisplayEntry<?>> innerEntryMap = ((DisplayEntry.Obj) entry).get();
            for (Map.Entry<String, DisplayEntry<?>> innerEntry : innerEntryMap.entrySet()) {
                inn.add(innerEntry.getKey(), getObject(innerEntry.getKey(), innerEntry.getValue()));
            }
            return inn;
        } else if (entry instanceof DisplayEntry.Arr) {
            JsonArray array = new JsonArray();
            List<DisplayEntry<?>> list = ((DisplayEntry.Arr) entry).get();
            for (DisplayEntry<?> de : list) {
                array.add(getObject("Entry", de));
            }
            return array;
        } else if (entry instanceof DisplayEntry.Str) {
            return new JsonPrimitive(((DisplayEntry.Str) entry).get());
        } else if (entry instanceof DisplayEntry.Bool) {
            return new JsonPrimitive(((DisplayEntry.Bool) entry).get());
        } else if (entry instanceof DisplayEntry.Num) {
            return new JsonPrimitive(((DisplayEntry.Num) entry).get());
        }
        return new JsonNull();
    }

    public static <T extends Enum<T>> T getNextEnum(int index, T[] values) {
        if (index < values.length - 1) {
            return values[++index];
        } else return values[0];
    }
}
