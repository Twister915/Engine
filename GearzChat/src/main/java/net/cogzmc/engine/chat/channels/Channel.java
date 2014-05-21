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

package net.cogzmc.engine.chat.channels;

import lombok.Data;
import lombok.ToString;
import net.cogzmc.engine.chat.channels.base.BaseChannel;
import net.cogzmc.engine.gearz.netcommand.NetCommand;
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
@Data
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
    public boolean hasPermission() {
        return permission != null && !permission.equals("");
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
    public void sendMessage(String message, Player sender) {
        for (Player receiver : Bukkit.getOnlinePlayers()) {
            if (!receiver.isValid()) continue;
            if (this.hasPermission()) {
                if (receiver.hasPermission(getPermission())) {
                    receiver.sendMessage(message);
                }
            } else {
                receiver.sendMessage(message);
            }
        }
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
