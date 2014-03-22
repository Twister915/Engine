package net.tbnr.gearz.effects.entityblocks;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by George on 04/02/14.
 * <p/>
 * Purpose Of File:
 * <p/>
 * Latest Change:
 */
public class GearzBlockManager {

	private static List<GearzBlock> regBlocks = new ArrayList<>();

	public static boolean isRegistered(GearzBlock b) {
		return regBlocks.contains(b);
	}

	public static GearzBlock registerBlock(GearzBlock gearzBlock) {
		if(isRegistered(gearzBlock)) {
			return regBlocks.get(regBlocks.indexOf(gearzBlock));
		}
		regBlocks.add(gearzBlock);
		return gearzBlock;
	}
}
