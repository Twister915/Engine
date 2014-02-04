package net.tbnr.gearz.effects.entityblocks;

import lombok.Data;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * Created by George on 04/02/14.
 * <p/>
 * Purpose Of File: To provide a wrapper for bukkit block's with more method's
 * <p/>
 * Latest Change: created it and added base functions
 * <p/>
 * Important Information: To get a GearzBlock from a block use the {@link #block2GearzBlock(Block block)}
 * <p/>
 * Do not try to invoke the constructor!
 */
@Data public class GearzBlock {

	// The reason I'm keeping all the type and data is because I might change the bukkit block to air

	private Material type;

	private Byte data;

	@Getter
	private final Block block;

	GearzBlock(Block block)	{

		this.type = block.getType();
		this.data = block.getData();
		this.block = block;
	}

	public static GearzBlock block2GearzBlock(Block block) {
		return GearzBlockManager.registerBlock(block);
	}

}
