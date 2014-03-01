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

package net.tbnr.gearz.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.util.bungee.cooldowns.TCooldown;
import net.tbnr.util.bungee.cooldowns.TCooldownManager;

import java.util.ArrayList;

/**
 * Created by jake on 12/28/13.
 * <p/>
 * Chat filtering
 */
public class Filter {
    public static FilterData filter(String message, ProxiedPlayer player) {
        FilterData filterData = new FilterData(message, player, false);
        if (player.hasPermission("gearz.chat.filters.bypass")) return filterData;

        if (GearzBungee.getInstance().getChat().isMuted()) {
            player.sendMessage(GearzBungee.getInstance().getFormat("chat-muted"));
            filterData.setCancelled(true);
            return filterData;
        }

        ArrayList<String> usernames = new ArrayList<>();

        for (ProxiedPlayer player1 : ProxyServer.getInstance().getPlayers()) {
            usernames.add(player1.getName());
        }

        ArrayList<String> cachedNames = new ArrayList<>();
        StringBuilder messageBuilder = new StringBuilder();
        String prefix = "";
        for (String word : filterData.getMessage().split(" ")) {
            messageBuilder.append(prefix);
            prefix = " ";
            if (usernames.contains(word)) {
                cachedNames.add(word);
                word = "<player>";
            }
            messageBuilder.append(word);
        }

        filterData.setMessage(messageBuilder.toString());
        for (CensoredWord word : GearzBungee.getInstance().getChat().getCensoredWords()) {
            filterData.setMessage(word.censorString(filterData.getMessage()));
        }

        if (!cachedNames.isEmpty()) {
            StringBuilder newMessage = new StringBuilder();
            prefix = "";
            int index = 0;
            for (String word : filterData.getMessage().split(" ")) {
                newMessage.append(prefix);
                prefix = " ";
                if (word.equalsIgnoreCase("<player>")) {
                    word = cachedNames.get(index);
                    index++;
                }
                newMessage.append(word);
            }
            filterData.setMessage(newMessage.toString());
        }


        if (!TCooldownManager.canContinueLocalReset(player.getName() + "chat", new TCooldown(1000))) {
            player.sendMessage(GearzBungee.getInstance().getFormat("chat-speed"));
            filterData.setCancelled(true);
            return filterData;
        }

        if (!TCooldownManager.canContinueLocal("allchat", new TCooldown(5))) {
            player.sendMessage(GearzBungee.getInstance().getFormat("chat-gspeed"));
            filterData.setCancelled(true);
            return filterData;
        }

        if (filterData.getMessage().length() == 1 && !(filterData.getMessage().equalsIgnoreCase("k"))) {
            player.sendMessage(GearzBungee.getInstance().getFormat("chat-short"));
            filterData.setCancelled(true);
            return filterData;
        }

        if (filterData.getMessage().matches(".*(([\\w]){1,2} ){10}.*")) {
            player.sendMessage(GearzBungee.getInstance().getFormat("chat-many-words"));
            filterData.setCancelled(true);
            return filterData;
        }

        if (filterData.getMessage().matches(".*([\\w]{17,}).*")) {
            player.sendMessage(GearzBungee.getInstance().getFormat("chat-long-word"));
            filterData.setCancelled(true);
            return filterData;
        }

        if ((filterData.getMessage().matches(".*(([\\w]+\\.)+[\\w]+).*") || filterData.getMessage().matches(".*([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}).*"))) {
            player.sendMessage(GearzBungee.getInstance().getFormat("chat-advertising"));
            filterData.setCancelled(true);
            return filterData;
        }

        if ((filterData.getMessage() + " ").matches(".*([A-Z]{2,}[\\s]){2,}.*")) {
            filterData.setMessage(filterData.getMessage().toLowerCase());
        }

        if (filterData.getMessage().matches(".*(\\w)\\1\\1\\1\\1\\1\\1+.*")) {
            filterData.setMessage(filterData.getMessage().replaceAll("(\\w)\\1\\1\\1\\1\\1\\1+", ""));
        }

        if (GearzBungee.getInstance().getChat().getLastMessages().containsKey(player)) {
            String lastMessage = GearzBungee.getInstance().getChat().getLastMessages().get(player);
            if (lastMessage.equalsIgnoreCase(filterData.getMessage())) {
                player.sendMessage(GearzBungee.getInstance().getFormat("chat-repeat"));
                filterData.setCancelled(true);
                return filterData;
            }
        }

        GearzBungee.getInstance().getChat().getLastMessages().put(player, filterData.getMessage());

        return filterData;
    }

    @AllArgsConstructor
    @Data
    @EqualsAndHashCode
    public static class FilterData {
        private String message;
        private ProxiedPlayer proxiedPlayer;
        private boolean cancelled;
    }
}
