package toma.configlib.config.types.primitives;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import toma.configlib.config.display.DisplayEntry;
import toma.configlib.config.types.ConfigEntry;

import java.util.function.Function;

public class ConfigTypeNumber<T extends Number> extends ConfigEntry<T> {

    private final Function<JsonElement, T> deserializer;
    private final Limiter limiter;

    protected ConfigTypeNumber(String key, T _default, Limiter limiter, Function<JsonElement, T> deserializer) {
        super(key, _default);
        this.deserializer = deserializer;
        this.limiter = limiter;
    }

    public boolean hasLimiter() {
        return limiter != null;
    }

    public Limiter getLimiter() {
        return limiter;
    }

    @Override
    public void serializeData(DisplayEntry.Obj entry) {
        entry.get().put(getKey(), new DisplayEntry.Num(get(), getLimiter()));
    }

    @Override
    public void deserializeData(JsonObject jsonObject) {
        setValue(jsonObject.has(getKey()) ? deserializer.apply(jsonObject.get(getKey())) : getDefault());
    }

    public static class Limiter {

        protected Number min, max;

        public static Limiter of(Number min, Number max) {
            return new Limiter(min, max);
        }

        private Limiter(Number min, Number max) {
            this.min = min;
            this.max = max;
        }

        public Number getMin() {
            return min;
        }

        public Number getMax() {
            return max;
        }
    }
}
