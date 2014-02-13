package net.tbnr.gearz.chat.channels;

import lombok.ToString;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jake on 1/16/14.
 */
@ToString
public class Channel implements ChannelInterface {
    String name;
    String format;
    String permission;
    List<String> ircChannels;
    boolean main;
    boolean crossServer;
    boolean ircLinked;
    boolean filtered;
    boolean logged;
    List<ProxiedPlayer> members;

    public Channel(String name, String format, String permission) {
        this.name = name;
        this.format = format;
        this.permission = permission;
        this.members = new ArrayList<>();
        this.ircChannels = new ArrayList<>();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getFormat() {
        return this.format;
    }

    @Override
    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public String getListeningPermission() {
        return this.permission;
    }

    @Override
    public boolean hasPermission() {
        return permission != null && !permission.equals("");
    }

    @Override
    public boolean isDefault() {
        return this.main;
    }

    @Override
    public void setDefault(boolean main) {
        this.main = main;
    }

    @Override
    public boolean isCrossServer() {
        return this.crossServer;
    }

    @Override
    public void setCrossServer(boolean crossServer) {
        this.crossServer = crossServer;
    }

    @Override
    public boolean isIRCLinked() {
        return this.ircLinked;
    }

    @Override
    public void setIRCLinked(boolean irc) {
        this.ircLinked = irc;
    }

    @Override
    public List<String> getIRCChannels() {
        return this.ircChannels;
    }

    @Override
    public void setIRCChannels(List<String> channels) {
        this.ircChannels = channels;
    }

    @Override
    public boolean isFiltered() {
        return this.filtered;
    }

    @Override
    public void setFiltered(boolean filtered) {
        this.filtered = filtered;
    }

    @Override
    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    @Override
    public boolean isLogged() {
        return this.logged;
    }

    @Override
    public void sendMessage(String message, ProxiedPlayer sender) {
        for (ProxiedPlayer receiver : ProxyServer.getInstance().getPlayers()) {
            sender.sendMessage("RUNNNING");
            if (this.isCrossServer()) {
                sender.sendMessage("CROSS");

                if (this.hasPermission()) {
                    if (receiver.hasPermission(getListeningPermission())) {
                        receiver.sendMessage(message);
                    }
                } else {
                    receiver.sendMessage(message);
                }
            } else {
                if (sender.getServer().getInfo().getName().equals(receiver.getServer().getInfo().getName())) {
                    sender.sendMessage("SAME SERVER");
                    if (this.hasPermission()) {
                        sender.sendMessage("IT HAS PERM");
                        if (receiver.hasPermission(getListeningPermission())) {
                            sender.sendMessage("SENDING");

                            receiver.sendMessage(message);
                        } else {
                            receiver.sendMessage("NO PERMISSION FOR CHANNEL");
                        }
                    } else {
                        receiver.sendMessage(message);
                    }
                }
            }
        }
    }

    @Override
    public List<ProxiedPlayer> getMembers() {
        return members;
    }

    public void addMember(ProxiedPlayer proxiedPlayer) {
        this.members.add(proxiedPlayer);
    }

    public void removeMember(ProxiedPlayer proxiedPlayer) {
        this.members.remove(proxiedPlayer);
    }
}
