package dev.revivalmoddingteam.recrafted.config;

import toma.config.object.builder.ConfigBuilder;

public class SeasonConfig {

    public int yearCycle = 36;

    public ConfigBuilder init(ConfigBuilder builder) {
        return builder
                .push()
                .name("Season").init()
                .addInt(yearCycle).name("Year cycle").range(24, 156).sliderRendering().add(t -> yearCycle = t.value())
                .pop();
    }
}
