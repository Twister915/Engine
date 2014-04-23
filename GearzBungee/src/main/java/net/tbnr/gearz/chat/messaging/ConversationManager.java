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

package net.tbnr.gearz.chat.messaging;

import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Latest Change:
 * <p>
 *
 * @author Jake
 * @since 4/14/2014
 */
public class ConversationManager {
    @Getter List<PrivateConversation.Conversation> conversations = new ArrayList<>();

    public boolean isPlayerInConversation(ProxiedPlayer proxiedPlayer) {
        return getConversationForPlayer(proxiedPlayer) != null;
    }

    public PrivateConversation.Conversation getConversationForPlayer(ProxiedPlayer player) {
        for (PrivateConversation.Conversation conversation : this.conversations) {
            if (conversation.getSender().getName().equals(player.getName())) return conversation;
        }
        return null;
    }
}
