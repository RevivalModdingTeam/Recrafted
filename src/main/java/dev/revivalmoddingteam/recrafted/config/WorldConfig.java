package dev.revivalmoddingteam.recrafted.config;

import toma.config.object.builder.ConfigBuilder;

public class WorldConfig {

    public PlantConfig plants = new PlantConfig();

    public ConfigBuilder init(ConfigBuilder builder) {
        return builder
                .push()
                .name("World")
                .init()
                .exec(plants::init)
                .pop();
    }

    public static class PlantConfig {

        public float blueberry = 0.8F;
        public float blackberry = 0.8F;
        public float raspberry = 0.75F;
        public float snowberry = 1.0F;
        public float strawberry = 0.9F;

        public ConfigBuilder init(ConfigBuilder builder) {
            return builder
                    .push()
                    .name("Plants")
                    .init()
                    .addFloat(blueberry).name("Blueberry").range(0.0F, 1.0F).sliderRendering().add(t -> blueberry = t.value())
                    .addFloat(blackberry).name("Blackberry").range(0.0F, 1.0F).sliderRendering().add(t -> blackberry = t.value())
                    .addFloat(raspberry).name("Raspberry").range(0.0F, 1.0F).sliderRendering().add(t -> raspberry = t.value())
                    .addFloat(snowberry).name("Snowberry").range(0.0F, 1.0F).sliderRendering().add(t -> snowberry = t.value())
                    .addFloat(strawberry).name("Strawberry").range(0.0F, 1.0F).sliderRendering().add(t -> strawberry = t.value())
                    .pop();
        }
    }
}
