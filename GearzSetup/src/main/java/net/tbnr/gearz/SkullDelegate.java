package net.tbnr.gearz;

import org.bukkit.block.Block;

/**
 * Created by Joey on 12/19/13.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public interface SkullDelegate {
    public void onComplete(SkullTask task);

    public void locatedBlock(Block block);
}
