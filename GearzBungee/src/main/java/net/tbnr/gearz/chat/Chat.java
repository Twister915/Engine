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

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.tbnr.gearz.GearzBungee;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Stores chat data including
 * personal conversations
 * and previous messages for players
 *
 * <p>
 * Latest Change: Remove mutes.
 * <p>
 *
 * @author Jake
 * @since 12/28/2013
 */
public class Chat {

    @Getter @Setter
    public boolean muted;

    @Getter
    public final List<CensoredWord> censoredWords;

    @Getter List<PrivateConversation.Conversation> conversations = new ArrayList<>();

    @Getter Map<ProxiedPlayer, String> lastMessages = Maps.newHashMap();

    public Chat() {
        setMuted(false);
        this.censoredWords = new ArrayList<>();
        this.lastMessages = Maps.newHashMap();
        updateCensor();
    }

    public boolean isPlayerInConversation(ProxiedPlayer proxiedPlayer) {
        return getConversationForPlayer(proxiedPlayer) != null;
    }

    public PrivateConversation.Conversation getConversationForPlayer(ProxiedPlayer player) {
        for (PrivateConversation.Conversation conversation : GearzBungee.getInstance().getChat().getConversations()) {
            if (conversation.getSender().getName().equals(player.getName())) return conversation;
        }
        return null;
    }

    public void updateCensor() {
        String[] censoredWords1 = GearzBungee.getInstance().getCensoredWords();
        censoredWords.clear();
        for (String s : censoredWords1) {
            censoredWords.add(new CensoredWord(s));
        }
    }
}
