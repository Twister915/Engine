package net.tbnr.gearz.effects.entityblocks;

import net.tbnr.gearz.effects.entityblocks.exceptions.GearzBlockException;
import org.bukkit.block.Block;

/**
 * Created by George on 04/02/14.
 * <p/>
 * Purpose Of File: To provide a wrapper for bukkit block's with more method's
 * <p/>
 * Latest Change: created it and added base functions
 */
public class GearzBlock {

	public GearzBlock(Block block) throws GearzBlockException	{
		if(GearzBlockManager.isRegistered(block)) {
			throw new GearzBlockException("That block is already Registered: "+block.getLocation().toString());
		}
	}

	public static GearzBlock block2GearzBlock(Block block) throws GearzBlockException {
		return new GearzBlock(block);
	}

}
