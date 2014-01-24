package net.tbnr.gearz.chat;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.gearz.punishments.LoginHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by jake on 12/28/13.
 * This class stores data for chat
 */
public class Chat {

    @Getter @Setter
    public boolean muted;

    @Getter
    public final List<CensoredWord> censoredWords;

    @Getter List<PrivateConversation.Conversation> conversations = new ArrayList<>();

    @Getter Map<ProxiedPlayer, String> lastMessages = Maps.newHashMap();

    @Getter
    final Map<String, LoginHandler.MuteData> mutes = Maps.newHashMap();

    public Chat() {
        setMuted(false);
        this.censoredWords = new ArrayList<>();
        this.lastMessages = Maps.newHashMap();
        updateCensor();
    }

    public boolean isPlayerMuted(String username) {
        if (!mutes.containsKey(username)) return false;
        LoginHandler.MuteData muteData = mutes.get(username);
        if (muteData.isPerm()) return true;
        Date end = muteData.getEnd();
        if (new Date().before(end)) {
            return true;
        } else {
            mutes.remove(username);
            return false;
        }
    }

    public void addMute(String username, LoginHandler.MuteData muteData) {
        mutes.put(username, muteData);
    }

    public LoginHandler.MuteData getMute(String username) {
        return mutes.get(username);
    }

    public void removeMute(String username) {
        mutes.remove(username);
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
