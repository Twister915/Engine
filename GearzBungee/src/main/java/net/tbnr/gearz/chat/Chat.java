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
 * Created by jake on 12/28/13.
 * <p/>
 * This class stores data for chat
 */
public class Chat {
    public Chat() {
        setMuted(false);
        this.censoredWords = new ArrayList<>();
        this.lastMessages = Maps.newHashMap();
        updateCensor();
    }

    @Getter @Setter
    public boolean muted;

    @Getter
    public List<CensoredWord> censoredWords;

    @Getter List<PrivateConversation.Conversation> conversations = new ArrayList<>();

    @Getter Map<ProxiedPlayer, String> lastMessages = Maps.newHashMap();

    public boolean isPlayerInConversation(ProxiedPlayer proxiedPlayer) {
        return getConvoForPlayer(proxiedPlayer) != null;
    }

    public PrivateConversation.Conversation getConvoForPlayer(ProxiedPlayer player) {
        for (PrivateConversation.Conversation conversation : GearzBungee.getInstance().getChat().getConversations()) {
            if (conversation.getSender().getName().equals(player.getName())) return conversation;
        }
        return null;
    }

    public void updateCensor() {
        Object[] censoredWords1 = GearzBungee.getInstance().getCensoredWords();
        censoredWords.clear();
        for (Object o : censoredWords1) {
            if (o instanceof String) censoredWords.add(new CensoredWord((String) o));
        }
    }


}
