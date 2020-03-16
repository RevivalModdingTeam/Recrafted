package toma.configlib.config.types.primitives;

import com.google.gson.JsonElement;

public class ConfigTypeDouble extends ConfigTypeNumber<Double> {

    public static ConfigTypeDouble create(String key, double value) {
        return new ConfigTypeDouble(key, value);
    }

    public static ConfigTypeDouble create(String key, double value, Limiter limiter) {
        return new ConfigTypeDouble(key, value, limiter);
    }

    private ConfigTypeDouble(String key, double _default) {
        this(key, _default, null);
    }

    private ConfigTypeDouble(String key, double _default, Limiter limiter) {
        super(key, _default, limiter, ConfigTypeDouble::load);
    }

    private static double load(JsonElement element) {
        return element.getAsDouble();
    }
}
