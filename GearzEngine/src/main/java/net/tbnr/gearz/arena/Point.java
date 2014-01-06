package net.tbnr.gearz.arena;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Point class
 */
@EqualsAndHashCode(of = {"x", "y", "z", "yaw", "pitch"}, doNotUseGetters = false)
@ToString(of = {"x", "y", "z", "yaw", "pitch"})
public class Point {
    @Getter
    private double x;
    @Getter
    private double y;
    @Getter
    private double z;
    @Getter
    private float yaw;
    @Getter
    private float pitch;

    public Point(double x, double y, double z) {
        this(x, y, z, 0, 0);
    }

    public Point(double x, double y, double z, float pitch, float yaw) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public Point(int x, int y, int z, int pitch, int yaw) {
        this((double) x, (double) y, (double) z, (float) pitch, (float) yaw);
    }
}
