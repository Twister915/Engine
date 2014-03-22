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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by George on 20/03/14.
 * <p/>
 * Purpose Of File:
 * <p/>
 * Latest Change:
 */
public class EntityBlockManager {

	private static List<EntityBlock> regBlocks = new ArrayList<>();

	public static boolean isRegistered(EntityBlock b) {
		return regBlocks.contains(b);
	}

	public static EntityBlock registerBlock(EntityBlock entityBlock) {
		if(isRegistered(entityBlock)) {
			return regBlocks.get(regBlocks.indexOf(entityBlock));
		}
		regBlocks.add(entityBlock);
		return entityBlock;
	}
}
