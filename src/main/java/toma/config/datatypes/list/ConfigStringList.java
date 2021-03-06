package toma.config.datatypes.list;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import toma.config.datatypes.IConfigType;

import java.util.List;
import java.util.function.Consumer;

public class ConfigStringList extends ConfigList<String> {

    public ConfigStringList(String name, String comment, List<String> value, boolean fixed, Consumer<IConfigType<List<String>>> consumer) {
        super(name, comment, value, fixed, consumer, JsonArray::add, JsonElement::getAsString, () -> "");
    }
}
