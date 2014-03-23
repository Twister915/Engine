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

	public static EntityBlock[] createSphere(int radius, Location center, Material type, byte b, double xSparsity, double ySparsity) {
		ArrayList<EntityBlock> blocks = new ArrayList<>();
		//pitch
		//for(int i = 0, l = 360; i < l; i+=xSparsity) {
			//yaw
			for(double i2 = 0, l2 = 180; i2 < l2; i2+=ySparsity) {
				//if(l2 <= 90) {
					//EntityBlock.newBlock(center of sphere, type of block, data of block, yaw of minecart, pitch of minecart, y-offset (radius));
					blocks.add(EntityBlock.newBlock(center, type, b, (float) i2, 0, radius));
			//	}
			}
	//	}
		return blocks.toArray(new EntityBlock[blocks.size()]);
	}

	/*public static EntityBlock[] createExplosion() {

	}*/
}
