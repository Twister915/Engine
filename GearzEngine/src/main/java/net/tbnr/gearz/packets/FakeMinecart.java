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

package net.tbnr.gearz.packets;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * <p/>
 * Latest Change:
 * <p/>
 *
 * @author George
 * @since 26/03/14
 */
public class FakeMinecart extends FakeEntity {

	public FakeMinecart(Player player, Location location) {
		super(player, EntityType.MINECART, 6, location, EntityFlags.NONE);
	}

	public void setBlockInside() {

	}

	public void removeBlockInside() {

	}

	public void setYOffset() {

	}

	public void setPitch() {

	}

	public void setYaw() {

	}
}
