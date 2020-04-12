package dev.revivalmoddingteam.recrafted.util.helper;

import dev.revivalmoddingteam.recrafted.world.capability.WorldCapFactory;
import dev.revivalmoddingteam.recrafted.world.season.Season;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.*;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.Collection;
import java.util.function.BiPredicate;

public class ModHelper {

    /* COLORS */

    public static float[] f_colorRGB(int from) {
        float[] array = new float[3];
        array[0] = ((from >> 16) & 0xff) / 255.0F;
        array[1] = ((from >>  8) & 0xff) / 255.0F;
        array[2] =  (from        & 0xff) / 255.0F;
        return array;
    }

    public static int inv_mix(int colorA, int colorB) {
        int a = 0xFFFFFF - Math.abs(colorA);
        int r = (((a >> 16) & 0xff) + ((colorB >> 16) & 0xff)) / 2;
        int g = (((a >>  8) & 0xff) + ((colorB >>  8) & 0xff)) / 2;
        int b = ((a & 0xff) + (colorB & 0xff)) / 2;
        return (r << 16) | (g << 8) | b;
    }

    public static int getColorRGB(float red, float green, float blue) {
        return (int)(red * 256.0F) << 16 | (int)(green * 256.0F) << 8 | (int)(blue * 256.0F);
    }

    /* TEMPERATURES */

    public static float getTemperatureAt(World world, BlockPos pos) {
        Biome biome = world.getBiome(pos);
        Season season = WorldCapFactory.getData(world).getSeasonData().getSeason();
        float baseTemperature = biome.getTemperature(pos);
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

    /* WORLD */

    public static RayTraceResult rayTrace(World world, Entity entity, RayTraceContext.FluidMode fluidMode, double maxDistance) {
        float yaw = entity.rotationYaw;
        float pitch = entity.rotationPitch;
        float cosYaw = MathHelper.cos(-yaw * (float) ((Math.PI / 180f) - Math.PI));
        float sinYaw = MathHelper.sin(-yaw * (float) ((Math.PI / 180f) - Math.PI));
        float cosPitch = -MathHelper.cos(-pitch * (float) (Math.PI / 180f));
        float sinPitch = MathHelper.sin(-pitch * (float) (Math.PI / 180f));
        float x = sinYaw * cosPitch;
        float z = cosYaw * cosPitch;
        Vec3d eyeLoc = entity.getEyePosition(1.0F);
        Vec3d targetLoc = eyeLoc.add(x * maxDistance, sinPitch * maxDistance, z * maxDistance);
        return world.rayTraceBlocks(new RayTraceContext(eyeLoc, targetLoc, RayTraceContext.BlockMode.OUTLINE, fluidMode, entity));
    }

    public static void spawnEntityWithRandomMotion(World world, Entity entity, int modifier) {
        if(!world.isRemote) {
            entity.setMotion(world.rand.nextDouble() / modifier - world.rand.nextDouble() / modifier, world.rand.nextDouble() / modifier, world.rand.nextDouble() / modifier - world.rand.nextDouble() / modifier);
            world.addEntity(entity);
        }
    }

    /* OTHER */

    public static <T1, T2> boolean isPresent(T1 check, T2[] array, BiPredicate<T1, T2> comparing) {
        for(T2 t : array) {
            if(comparing.test(check, t)) {
                return true;
            }
        }
        return false;
    }

    public static <T1, T2> boolean isPresent(T1 check, Iterable<T2> iterable, BiPredicate<T1, T2> comparing) {
        for(T2 t : iterable) {
            if(comparing.test(check, t)) {
                return true;
            }
        }
        return false;
    }
}
