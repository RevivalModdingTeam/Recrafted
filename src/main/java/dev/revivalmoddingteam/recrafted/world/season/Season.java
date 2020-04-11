package dev.revivalmoddingteam.recrafted.world.season;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class Season {

    private final ITextComponent name;
    private final byte index;
    private final int waterColorMultipler, foliageColorMultiplier;
    private final float tempModifier;
    private final boolean forceChunkReload;

    public Season(SeasonBuilder builder) {
        this.name = new TranslationTextComponent("season." + builder.key);
        this.index = builder.id;
        this.waterColorMultipler = builder.waterColor;
        this.foliageColorMultiplier = builder.foliageColor;
        this.tempModifier = builder.temperatureModifier;
        this.forceChunkReload = builder.forceChunkUpdate;
    }

    public int getWaterColorMultipler() {
        return waterColorMultipler;
    }

    public int getFoliageColorMultiplier() {
        return foliageColorMultiplier;
    }

    public float getTemperature() {
        return tempModifier;
    }

    public byte getSeasonIndex() {
        return index;
    }

    public boolean isSpring() {
        return isInRange(index, 0, 2);
    }

    public boolean isSummmer() {
        return isInRange(index, 3, 5);
    }

    public boolean isFall() {
        return isInRange(index, 6, 8);
    }

    public boolean isWinter() {
        return isInRange(index, 9, 11);
    }

    public ITextComponent getName() {
        return name;
    }

    public boolean updatesChunks() {
        return forceChunkReload;
    }

    private boolean isInRange(int i, int min, int max) {
        return i >= min && i <= max;
    }

    protected static class SeasonBuilder {

        private byte id;
        private String key;
        private float temperatureModifier;
        private int waterColor;
        private int foliageColor;
        private boolean forceChunkUpdate;

        private SeasonBuilder() {}

        public static SeasonBuilder create() {
            return new SeasonBuilder();
        }

        public SeasonBuilder key(String key) {
            this.key = key;
            return this;
        }

        public SeasonBuilder id(int id) {
            this.id = (byte) id;
            return this;
        }

        public SeasonBuilder colors(int waterColor, int foliageColor) {
            this.waterColor = waterColor;
            this.foliageColor = foliageColor;
            return this;
        }

        public SeasonBuilder tempModifier(final float temperatureModifier) {
            this.temperatureModifier = temperatureModifier;
            return this;
        }

        public SeasonBuilder updatesChunks() {
            this.forceChunkUpdate = true;
            return this;
        }

        public Season build() {
            return new Season(this);
        }
    }
}
