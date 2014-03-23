/*
 * Copyright (c) 2014.
 * Cogz Development LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.tbnr.gearz.effects.entityblocks;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;

/**
 * Created by George on 23/03/14.
 * <p/>
 * Purpose Of File:
 * <p/>
 * Latest Change:
 */
public class EntityBlockUtil {

	public static EntityBlock[] createSphere(int radius, Location center, Material type, byte b) {
		ArrayList<EntityBlock> blocks = new ArrayList<>();
		for(int i = 0, l = 360; i < l; i++) {
			for(int i2 = 0, l2 = 360; i2 < l2; i2++) {
				blocks.add(EntityBlock.newBlock(center, type, b, (float) i2, (float) i, radius));
			}
		}
		return blocks.toArray(new EntityBlock[blocks.size()]);
	}
}
