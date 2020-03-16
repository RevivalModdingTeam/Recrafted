package dev.revivalmoddingteam.recrafted.util.helper;

public class ColorHelper {

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

    private static float mix(float a, float b) {
        return Math.min(a, b) + Math.abs(a - b) / 2.0F;
    }
}
