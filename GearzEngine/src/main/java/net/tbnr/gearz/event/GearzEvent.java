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

package net.tbnr.gearz.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * <p>
 * Latest Change:
 * <p>
 *
 * @author Jake
 * @since 5/4/2014
 */
public class GearzEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
