package dev.revivalmoddingteam.recrafted.util.helper;

import dev.revivalmoddingteam.recrafted.world.capability.WorldCapFactory;
import dev.revivalmoddingteam.recrafted.world.season.Season;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class TemperatureHelper {

    public static float getTemperatureAt(World world, BlockPos pos) {
        Biome biome = world.getBiome(pos);
        Season season = WorldCapFactory.getData(world).getSeasonData().getSeason();
        float baseTemperature = biome.getTemperature(pos) + season.getTemperature(world);
        float modifier = baseTemperature < 0 ? -2.5F : 2.5F;
        int time = (int) world.getDayTime();
        float f0 = (time / 12000F) * 180;
        float f1 = (float) Math.sin(f0 / 180.0F * Math.PI);
        float f2 = (baseTemperature / modifier) * f1;
        float timeTemp = baseTemperature + f2;
        float lerp = 0.5F;
        float heightTemp = timeTemp - lerp * Math.abs((world.getSeaLevel() - pos.getY()) / (float) world.getSeaLevel());
        float finalTemperature = heightTemp + (world.getLightFor(LightType.BLOCK, pos) / 15.0F) * Math.abs(baseTemperature);
        if(world.isRainingAt(pos)) {
            finalTemperature *= 0.85F;
        }
        return finalTemperature;
    }
}
