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

package net.tbnr.gearz.packets;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import lombok.Getter;
import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.packets.wrapper.WrapperPlayServerEntityDestroy;
import net.tbnr.gearz.packets.wrapper.WrapperPlayServerEntityMetadata;
import net.tbnr.gearz.packets.wrapper.WrapperPlayServerSpawnEntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class FakeEntity {
    private static int NEXT_ID = 6000;

    @Getter
    public final int id = NEXT_ID++;
    @Getter
    public String customName;
    @Getter
    public boolean created;

    @Getter
    public final Player player;
    @Getter
    public final EntityType type;
    @Getter
    public int health;
    @Getter
    public Location location;
    @Getter
    public EntityFlags flag;

    private final WrappedDataWatcher watcher;

    public enum EntityFlags {
        ON_FIRE, CROUCHED, SPRINTING, EATING_DRINKING_BLOCKING, INVISIBLE, NONE
    }

    public FakeEntity(Player player, EntityType type, int health, Location location, EntityFlags flag) {
        this.player = player;
        this.type = type;
        this.health = health;
        this.location = location;
        this.flag = flag;
        watcher = new WrappedDataWatcher();
    }

    public void setEntityFlag(EntityFlags flag) {
        this.flag = flag;
        update();
    }

    public void setLocation(Location location) {
        this.location = location;
        update();
    }

    public void setHealth(int health) {
        this.health = health;
        update();
    }

    public void setCustomName(String name) {
        this.customName = name;
        update();
    }

    public void create() {
        if (!created) {
            createEntity();
        } else {
            destroy();

            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Gearz.getInstance(), new Runnable() {
                @Override
                public void run() {
                    createEntity();
                }
            }, 10L);
        }
    }

    public void destroy() {
        if (created) {
            WrapperPlayServerEntityDestroy destroyMe = new WrapperPlayServerEntityDestroy();
            destroyMe.setEntities(new int[]{id});

            destroyMe.sendPacket(player);
            created = false;
        }
    }

    private void update() {
        if (created) {
            updateWatcher();
            WrapperPlayServerEntityMetadata update = new WrapperPlayServerEntityMetadata();

            update.setEntityId(id);
            update.setEntityMetadata(watcher.getWatchableObjects());
            update.sendPacket(player);
        }
    }

    private void createEntity() {
        updateWatcher();

        WrapperPlayServerSpawnEntityLiving spawnMob = new WrapperPlayServerSpawnEntityLiving();

        spawnMob.setEntityID(id);
        spawnMob.setType(type);
        spawnMob.setX(location.getX());
        spawnMob.setY(location.getY());
        spawnMob.setZ(location.getZ());
        spawnMob.setYaw(((location.getYaw() * 256.0F) / 360.0F));
        spawnMob.setHeadPitch(((location.getPitch() * 256.0F) / 360.0F));
        spawnMob.setMetadata(watcher);

        spawnMob.sendPacket(player);

        created = true;
    }

    private byte getFlag(EntityFlags flag) {
        if (flag == EntityFlags.ON_FIRE) {
            return 0x01;
        } else if (flag == EntityFlags.CROUCHED) {
            return 0x02;
        } else if (flag == EntityFlags.SPRINTING) {
            return 0x08;
        } else if (flag == EntityFlags.EATING_DRINKING_BLOCKING) {
            return 0x10;
        } else if (flag == EntityFlags.INVISIBLE) {
            return 0x20;
        } else if (flag == EntityFlags.NONE) {
            return (byte) 0;
        } else {
            return (byte) 0;
        }
    }

    private void updateWatcher() {
        watcher.setObject(0, getFlag(flag));
        watcher.setObject(6, (float) health);
        watcher.setObject(7, 0);
        watcher.setObject(8, (byte) 0);

        if (customName != null) {
            watcher.setObject(10, customName);
            watcher.setObject(11, (byte) 1);
        } else {
            watcher.setObject(11, (byte) 0);
        }
    }
}
