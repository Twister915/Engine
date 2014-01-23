package net.tbnr.util;

import net.tbnr.gearz.Gearz;
import org.bukkit.Location;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.Random;

public class RandomUtils {

    public static String getRandomString(Integer length) {
        return (new BigInteger(130, Gearz.getRandom()).toString(length));
    }

    /**
     * IDK Someone WHO HAS IDEA FINISH IT :P
     * xD
     * lol
     *
     * @param location
     * @return
     */
    public static Location getSpreadedLocation(Location location) {
        Random random = new Random();
        int xRangeMin = 0;
        int xRangeMax = 0;
        int zRangeMin = 0;
        int zRangeMax = 0;
        double x = xRangeMin >= xRangeMax ? xRangeMin : random.nextDouble() * (xRangeMax - xRangeMin) + xRangeMin;
        double z = zRangeMin >= zRangeMax ? zRangeMin : random.nextDouble() * (zRangeMax - zRangeMin) + zRangeMin;
        return new Location(location.getWorld(), x, location.getY(), z, location.getYaw(), location.getPitch());
    }

    public static <T> T[] concatenate(T[] A, T[] B) {
        int aLen = A.length;
        int bLen = B.length;

        @SuppressWarnings("unchecked") T[] C = (T[]) Array.newInstance(A.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(A, 0, C, 0, aLen);
        System.arraycopy(B, 0, C, aLen, bLen);

        return C;
    }
}
