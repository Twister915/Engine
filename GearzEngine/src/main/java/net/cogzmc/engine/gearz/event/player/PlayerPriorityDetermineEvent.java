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

package net.cogzmc.engine.gearz.event.player;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.cogzmc.engine.gearz.event.GearzEvent;
import net.cogzmc.engine.gearz.player.GearzPlayer;
import org.bukkit.event.Cancellable;

@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
@Data
public final class PlayerPriorityDetermineEvent extends GearzEvent implements Cancellable {
    private boolean cancelled;
    private boolean absolutePriority = false;
    private String joinMessage = null;
    @NonNull
    private GearzPlayer player;
}
