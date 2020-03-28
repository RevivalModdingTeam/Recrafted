package dev.revivalmoddingteam.recrafted.config;

import com.google.gson.JsonObject;
import toma.configlib.config.display.DisplayEntry;
import toma.configlib.config.types.ConfigTypeObject;
import toma.configlib.config.types.primitives.ConfigTypeDouble;
import toma.configlib.config.types.primitives.ConfigTypeNumber;
import toma.configlib.config.util.Serializable;

public class WorldConfig implements Serializable {

    public ConfigTypeObject<PlantConfig> plants = ConfigTypeObject.create("Plants", new PlantConfig());

    @Override
    public void serialize(DisplayEntry.Obj displayEntry) {
        plants.serialize(displayEntry);
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        plants.deserialize(jsonObject);
    }

    public static class PlantConfig implements Serializable {

        public ConfigTypeDouble blueberry = ConfigTypeDouble.create("Blueberry", 0.80, ConfigTypeNumber._0_1_limiter());
        public ConfigTypeDouble blackberry = ConfigTypeDouble.create("Blackberry", 0.80, ConfigTypeNumber._0_1_limiter());
        public ConfigTypeDouble raspberry = ConfigTypeDouble.create("Raspberry", 0.75, ConfigTypeNumber._0_1_limiter());
        public ConfigTypeDouble snowberry = ConfigTypeDouble.create("Snowberry", 1.0, ConfigTypeNumber._0_1_limiter());
        public ConfigTypeDouble strawberry = ConfigTypeDouble.create("Strawberry", 0.90, ConfigTypeNumber._0_1_limiter());

        @Override
        public void serialize(DisplayEntry.Obj displayEntry) {
            blueberry.serialize(displayEntry);
            blackberry.serialize(displayEntry);
            raspberry.serialize(displayEntry);
            snowberry.serialize(displayEntry);
            strawberry.serialize(displayEntry);
        }

        @Override
        public void deserialize(JsonObject jsonObject) {
            blueberry.deserialize(jsonObject);
            blackberry.deserialize(jsonObject);
            raspberry.deserialize(jsonObject);
            snowberry.deserialize(jsonObject);
            strawberry.deserialize(jsonObject);
        }
    }
}
