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

package net.tbnr.gearz.event.player;

import lombok.Getter;
import lombok.NonNull;
import net.tbnr.gearz.player.GearzPlayer;

/**
 * Created by Joey on 1/12/14.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public final class PlayerGameDamageByPlayerEvent extends PlayerGameDamageEvent {
    @NonNull @Getter private final GearzPlayer damager;

    public PlayerGameDamageByPlayerEvent(PlayerGameDamageEvent event, GearzPlayer damager) {
        super(event.getGame(), event.getPlayer(), event.getDamage(), event.isCancelled());
        this.damager = damager;
    }
}
