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

import lombok.ToString;
import net.cogz.chat.channels.base.BaseChannel;
import net.tbnr.gearz.netcommand.NetCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link Channel}. Handles
 * sending formatted messages to players on
 * the sender's server, or across multiple servers
 * using a {@link NetCommand}. Also handles
 * permission checking and filtering of messages
 * that go through the channel.
 *
 * <p>
 * Latest Change: Rewrite for Bukkit
 * <p>
 *
 * @author Jake
 * @since 1/16/2014
 */
@ToString(exclude = {"members", "format"})
public class Channel implements BaseChannel {
    private String name;
    private String format;
    private String permission;
    private boolean main;
    private boolean crossServer;
    private boolean filtered;
    private List<Player> members;

    public Channel(String name, String format, String permission) {
        this.name = name;
        this.format = format;
        this.permission = permission;
        this.members = new ArrayList<>();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getFormat() {
        return this.format;
    }

    @Override
    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public String getListeningPermission() {
        return this.permission;
    }

    @Override
    public boolean hasPermission() {
        return permission == null || !permission.equals("");
    }

    @Override
    public boolean isDefault() {
        return this.main;
    }

    @Override
    public void setDefault(boolean main) {
        this.main = main;
    }

    @Override
    public boolean isCrossServer() {
        return this.crossServer;
    }

    @Override
    public void setCrossServer(boolean crossServer) {
        this.crossServer = crossServer;
    }

    @Override
    public boolean isFiltered() {
        return this.filtered;
    }

    @Override
    public void setFiltered(boolean filtered) {
        this.filtered = filtered;
    }

    @Override
    public void sendMessage(String message, Player sender) {
        for (Player receiver : Bukkit.getOnlinePlayers()) {
            if (!receiver.isValid()) continue;
            if (this.hasPermission()) {
                if (receiver.hasPermission(getListeningPermission())) {
                    receiver.sendMessage(message);
                }
            } else {
                receiver.sendMessage(message);
            }
        }
        if (this.isCrossServer()) {
            NetCommand.withName("chat").withArg("channel", this.name).withArg("message", message);
        }
    }

    @Override
    public List<Player> getMembers() {
        return members;
    }

    /**
     * Adds a member to the channel
     *
     * @param player player to add to he channel
     */
    public void addMember(Player player) {
        this.members.add(player);
    }

    /**
     * Removes a member from the channel
     *
     * @param player player to remove from the channel
     */
    public void removeMember(Player player) {
        this.members.remove(player);
    }
}
