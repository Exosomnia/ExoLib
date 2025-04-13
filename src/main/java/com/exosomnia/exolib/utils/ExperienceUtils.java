package com.exosomnia.exolib.utils;

public class ExperienceUtils {

    public static int totalXPForLevel(int level) {
        if (level > 30) {
            return (int) ((4.5 * (level * level)) - (162.5 * level) + 2220);
        } else if (level > 15) {
            return (int) ((2.5 * (level * level)) - (40.5 * level) + 360);
        } else {
            return (level * level) + (6 * level);
        }
    }

    public static int totalLevelForXP(int xp) {
        if (xp < 352) {
            return (int) ((-6 + Math.sqrt(4 * xp + 36)) / 2);
        } else if (xp > 1507) {
            return (int) ((40.5 + Math.sqrt(10 * xp - 1959.75)) / 5);
        } else {
            return (int) ((162.5 + Math.sqrt(18 * xp - 13553.75)) / 9);
        }
    }
}
