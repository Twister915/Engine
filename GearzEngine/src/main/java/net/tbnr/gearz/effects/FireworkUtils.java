package net.tbnr.gearz.effects;

import com.google.common.collect.Lists;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.List;
import java.util.Random;

/**
 * Created by Jake on 1/26/14.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public class FireworkUtils {
    static Random random = new Random();
    static List<Color> colors = Lists.newArrayList(Color.AQUA, Color.BLUE, Color.FUCHSIA, Color.GREEN, Color.LIME, Color.MAROON, Color.NAVY, Color.NAVY, Color.OLIVE, Color.ORANGE, Color.PURPLE, Color.RED, Color.TEAL, Color.YELLOW);
    static int FIREWORK_POWER = 0;
    static boolean FIREWORK_TRAIL = false;

    public static Firework getRandomFirework(Location loc) {
        FireworkMeta fireworkMeta = (FireworkMeta) (new ItemStack(Material.FIREWORK)).getItemMeta();
        Firework firework = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);

        fireworkMeta.setPower(FIREWORK_POWER);
        fireworkMeta.addEffect(FireworkEffect.builder()
                .with(FireworkUtils.randomFireworkType())
                .withColor(FireworkUtils.randomColor())
                .trail(FIREWORK_TRAIL)
                .build());

        firework.setFireworkMeta(fireworkMeta);
        return firework;
    }

    public static FireworkEffect.Type randomFireworkType() {
        return FireworkEffect.Type.values()[random.nextInt(FireworkEffect.Type.values().length)];
    }

    public static Color randomColor() {
        return colors.get(random.nextInt(colors.size()));
    }
}