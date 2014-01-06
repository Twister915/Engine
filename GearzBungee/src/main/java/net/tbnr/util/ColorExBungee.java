package net.tbnr.util;

import java.awt.*;

public class ColorExBungee {
    public static final ColorExBungee WHITE = new ColorExBungee(Color.decode("#000000"));
    private int m_r;
    private int m_g;
    private int m_b;
    private int m_a;

    public ColorExBungee(int c) {
        this(new Color(c, true));
    }

    public ColorExBungee(Color c) {
        this.m_r = c.getRed();
        this.m_g = c.getGreen();
        this.m_b = c.getBlue();
        this.m_a = c.getAlpha();
    }

    private ColorExBungee(int a, int r, int g, int b) {
        this.m_r = r;
        this.m_g = g;
        this.m_b = b;
        this.m_a = a;
    }

    public boolean isTransparent() {
        return this.m_a < 128;
    }

    public static ColorExBungee add(ColorExBungee c1, ColorExBungee c2) {
        return new ColorExBungee(c1.m_a + c2.m_a, c1.m_r + c2.m_r, c1.m_g + c2.m_g, c1.m_b + c2.m_g);
    }

    public static ColorExBungee sub(ColorExBungee c1, ColorExBungee c2) {
        return new ColorExBungee(c1.m_a - c2.m_a, c1.m_r - c2.m_r, c1.m_g - c2.m_g, c1.m_b - c2.m_g);
    }

    public static ColorExBungee mul(ColorExBungee c, double m) {
        return new ColorExBungee((int) (c.m_a * m), (int) (c.m_r * m), (int) (c.m_g * m), (int) (c.m_b * m));
    }

    public static double dist(ColorExBungee c1, ColorExBungee c2) {
        double rmean = (c1.m_r + c2.m_r) / 2.0D;
        double r = c1.m_r - c2.m_r;
        double g = c1.m_g - c2.m_g;
        int b = c1.m_b - c2.m_b;
        double weightR = 2.0D + rmean / 256.0D;
        double weightG = 4.0D;
        double weightB = 2.0D + (255.0D - rmean) / 256.0D;
        return weightR * r * r + weightG * g * g + weightB * b * b;
    }

    public static ColorExBungee clamp(ColorExBungee c) {
        return new ColorExBungee(clamp(c.m_a), clamp(c.m_r), clamp(c.m_g), clamp(c.m_b));
    }

    public Color toColor() {
        return new Color(clamp(this.m_r), clamp(this.m_g), clamp(this.m_b), clamp(this.m_a));
    }

    public int toRGB() {
        return toColor().getRGB();
    }

    private static int clamp(int c) {
        return Math.max(0, Math.min(255, c));
    }
}
