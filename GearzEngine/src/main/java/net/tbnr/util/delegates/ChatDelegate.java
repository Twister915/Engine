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

package net.tbnr.util.delegates;

import org.bukkit.entity.Player;

/**
 * <p>
 * Latest Change:
 * <p>
 *
 * @author Jake
 * @since 4/18/2014
 */
public interface ChatDelegate {
    void setChannel(Player player, String channel);
}
