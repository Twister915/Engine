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

package net.cogzmc.engine.gearz.game.voting;

import lombok.*;
import net.cogzmc.engine.gearz.player.GearzPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Joey on 1/9/14.
 * <p/>
 * Purpose Of File:
 * <p/>
 * Latest Change:
 */
@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
public final class PlayerMapVoteEvent extends Event {
    private Integer numberOfVotes;
    @Setter(AccessLevel.PACKAGE)
    private GearzPlayer player;
    @Setter(AccessLevel.PACKAGE)
    private Votable votable;
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
