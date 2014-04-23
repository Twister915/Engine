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

package net.tbnr.gearz;

import lombok.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joey on 12/19/13.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
@RequiredArgsConstructor()
@EqualsAndHashCode
@Data
@ToString
public class SkullTask implements Runnable {

    @NonNull
    private final World world;
    @NonNull
    private final SkullType skullType;
    @NonNull
    private final Integer numberOfBlocksEach;
    @NonNull
    private final Location minimum;
    @NonNull
    private final Location maximum;
    @NonNull
    private final SkullDelegate delegate;

    private SkullCursor x;

    @Getter
    private final List<Block> blocksFound = new ArrayList<>();

    public void setup() {
        this.x = new SkullCursor(minimum.getBlockX(), maximum.getBlockX(), numberOfBlocksEach);
        this.reschedule();
    }

    @Override
    public void run() {
        if (doneCheck()) {
            this.delegate.onComplete(this);
            return;
        }
        while (x.shouldContinue()) {
            int x = this.x.getNext();
            for (int y = minimum.getBlockY(); y < maximum.getBlockY(); y++) {
                for (int z = minimum.getBlockZ(); z < maximum.getBlockZ(); z++) {
                    Block blockAt = this.world.getBlockAt(x, y, z);
                    if (blockAt.getType() != Material.SKULL) continue;
                    Skull skull = (Skull) blockAt.getState();
                    if (skull.getSkullType() != skullType) continue;
                    this.delegate.locatedBlock(blockAt);
                    this.blocksFound.add(blockAt);
                }
            }
        }
        this.x.nextSession();
        reschedule();
    }

    private boolean doneCheck() {
        return (x.isDone());
    }

    private void reschedule() {
        Bukkit.getScheduler().runTaskLater(GearzSetup.getInstance(), this, 5L);
    }
}
