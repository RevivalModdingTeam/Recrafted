package dev.revivalmoddingteam.recrafted.world.biome;

public class BiomeType {

    public static final BiomeType FREEZING = new BiomeType(true, -1.0F);
    public static final BiomeType COLD = new BiomeType(true, -0.2F);
    public static final BiomeType MILD = new BiomeType(true, 0.7F);
    public static final BiomeType WARM = new BiomeType(true, 1.1F);
    public static final BiomeType HOT = new BiomeType(false, 2.0F);

    protected final boolean freezeWater;
    protected final float averageTemperature;

    public BiomeType(final boolean freezeWater, final float averageTemperature) {
        this.freezeWater = freezeWater;
        this.averageTemperature = averageTemperature;
    }
}
