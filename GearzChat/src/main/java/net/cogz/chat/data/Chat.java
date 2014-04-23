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

package net.cogz.chat.data;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import net.cogz.chat.GearzChat;
import net.cogz.chat.filter.CensoredWord;

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

    /**
     * Whether or not chat is globally muted
     */
    @Getter @Setter
    public boolean muted = false;

    /**
     * A list of {@link net.cogz.chat.filter.CensoredWord}
     */
    @Getter
    public final List<CensoredWord> censoredWords;

    /**
     * The last messages that a player sent
     */
    @Getter Map<String, String> lastMessages = Maps.newHashMap();

    public Chat() {
        this.censoredWords = new ArrayList<>();
        this.lastMessages = Maps.newHashMap();
        updateCensor();
    }

    /**
     * Updates the list of censored words from the database
     */
    public void updateCensor() {
        String[] censoredWords1 = GearzChat.getInstance().getCensoredWords();
        censoredWords.clear();
        for (String s : censoredWords1) {
            censoredWords.add(new CensoredWord(s));
        }
    }
}
