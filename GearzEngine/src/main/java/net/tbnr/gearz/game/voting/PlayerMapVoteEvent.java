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

package net.tbnr.gearz.game.voting;

import lombok.*;
import net.tbnr.gearz.player.GearzPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Joey on 1/9/14.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
public final class PlayerMapVoteEvent extends Event {
    private Integer numberOfVotes;
    @Setter(AccessLevel.PACKAGE)private GearzPlayer player;
    @Setter(AccessLevel.PACKAGE)private Votable votable;
    /*
   Event code
    */
    private static final HandlerList handlers = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
