package net.tbnr.gearz.effects.entityblocks;

import lombok.Data;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * Created by George on 04/02/14.
 * <p/>
 * Purpose Of File: To provide a wrapper for bukkit block's with more method's
 * <p/>
 * Latest Change: created it and added base functions
 * <p/>
 * Important Information: As soon as you make the gearz block use the {@link #register()} method
 * For Example: GearzBlock b = new GearzBlock(location, type, data).register();
 * Save the register command as your reference to the block
 * <p/>
 *
 * Do not try to invoke the constructor!
 */
@Data public class GearzBlock {

	protected Material type;

	protected Byte data;

	protected Location location;

	public GearzBlock(Location l) {
		this(l.getBlock());
	}

	public GearzBlock(Block block)	{
		this(block.getLocation(), block.getType(), block.getData());
	}

	public GearzBlock(Location location, Material type, Byte data) {
		this.location = location;
		this.type = type;
		this.data = data;
	}

	public GearzBlock register() {
		return GearzBlockManager.registerBlock(this);
	}

}
