/*
 * Copyright (c) 2014.
 * CogzMC LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.tbnr.gearz;

import org.bukkit.block.Block;

/**
 * Created by Joey on 12/19/13.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public interface SkullDelegate {
    public void onComplete(SkullTask task);

    public void locatedBlock(Block block);
}
