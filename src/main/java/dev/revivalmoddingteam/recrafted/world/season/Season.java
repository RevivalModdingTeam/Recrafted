package dev.revivalmoddingteam.recrafted.world.season;

import dev.revivalmoddingteam.recrafted.config.RecraftedConfig;
import dev.revivalmoddingteam.recrafted.world.capability.WorldCapFactory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class Season {

    private final ITextComponent name;
    private final byte index;
    private final int waterColorMultipler, foliageColorMultiplier;
    private final float tempModifier;

    public final int mildSeasonStart, mildSeasonEnd;

    public Season(SeasonBuilder builder) {
        this.name = new TranslationTextComponent("season." + builder.key);
        this.index = builder.id;
        this.waterColorMultipler = builder.waterColor;
        this.foliageColorMultiplier = builder.foliageColor;
        this.tempModifier = builder.temperatureModifier;

        int diff = RecraftedConfig.getSeasonConfiguration().yearCycle.get() / 4;
        mildSeasonStart = diff * index + 1;
        mildSeasonEnd = diff * (index + 1) - 1;
    }

    public int getWaterColorMultipler() {
        return waterColorMultipler;
    }

    public int getFoliageColorMultiplier() {
        return foliageColorMultiplier;
    }

    public float getTemperature(World world) {
        return tempModifier * (isMild(world) ? 0.5F : 1.0F);
    }

    public boolean isMild(World world) {
        int day = WorldCapFactory.getData(world).getSeasonData().getDay(world);
        return day <= mildSeasonStart || day >= mildSeasonEnd;
    }

    public byte getSeasonIndex() {
        return index;
    }

    public boolean isSpring() {
        return index == 0;
    }

    public boolean isSummmer() {
        return index == 1;
    }

    public boolean isFall() {
        return index == 2;
    }

    public boolean isWinter() {
        return index == 3;
    }

    public ITextComponent getName() {
        return name;
    }

    protected static class SeasonBuilder {

        private byte id;
        private String key;
        private float temperatureModifier;
        private int waterColor;
        private int foliageColor;

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

        public Season build() {
            return new Season(this);
        }
    }
}
