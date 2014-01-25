package net.tbnr.gearz.chat.channels;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

/**
 * Created by Jake on 1/16/14.
 */
public interface ChannelInterface {
    public String getName();

    public String getFormat();

    public void setFormat(String format);

    public String getListeningPermission();

    public boolean hasPermission();

    public boolean isDefault();

    public void setDefault(boolean main);

    public boolean isCrossServer();

    public void setCrossServer(boolean crossServer);

    public boolean isIRCLinked();

    public void setIRCLinked(boolean irc);

    public List<String> getIRCChannels();

    public void setIRCChannels(List<String> channels);

    public boolean isFiltered();

    public void setFiltered(boolean filtered);

    public void setLogged(boolean logged);

    public boolean isLogged();

    public void sendMessage(String message, ProxiedPlayer sender);

    public List<ProxiedPlayer> getMembers();
}
