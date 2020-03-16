package dev.revivalmoddingteam.recrafted.config;

import com.google.gson.JsonObject;
import toma.configlib.config.IConfig;
import toma.configlib.config.display.DisplayEntry;
import toma.configlib.config.types.ConfigTypeObject;

public class RecraftedConfig implements IConfig {

    public static ConfigTypeObject<SeasonConfig> seasonConfig = ConfigTypeObject.create("Seasons", new SeasonConfig());

    @Override
    public void serializeClient(DisplayEntry.Obj entry) {

    }

    @Override
    public void deserializeClient(JsonObject object) {

    }

    @Override
    public void serialize(DisplayEntry.Obj entry) {
        seasonConfig.serialize(entry);
    }

    @Override
    public void deserialize(JsonObject object) {
        seasonConfig.deserialize(object);
    }

    public static SeasonConfig getSeasonConfiguration() {
        return seasonConfig.get();
    }
}
