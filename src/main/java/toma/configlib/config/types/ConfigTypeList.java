package toma.configlib.config.types;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import toma.configlib.config.display.DisplayEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConfigTypeList<T> extends ConfigEntry<List<T>> {

    private final BiConsumer<T, DisplayEntry.Arr> serializer;
    private final Function<JsonArray, List<T>> deserializer;
    private final Supplier<DisplayEntry<?>> entryFactory;

    private ConfigTypeList(String name, List<T> def, BiConsumer<T, DisplayEntry.Arr> serializer, Function<JsonArray, List<T>> deserializer, Supplier<DisplayEntry<?>> entryFactory) {
        super(name, def);
        this.serializer = serializer;
        this.deserializer = deserializer;
        this.entryFactory = entryFactory;
    }

    public static class Bool extends ConfigTypeList<Boolean> {
        public Bool(String name, List<Boolean> def) {
            super(name, def, Utils::serializeBool, Utils::deserializeBool, DisplayEntry.Bool::new);
        }
    }
    public static class Int extends ConfigTypeList<Integer> {
        public Int(String name, List<Integer> def) {
            super(name, def, Utils::serializeNumber, Utils::deserializeInt, DisplayEntry.Num::new);
        }
    }
    public static class DecimalNum extends ConfigTypeList<Double> {
        public DecimalNum(String name, List<Double> def) {
            super(name, def, Utils::serializeNumber, Utils::deserializeDouble, DisplayEntry.Num::new);
        }
    }
    public static class Str extends ConfigTypeList<String> {
        public Str(String name, List<String> def) {
            this(name, def, () -> "");
        }

        public Str(String name, List<String> def, Supplier<String> defaultDisplayValue) {
            super(name, def, Utils::serializeString, Utils::deserializeString, () -> new DisplayEntry.Str(defaultDisplayValue.get()));
        }
    }
    public static class Obj<T> extends ConfigTypeList<T> {
        public Obj(String name, List<T> def, BiConsumer<T, DisplayEntry.Arr> serializer, Function<JsonArray, List<T>> deserializer, Supplier<DisplayEntry<?>> entrySupplier) {
            super(name, def, serializer, deserializer, entrySupplier);
        }
    }

    @Override
    public void serializeData(DisplayEntry.Obj entry) {
        DisplayEntry.Arr arr = new DisplayEntry.Arr(new JsonArray(), entryFactory);
        get().forEach(t -> serializer.accept(t, arr));
        entry.put(getKey(), arr);
    }

    @Override
    public void deserializeData(JsonObject jsonObject) {
        JsonArray array = jsonObject.has(getKey()) ? jsonObject.getAsJsonArray(getKey()) : null;
        if(array == null) {
            setValue(getDefault());
        } else {
            setValue(deserializer.apply(array));
        }
    }

    private static final class Utils {

        private static void serializeBool(boolean b, DisplayEntry.Arr array) {
            array.get().add(new DisplayEntry.Bool(b));
        }

        private static List<Boolean> deserializeBool(JsonArray array) {
            return deserialize(array, JsonElement::getAsBoolean);
        }

        private static void serializeNumber(Number value, DisplayEntry.Arr array) {
            array.get().add(new DisplayEntry.Num(value));
        }

        private static List<Integer> deserializeInt(JsonArray array) {
            return deserialize(array, JsonElement::getAsInt);
        }

        private static List<Double> deserializeDouble(JsonArray array) {
            return deserialize(array, JsonElement::getAsDouble);
        }

        private static void serializeString(String s, DisplayEntry.Arr array) {
            array.get().add(new DisplayEntry.Str(s));
        }

        private static List<String> deserializeString(JsonArray array) {
            return deserialize(array, JsonElement::getAsString);
        }

        private static <T> List<T> deserialize(JsonArray array, Function<JsonElement, T> function) {
            List<T> list = new ArrayList<>();
            for(JsonElement element : array) {
                list.add(function.apply(element));
            }
            return list;
        }
    }
}
