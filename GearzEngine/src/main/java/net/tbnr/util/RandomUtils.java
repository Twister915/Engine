package net.tbnr.util;

import com.comphenix.protocol.injector.BukkitUnwrapper;
import com.comphenix.protocol.reflect.accessors.Accessors;
import net.tbnr.gearz.Gearz;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigInteger;

public class RandomUtils implements GUtility {

    public static String getRandomString(Integer length) {
        return (new BigInteger(130, Gearz.getRandom()).toString(length));
    }

    /**
     * Get's the spread location
     *
     * @param location the location to test
     * @return the spread location
     * @deprecated Not finishied
     */
    public static Location getSpreadedLocation(Location location) {
       /* Random random = new Random();
        int xRangeMin = 0;
        int xRangeMax = 0;
        int zRangeMin = 0;
        int zRangeMax = 0;
        double x = xRangeMin >= xRangeMax ? xRangeMin : random.nextDouble() * (xRangeMax - xRangeMin) + xRangeMin;
        double z = zRangeMin >= zRangeMax ? zRangeMin : random.nextDouble() * (zRangeMax - zRangeMin) + zRangeMin;
        return new Location(location.getWorld(), x, location.getY(), z, location.getYaw(), location.getPitch());*/
	    return null;
    }

    public static <T> T[] concatenate(T[] A, T[] B) {
        int aLen = A.length;
        int bLen = B.length;

        @SuppressWarnings("unchecked") T[] C = (T[]) Array.newInstance(A.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(A, 0, C, 0, aLen);
        System.arraycopy(B, 0, C, aLen, bLen);

        return C;
    }

    // Default Collision info
    // width - 0.6
    // length - 1.8

    public static void setPlayerCollision(Player player, Boolean collision) {
        Object entityPlayer = BukkitUnwrapper.getInstance().unwrapItem(player);
        Field width = Accessors.getFieldAccessor(entityPlayer.getClass(), "width", true).getField();
        Field length = Accessors.getFieldAccessor(entityPlayer.getClass(), "length", true).getField();
        try {
            width.set(entityPlayer, collision ? 0.6F : 0.0F);
            length.set(entityPlayer, collision ? 1.8F : 0.0F);
        } catch (IllegalAccessException ignored) {
        }
    }
}
