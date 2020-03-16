package dev.revivalmoddingteam.recrafted.config;

import com.google.gson.JsonObject;
import toma.configlib.config.display.DisplayEntry;
import toma.configlib.config.types.primitives.ConfigTypeInt;
import toma.configlib.config.types.primitives.ConfigTypeNumber;
import toma.configlib.config.util.Serializable;

public class SeasonConfig implements Serializable {

    public ConfigTypeInt yearCycle = ConfigTypeInt.create("Year cycle", 36, ConfigTypeNumber.Limiter.of(8, 128));

    @Override
    public void serialize(DisplayEntry.Obj displayEntry) {
        yearCycle.serialize(displayEntry);
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        yearCycle.deserialize(jsonObject);
    }
}
