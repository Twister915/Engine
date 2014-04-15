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

package net.cogz.chat.channels.base;

import org.bukkit.entity.Player;

import java.util.List;

/**
 * Base implementation of a channel.
 *
 * <p>
 * Latest Change: Rewrite for Bukkit
 * <p>
 *
 * @author Jake
 * @since 1/16/2014
 */
public interface BaseChannel {
    /**
     * Name of the channel
     *
     * @return channel name
     */
    public String getName();

    /**
     * Format of the channel. Uses
     * {@link java.text.MessageFormat} to
     * format the message sent.
     *
     * @return the channel format
     * @see net.cogz.chat.channels.Channel#getFormat() for information of formatting
     */
    public String getFormat();

    /**
     * Sets the channel format
     *
     * @param format format to set the channel to
     * @see net.cogz.chat.channels.Channel#getFormat() for information of formatting
     */
    public void setFormat(String format);

    /**
     * Gets the permission required to see messages in this channel
     *
     * @return channel permission
     */
    public String getListeningPermission();

    /**
     * Whether or not the channel has a permission. Checks if the permisison is null or is empty.
     *
     * @return whether or not a permission exists
     */
    public boolean hasPermission();

    /**
     * Returns whether or not the channel is the default one
     *
     * @return whether or not the channel is the default one
     */
    public boolean isDefault();

    /**
     * Sets whether or not the channel is the default one
     *
     * @param main whether or not the channel is the default one
     */
    public void setDefault(boolean main);

    /**
     * Returns whether or not the channel sends messages across servers
     *
     * @return whether or not the channel sends cross server messages
     */
    public boolean isCrossServer();

    /**
     * Sets whether or not the channel is cross server
     *
     * @param crossServer whether or not the channel is cross server
     */
    public void setCrossServer(boolean crossServer);

    /**
     * Returns whether or not the channel is filtered by {@link net.cogz.chat.filter.Filter}
     *
     * @return whether or not the channel is filtered
     */
    public boolean isFiltered();

    /**
     * Sets whether or not the channel is filtered by {@link net.cogz.chat.filter.Filter}
     *
     * @param filtered whether or not the channel is filtered by {@link net.cogz.chat.filter.Filter}
     */
    public void setFiltered(boolean filtered);

    /**
     * Sends a message on this channel to all of the player's with the necessary listening permission
     *
     * @param message message to send
     * @param sender  {@link Player} instance of the sender
     */
    public void sendMessage(String message, Player sender);

    /**
     * {@link List} of Bukkit {@link Player} that are members of this channel.
     *
     * @return a list of {@link Player} who are members in this channel
     */
    public List<Player> getMembers();
}
