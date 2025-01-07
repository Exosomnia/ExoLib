package com.exosomnia.exolib.utils;

public class ColorUtils {

    public static float[] intToFloats(int color) {
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;
        return new float[]{red, green, blue};
    }

    public static int floatsToInt(float[] color) {
        int output = ((int)(color[0] * 255) << 16);
        output = ((int)(color[1] * 255) << 8) | output;
        output = (int)(color[2] * 255) | output;
        return output;
    }
}
