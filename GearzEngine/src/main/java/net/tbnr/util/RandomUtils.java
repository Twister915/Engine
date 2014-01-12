package net.tbnr.util;

import net.tbnr.gearz.Gearz;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.Wool;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomUtils {

    public static ItemStack colorizeLeather(Material material, Color color) {
        ItemStack item = new ItemStack(material);
        if (material == Material.LEATHER_BOOTS || material == Material.LEATHER_LEGGINGS || material == Material.LEATHER_CHESTPLATE || material == Material.LEATHER_HELMET) {
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            meta.setColor(color);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack colorizeWool(DyeColor color){
        Wool wool = new Wool(color);
        return wool.toItemStack();
    }

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

    public static List<String> colorStringList(List<String> strings) {
        ArrayList<String> string = new ArrayList<>();
        for (String s : strings) {
            string.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        return string;
    }
}
