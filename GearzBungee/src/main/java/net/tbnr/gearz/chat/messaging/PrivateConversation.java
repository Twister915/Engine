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

package net.tbnr.gearz.chat.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.tbnr.gearz.GearzBungee;

/**
 * Manages chat for personal conversations
 * which stops a player from sending a global
 * message when they are in a personal conversation.
 *
 * <p>
 * Latest Change: Create
 * <p>
 *
 * @author Jake
 * @since 12/29/2013
 */
public class PrivateConversation implements Listener {
    final Conversation conversation;

    public PrivateConversation(ProxiedPlayer sender, ProxiedPlayer target) {
        this.conversation = new Conversation(sender, target);
        GearzBungee.getInstance().getConversationManager().getConversations().add(conversation);
        sender.sendMessage(GearzBungee.getInstance().getFormat("conversation-start", false, true, new String[]{"<player>", target.getName()}));
        GearzBungee.getInstance().registerEvents(this);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    @SuppressWarnings("unused")
    public void onChat(ChatEvent event) {
        if (event.isCancelled()) return;
        if (event.isCommand()) return;
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();

        if (!GearzBungee.getInstance().getConversationManager().isPlayerInConversation(player)) return;

        GearzBungee.getInstance().getConversationManager().getConversationForPlayer(player).sendMessage(event.getMessage());
    }

    @AllArgsConstructor
    @Data
    public class Conversation {
        private final ProxiedPlayer sender;
        private final ProxiedPlayer target;

        public void sendMessage(String message) {
            String sendToSender = GearzBungee.getInstance().getFormat("messaging-message", false, false, new String[]{"<sender>", target.getName()}, new String[]{"<message>", message}, new String[]{"<direction>", "to"});
            String sendToTarget = GearzBungee.getInstance().getFormat("messaging-message", false, false, new String[]{"<sender>", sender.getName()}, new String[]{"<message>", message}, new String[]{"<direction>", "from"});

            sender.sendMessage(sendToSender);
            target.sendMessage(sendToTarget);
        }

        public void end() {
            conversation.getSender().sendMessage(GearzBungee.getInstance().getFormat("conversation-end", false, true, new String[]{"<player>", conversation.getTarget().getName()}));
            GearzBungee.getInstance().getConversationManager().getConversations().remove(this);
        }
    }
}
