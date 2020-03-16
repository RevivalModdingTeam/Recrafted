package toma.configlib.config.types.primitives;

import com.google.gson.JsonElement;

public class ConfigTypeInt extends ConfigTypeNumber<Integer> {

    public static ConfigTypeInt create(String key, int value, Limiter limiter) {
        return new ConfigTypeInt(key, value, limiter);
    }

    public static ConfigTypeInt create(String key, int value) {
        return new ConfigTypeInt(key, value);
    }

    private ConfigTypeInt(String key, int _default) {
        this(key, _default, null);
    }

    private ConfigTypeInt(String key, int _default, Limiter limiter) {
        super(key, _default, limiter, ConfigTypeInt::load);
    }

    private static int load(JsonElement element) {
        return element.getAsInt();
    }
}
