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

package net.tbnr.gearz.event.game;

import lombok.AccessLevel;
import lombok.Getter;
import net.tbnr.gearz.game.GearzGame;
import org.bukkit.event.Event;

/**
 * Created by George on 16/02/14.
 * <p/>
 * Purpose Of File: A Superclass for GearzGame events
 * <p/>
 * Latest Change: Created it
 */
public abstract class GearzGameEvent extends Event {
	@Getter
	private final GearzGame game;

	public GearzGameEvent(final GearzGame game) {
		this.game = game;
	}
}
