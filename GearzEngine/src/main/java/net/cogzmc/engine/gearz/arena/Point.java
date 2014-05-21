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

package net.cogzmc.engine.gearz.arena;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Point class
 */
@EqualsAndHashCode(of = {"x", "y", "z", "yaw", "pitch"}, doNotUseGetters = false)
@ToString(of = {"x", "y", "z", "yaw", "pitch"})
public final class Point {
    @Getter
    private final double x;
    @Getter
    private final double y;
    @Getter
    private final double z;
    @Getter
    private final float yaw;
    @Getter
    private final float pitch;

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

    public Location toLocation(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }
}
