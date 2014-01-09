package net.tbnr.gearz.chat;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
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
    public List<CensoredWord> censoredWords;

    @Getter List<PrivateConversation.Conversation> conversations = new ArrayList<>();

    @Getter Map<ProxiedPlayer, String> lastMessages = Maps.newHashMap();

    @Getter Map<ProxiedPlayer, LoginHandler.MuteData> mutes = Maps.newHashMap();

    public Chat() {
        setMuted(false);
        this.censoredWords = new ArrayList<>();
        this.lastMessages = Maps.newHashMap();
        updateCensor();
    }

    public boolean isPlayerMuted(ProxiedPlayer player) {
        ProxyServer.getInstance().getLogger().info("NULL CHECK");
        if (!mutes.containsKey(player)) return false;
        LoginHandler.MuteData muteData = mutes.get(player);
        ProxyServer.getInstance().getLogger().info("PERM CHECK");
        if (muteData.isPerm()) return true;
        Date end = muteData.getEnd();
        if (new Date().before(end)) {
            ProxyServer.getInstance().getLogger().info("DATE CHECK");
            return true;
        } else {
            ProxyServer.getInstance().getLogger().info("DATE CHECK DOS");
            mutes.remove(player);
            return false;
        }
    }

    public void addMute(ProxiedPlayer proxiedPlayer, LoginHandler.MuteData muteData) {
        mutes.put(proxiedPlayer, muteData);
    }

    public LoginHandler.MuteData getMute(ProxiedPlayer player) {
        return mutes.get(player);
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
