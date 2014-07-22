/*
 * Copyright (c) 2014.
 * CogzMC LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.tbnr.util.render;

import com.google.common.collect.Maps;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ImageToChatBungeeUtil {

    private static final ColorExBungee[] colors = {new ColorExBungee(Color.decode("#000000")), new ColorExBungee(Color.decode("#0000AA")), new ColorExBungee(Color.decode("#00AA00")), new ColorExBungee(Color.decode("#00AAAA")), new ColorExBungee(Color.decode("#AA0000")), new ColorExBungee(Color.decode("#AA00AA")), new ColorExBungee(Color.decode("#FFAA00")), new ColorExBungee(Color.decode("#AAAAAA")), new ColorExBungee(Color.decode("#555555")), new ColorExBungee(Color.decode("#5555FF")), new ColorExBungee(Color.decode("#55FF55")), new ColorExBungee(Color.decode("#55FFFF")), new ColorExBungee(Color.decode("#FF5555")), new ColorExBungee(Color.decode("#FF55FF")), new ColorExBungee(Color.decode("#FF5555")), new ColorExBungee(Color.decode("#FFFFFF"))};
    private static final Map<String, ChatColor> colorHexMap;

    static {
        colorHexMap = Maps.newHashMap();
        colorHexMap.put("000000", ChatColor.BLACK);
        colorHexMap.put("0000AA", ChatColor.DARK_BLUE);
        colorHexMap.put("00AA00", ChatColor.DARK_GREEN);
        colorHexMap.put("00AAAA", ChatColor.DARK_AQUA);
        colorHexMap.put("AA0000", ChatColor.DARK_RED);
        colorHexMap.put("AA00AA", ChatColor.DARK_PURPLE);
        colorHexMap.put("FFAA00", ChatColor.GOLD);
        colorHexMap.put("AAAAAA", ChatColor.GRAY);
        colorHexMap.put("555555", ChatColor.DARK_GRAY);
        colorHexMap.put("5555FF", ChatColor.BLUE);
        colorHexMap.put("55FF55", ChatColor.GREEN);
        colorHexMap.put("55FFFF", ChatColor.AQUA);
        colorHexMap.put("FF5555", ChatColor.RED);
        colorHexMap.put("FF55FF", ChatColor.LIGHT_PURPLE);
        colorHexMap.put("FF5555", ChatColor.YELLOW);
        colorHexMap.put("FFFFFF", ChatColor.WHITE);
    }

	private ImageToChatBungeeUtil() {
	}

	private static ChatColor getColorFor(Color color) {
        String rgb = Integer.toHexString(findClosestColor(new ColorExBungee(color), colors).toRGB());
        rgb = rgb.substring(2, rgb.length()).toUpperCase();
        return colorHexMap.get(rgb);
    }

    public static List<String> getHeadImageWithCenteredText(String player, String text, boolean filledTextShadow) {
        List<String> newList = getHeadImage(player, filledTextShadow);
        String newText = newList.get(3) + centerText(text) + ChatColor.RESET;
        newList.set(3, newText);
        return newList;
    }

    public static List<String> getHeadImage(String player, boolean filledTextShadow) {
        return getTextImage("https://minotar.net/helm/" + player + "/8.png", filledTextShadow);
    }

    public static List<String> getImageWithCenteredText(String urlText, String text, boolean filledTextShadow) {
        List<String> newList = getTextImage(urlText, filledTextShadow);
        String newText = newList.get(3) + centerText(text) + ChatColor.RESET;
        newList.set(3, newText);
        return newList;
    }

    public static List<String> getTextImage(String urlText, boolean filledTextShadow) {
        BufferedImage i = getImageFromURL(urlText);
        List<String> strings = new ArrayList<>();
        for (int y = 0; y < i.getHeight(); y++) {
            StringBuilder builder = new StringBuilder();
            for (int x = 0; x < i.getWidth(); x++) {
                builder.append(getColorFor(getColor(i, x, y))).append(filledTextShadow ? "\u2588" : "\u2593");
            }
            strings.add(builder.toString());
        }
        return strings;
    }

    public static Color getColor(BufferedImage image, int x, int y) {
        if (x < 0 || x >= image.getWidth(null)) {
            throw new IndexOutOfBoundsException("x must be between 0 and " + (image.getWidth(null) - 1));
        }
        if (y < 0 || y >= image.getHeight(null)) {
            throw new IndexOutOfBoundsException("y must be between 0 and " + (image.getHeight(null) - 1));
        }
        return new Color(image.getRGB(x, y));
    }

    public static BufferedImage getImageFromURL(String urlText) {
        try {
            URL url = ImageToChatBungeeUtil.class.getResource(urlText);
            if (url == null) {
                url = new URL(urlText);
            }
            return ImageIO.read(url);
        } catch (IOException e) {
            throw new RuntimeException("Could not load player head");
        }
    }

    public static String centerText(String text) {
        int maxWidth = 55;
        int spaces = (int) Math.round((maxWidth - 1.4 * ChatColor.stripColor(text).length()) / 2);
        return StringUtils.repeat(" ", spaces) + text;
    }

    private static ColorExBungee findClosestColor(ColorExBungee c, ColorExBungee[] palette) {
        if (c.isTransparent()) {
            return ColorExBungee.WHITE;
        }

        double delta = 1.7976931348623157E+308D;
        int result = -1;
        for (int i = 0; i < palette.length; i++) {
            ColorExBungee palC = palette[i];
            double d = ColorExBungee.dist(c, palC);
            if (d < delta) {
                result = i;
                delta = d;
            }
        }

        return result != -1 ? palette[result] : ColorExBungee.WHITE;
    }
}
