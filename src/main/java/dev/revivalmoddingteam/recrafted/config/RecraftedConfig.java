package dev.revivalmoddingteam.recrafted.config;

import toma.config.IConfig;
import toma.config.datatypes.ConfigObject;
import toma.config.event.ConfigUpdateEvent;
import toma.config.object.builder.ConfigBuilder;

public class RecraftedConfig implements IConfig {

    public static SeasonConfig seasonConfig = new SeasonConfig();
    public static WorldConfig worldConfig = new WorldConfig();

    @Override
    public ConfigObject getConfig(ConfigBuilder builder) {
        return builder
                .exec(seasonConfig::init)
                .exec(worldConfig::init)
                .build();
    }
}
