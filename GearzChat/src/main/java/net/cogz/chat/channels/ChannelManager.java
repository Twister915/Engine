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

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.cogz.chat.GearzChat;
import net.cogz.chat.filter.Filter;
import net.tbnr.gearz.Gearz;
import net.tbnr.util.PermissionsDelegate;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages creation and registration of
 * channels. Also provides implementation
 * for sending messages on channels.
 *
 * <p>
 * Latest Change: Player display names
 * <p>
 *
 * @author Jake
 * @since 1/16/2014
 */
public class ChannelManager {
    /**
     * Whether or not channels are enabled
     */
    @Getter
    private boolean enabled = false;

    /**
     * A list of currently registered channels.
     */
    @Getter
    private final List<Channel> channels = new ArrayList<>();

    /**
     * A map of players to their respective channels
     */
    private final Map<String, Channel> playerChannels = new HashMap<>();

    public ChannelManager() {
        enabled = GearzChat.getInstance().getChannelConfig().getConfig().getBoolean("channels.enabled", false);
    }

    /**
     * Registers all channels based on data stored in a config file.
     */
    public void registerChannels() {
        FileConfiguration config = GearzChat.getInstance().getChannelConfig().getConfig();
        for (String chanName : config.getStringList("channels.list")) {
            String format = config.getString("formatting." + chanName + ".format");
            String permission = config.getString("formatting." + chanName + ".permission");
            boolean main = config.getBoolean("formatting." + chanName + ".default", false);
            boolean crossServer = config.getBoolean("formatting." + chanName + ".cross-server", false);
            boolean filter = config.getBoolean("formatting." + chanName + ".filter", true);
            Channel channel = new Channel(chanName, format, permission);
            channel.setDefault(main);
            channel.setCrossServer(crossServer);
            channel.setFiltered(filter);
            registerChannel(channel);
        }
    }

    /**
     * Unregisters all registered channels
     */
    public void unregisterChannels() {
        for (Channel channel : channels) {
            unregisterChannel(channel);
        }
    }

    /**
     * Gets the default channel
     *
     * @return the default channel
     */
    public Channel getDefaultChannel() {
        for (Channel channel : channels) {
            if (channel.isDefault()) return channel;
        }
        return null;
    }

    /**
     * Registers a channel
     *
     * @param channel channel to register
     */
    private void registerChannel(Channel channel) {
        GearzChat.getInstance().getLogger().info("Registered channel " + channel.getName() + ": " + channel.toString());
        channels.add(channel);
    }

    /**
     * Unregisters a channel. Sets all the
     * channel's current members to the default
     * channel.
     *
     * @param channel channel to unregister
     */
    private void unregisterChannel(Channel channel) {
        if (channels.contains(channel)) {
            for (Player player : channel.getMembers()) {
                setChannel(player, getDefaultChannel());
            }
            channels.remove(channel);
        }
    }

    /**
     * Returns whether or not a channel is registered
     *
     * @param name name of the channel to check
     * @return whether or not the channel is registered
     */
    private boolean isChannelRegistered(String name) {
        return getChannelByName(name) != null;
    }

    /**
     * Returns a channel based on the search by name
     *
     * @param name name of the channel to search for
     * @return channel found based on the name paramater
     */
    public Channel getChannelByName(String name) {
        Preconditions.checkNotNull(name, "Name can not be null");
        for (Channel channel : channels) {
            if (channel.getName().toLowerCase().equals(name)) return channel;
        }
        return null;
    }

    /**
     * Returns the current channel for the player
     *
     * @param player the player to lookup
     * @return the channel that the player is currently on
     */
    public Channel getCurrentChannel(Player player) {
        return getChannel(player);
    }

    /**
     * Handles the filtering of a message if necessary.
     * The finalized message is then passed on to the {@link Channel}
     * instance to be sent to the channel's listeners.
     *
     * @param sender  sender of the message as a {@link Player}
     * @param message message sent
     * @return the channel that the message was sent to
     */
    public Channel sendMessage(Player sender, String message) {
        Channel channel = getCurrentChannel(sender);
        if (channel == null) {
            setChannel(sender, getDefaultChannel());
            channel = getCurrentChannel(sender);
        }
        if (channel.isFiltered()) {
            Filter.FilterData filterData = Filter.filter(message, sender);
            if (filterData.isCancelled()) {
                return channel;
            }
            message = filterData.getMessage();
        }
        channel.sendMessage(formatMessage(message, sender), sender);
        return channel;
    }

    private String formatMessage(String message, Player player) {
        String chanFormat = getCurrentChannel(player).getFormat();
        PermissionsDelegate perms = Gearz.getInstance().getPermissionsDelegate();
        MessageFormat formatter = new MessageFormat(chanFormat);
        String senderName = player.getName();
        String senderDisplay = player.getDisplayName();
        String cleanMessage = ChatColor.stripColor(message);
        String prefix = "";
        String suffix = "";
        String nameColor = "";
        String resetColor = ChatColor.RESET + "";
        if (perms != null) {
            prefix = perms.getPrefix(player.getName());
            suffix = perms.getSuffix(player.getName());
            nameColor = perms.getNameColor(player.getName());
            if (prefix == null) prefix = "";
            else prefix = ChatColor.translateAlternateColorCodes('&', prefix);
            if (suffix == null) suffix = "";
            else suffix = ChatColor.translateAlternateColorCodes('&', suffix);
            if (nameColor == null) nameColor = "";
            else nameColor = ChatColor.translateAlternateColorCodes('&', nameColor);
        }

        Object[] args = {senderName, senderDisplay, message, cleanMessage, prefix, suffix, nameColor, resetColor};
        return ChatColor.translateAlternateColorCodes('&', formatter.format(args));
    }

    /**
     * Sets a players channel
     *
     * @param player  player to set the channel of
     * @param channel channel to set the player to
     */
    public void setChannel(Player player, Channel channel) {
        Channel playerChannel = this.playerChannels.get(player.getName());
        if (playerChannel != null && playerChannel.getName().equals(channel.getName())) {
            throw new IllegalStateException("Already on this channel!");
        }
        if (playerChannel != null) {
            playerChannel.removeMember(player);
        }

        this.playerChannels.put(player.getName(), channel);
        channel.addMember(player);
    }

    /**
     * Removes the channel from a player
     *
     * @param player player to remove the channel from
     */
    public void removeChannel(Player player) {
        this.playerChannels.get(player.getName()).removeMember(player);
        this.playerChannels.remove(player.getName());
    }

    /**
     * Gets a {@link Player}'s channel
     *
     * @param player player to get the channel of
     * @return the channel of the player paramater
     */
    public Channel getChannel(Player player) {
        return this.playerChannels.get(player.getName());
    }
}
