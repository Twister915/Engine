package net.tbnr.util;

import net.tbnr.gearz.Gearz;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class BlockRepair {

    public static void performRegen(final List<BlockState> blocks, final Location center, final int blocksPerTime, long delay) {
        new BukkitRunnable() {
            @Override
            public void run() {
                BlockRepair.regenerateBlocks(blocks, blocksPerTime, 12, new Comparator<BlockState>() {
                    @Override
                    public int compare(BlockState state1, BlockState state2) {
                        return Double.compare(state1.getLocation().distance(center), state2.getLocation().distance(center));
                    }
                });
            }
        }.runTaskLater(Gearz.getInstance(), delay);
    }

    public static void regenerateBlocks(Collection<BlockState> blocks, final int blocksPerTime, final long delay, Comparator<BlockState> comparator) {
        final List<BlockState> orderedBlocks = new ArrayList<>();
        orderedBlocks.addAll(blocks);
        if (comparator != null) {
            Collections.sort(orderedBlocks, comparator);
        }
        final int size = orderedBlocks.size();
        if (size > 0) {
            new BukkitRunnable() {
                int index = size - 1;

                @Override
                public void run() {
                    for (int i = 0; i < blocksPerTime; i++) {
                        if (index >= 0) {
                            final BlockState state = orderedBlocks.get(index);

                            regenerateBlock(state.getBlock(), state.getType(), state.getData().getData());

                            index -= 1;
                        } else {
                            this.cancel();
                            return;
                        }
                    }
                }
            }.runTaskTimer(Gearz.getInstance(), 0L, delay);
        }
    }

    public static void regenerateBlock(Block block, final Material type, final byte data) {
        final Location loc = block.getLocation();

        loc.getWorld().playEffect(loc, Effect.STEP_SOUND, (type == Material.AIR ? block.getType().getId() : type.getId()));
        block.setTypeIdAndData(type.getId(), data, false);
    }
}
