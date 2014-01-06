package net.tbnr.gearz;

import org.bukkit.block.Block;

/**
 * Created by Joey on 12/19/13.
 */
public interface SkullDelegate {
    public void onComplete(SkullTask task);

    public void locatedBlock(Block block);
}
