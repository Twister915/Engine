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

package net.cogz.chat.channels;

import lombok.Getter;
import lombok.Setter;
import net.tbnr.gearz.event.GearzEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * <p/>
 * Latest Change:
 * <p/>
 *
 * @author Jake
 * @since 5/19/2014
 */
public class ChannelSwitchEvent extends GearzEvent implements Cancellable {
    @Getter
    private Player player;
    @Getter private Channel oldChannel;
    @Getter @Setter
    private Channel newChannel;
    @Getter @Setter private boolean cancelled = false;
    @Getter private boolean isNecessary = false;

    public ChannelSwitchEvent(Player player, @Nullable Channel oldChannel, @Nonnull Channel newChannel, boolean isNecessary) {
        this.player = player;
        this.oldChannel = oldChannel;
        this.newChannel = newChannel;
        this.isNecessary = isNecessary;
    }
}
