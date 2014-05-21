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

package net.cogz.chat.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.cogz.chat.GearzChat;
import net.tbnr.util.player.cooldowns.TCooldown;
import net.tbnr.util.player.cooldowns.TCooldownManager;
import org.bukkit.entity.Player;

/**
 * Manages filtering of chat by passing the
 * message sent and the sender of the messages.
 * Manages mutes, censors, and filters.
 *
 * <p>
 * Latest Change: Move to Bukkit
 * <p>
 *
 * @author Jake
 * @since 12/28/2013
 */
public class Filter {
    public static FilterData filter(String message, Player player) {
        FilterData filterData = new FilterData(message, player, false);
        if (player.hasPermission("gearz.chat.filters.bypass")) return filterData;

        if (GearzChat.getInstance().getChat().isMuted() && !player.hasPermission("gearz.mute.bypass")) {
            player.sendMessage(GearzChat.getInstance().getFormat("formats.chat-muted"));
            filterData.setCancelled(true);
            return filterData;
        }

        for (CensoredWord word : GearzChat.getInstance().getChat().getCensoredWords()) {
            filterData.setMessage(word.censorString(filterData.getMessage()));
        }

        if (!TCooldownManager.canContinueLocal(player.getName() + "chat", new TCooldown(1000))) {
            player.sendMessage(GearzChat.getInstance().getFormat("formats.chat-speed"));
            filterData.setCancelled(true);
            return filterData;
        }

        if (!TCooldownManager.canContinueLocal("allchat", new TCooldown(5))) {
            player.sendMessage(GearzChat.getInstance().getFormat("formats.chat-gspeed"));
            filterData.setCancelled(true);
            return filterData;
        }

        if (filterData.getMessage().matches("connected with an ([^\\s]+) using MineChat")) {
            player.kickPlayer(GearzChat.getInstance().getFormat("formats.minechat-banned"));
            filterData.setCancelled(true);
            return filterData;
        }

        if (filterData.getMessage().length() == 1 && !(filterData.getMessage().equalsIgnoreCase("k"))) {
            player.sendMessage(GearzChat.getInstance().getFormat("formats.chat-short"));
            filterData.setCancelled(true);
            return filterData;
        }

        if (filterData.getMessage().matches(".*(([\\w]){1,2} ){10}.*")) {
            player.sendMessage(GearzChat.getInstance().getFormat("formats.chat-many-words"));
            filterData.setCancelled(true);
            return filterData;
        }

        if (filterData.getMessage().matches(".*([\\w]{17,}).*")) {
            player.sendMessage(GearzChat.getInstance().getFormat("formats.chat-long-word"));
            filterData.setCancelled(true);
            return filterData;
        }

        if ((filterData.getMessage().matches(".*(([\\w]+\\.)+[\\w]+).*") || filterData.getMessage().matches(".*([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}).*"))) {
            player.sendMessage(GearzChat.getInstance().getFormat("formats.chat-advertising"));
            filterData.setCancelled(true);
            return filterData;
        }

        if ((filterData.getMessage() + " ").matches(".*([A-Z]{2,}[\\s]){2,}.*")) {
            filterData.setMessage(filterData.getMessage().toLowerCase());
        }

        if (filterData.getMessage().matches(".*(\\w)\\1\\1\\1\\1\\1\\1+.*")) {
            filterData.setMessage(filterData.getMessage().replaceAll("(\\w)\\1\\1\\1\\1\\1\\1+", ""));
        }

        if (GearzChat.getInstance().getChat().getLastMessages().containsKey(player.getName())) {
            String lastMessage = GearzChat.getInstance().getChat().getLastMessages().get(player.getName());
            if (lastMessage.equalsIgnoreCase(filterData.getMessage())) {
                player.sendMessage(GearzChat.getInstance().getFormat("formats.chat-repeat"));
                filterData.setCancelled(true);
                return filterData;
            }
        }

        GearzChat.getInstance().getChat().getLastMessages().put(player.getName(), filterData.getMessage());
        return filterData;
    }

    @AllArgsConstructor
    @Data
    public static class FilterData {
        private String message;
        private Player proxiedPlayer;
        private boolean cancelled;
    }
}
