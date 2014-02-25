package net.tbnr.gearz.chat.channels;

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.craftminecraft.bungee.bungeeyaml.bukkitapi.Configuration;
import net.craftminecraft.bungee.bungeeyaml.bukkitapi.file.FileConfiguration;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.gearz.chat.Filter;
import net.tbnr.gearz.chat.channels.irc.Connection;
import net.tbnr.gearz.modules.PlayerInfoModule;
import net.tbnr.gearz.player.bungee.GearzPlayer;
import net.tbnr.gearz.player.bungee.GearzPlayerManager;
import net.tbnr.gearz.player.bungee.PermissionsDelegate;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Jake on 1/16/14.
 */
public class ChannelManager {
    @Getter
    public boolean enabled = false;

    @Getter
    private List<Channel> channels = new ArrayList<>();

    @Getter
    Connection connection;

    @Getter
    List<String> toJoin;

    @Getter
    boolean ircEnabled = false;

    public ChannelManager() {
        enabled = GearzBungee.getInstance().getConfig().getBoolean("channels.enabled", false);
        if (GearzBungee.getInstance().getConfig().getBoolean("irc.enabled", false) && enabled) {
            ircEnabled = true;
            toJoin = new ArrayList<>();
            FileConfiguration config = GearzBungee.getInstance().getConfig();
            this.connection = new Connection(config.getString("irc.name"), config.getString("irc.server"), config.getString("irc.login"));
            try {
                this.connection.connect();
                this.connection.printInput = config.getBoolean("irc.printout", false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void registerChannels() {
        Configuration config = GearzBungee.getInstance().getConfig();
        for (String chanName : config.getStringList("channels.list")) {
            String format = config.getString("formatting." + chanName + ".format");
            String permission = config.getString("formatting." + chanName + ".permission");
            List<String> channels = new ArrayList<>();
            boolean main = config.getBoolean("formatting." + chanName + ".default", false);
            boolean logged = config.getBoolean("formatting." + chanName + ".logged", false);
            boolean ircLinked = config.getBoolean("formatting." + chanName + ".irc.enabled");
            if (ircLinked) {
                for (String chan : config.getStringList("formatting." + chanName + ".irc.channels")) {
                    channels.add(chan);
                    toJoin.add(chan);
                }
            }
            boolean crossServer = config.getBoolean("formatting." + chanName + ".cross-server", false);
            boolean filter = config.getBoolean("formatting." + chanName + ".filter", true);
            Channel channel = new Channel(chanName, format, permission);
            channel.setDefault(main);
            channel.setIRCLinked(ircLinked);
            channel.setIRCChannels(channels);
            channel.setCrossServer(crossServer);
            channel.setFiltered(filter);
            channel.setLogged(logged);
            registerChannel(channel);
        }
    }

    public void unregisterChannels() {
        for (Channel channel : channels) {
            unregisterChannel(channel);
        }
    }

    public Channel getDefaultChannel() {
        for (Channel channel : channels) {
            if (channel.isDefault()) return channel;
        }
        return null;
    }

    private void registerChannel(Channel channel) {
        channels.add(channel);
        ProxyServer.getInstance().getLogger().info("Channel registered with name: " + channel.getName());
    }

    private void unregisterChannel(Channel channel) {
        if (channels.contains(channel)) {
            for (ProxiedPlayer player : channel.getMembers()) {
                GearzPlayer gearzPlayer = GearzPlayerManager.getGearzPlayer(player);
                gearzPlayer.setChannel(getDefaultChannel());
            }
            channels.remove(channel);
        }
    }

    private boolean isChannelRegistered(String name) {
        return getChannelByName(name) != null;
    }

    public Channel getChannelByName(String name) {
        Preconditions.checkNotNull(name, "Name can not be null");
        for (Channel channel : channels) {
            if (channel.getName().toLowerCase().equals(name)) return channel;
        }
        return null;
    }

    public Channel getCurrentChannel(ProxiedPlayer proxiedPlayer) {
        GearzPlayer target = GearzPlayerManager.getGearzPlayer(proxiedPlayer);
        return target.getChannel();
    }

    public Channel sendMessage(ProxiedPlayer sender, String message) {
        final Channel channel = getCurrentChannel(sender);
        if (channel.isFiltered()) {
            Filter.FilterData filterData = Filter.filter(message, sender);
            if (filterData.isCancelled()) {
                return channel;
            }
            message = filterData.getMessage();
        }
        if (channel.isIRCLinked() || channel.isLogged()) {
            final String toSend = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', GearzBungee.getInstance().getConfig().getString("irc.format").replace("%server%", PlayerInfoModule.getServerForBungee(sender.getServer().getInfo()).getGame()).replace("%message%", message).replace("%sender%", sender.getName())));
            ProxyServer.getInstance().getScheduler().runAsync(GearzBungee.getInstance(), new Runnable() {
                @Override
                public void run() {
                    if (channel.isIRCLinked() && ircEnabled && connection != null) {
                        for (String chan : channel.getIRCChannels()) {
                            connection.raw("PRIVMSG " + chan + " :" + toSend);
                        }
                    }
                    if (channel.isLogged()) {
                        FileWriter fWriter;
                        BufferedWriter writer;
                        String file = channel.getName() + ".txt";
                        try {
                            fWriter = new FileWriter(file);
                            writer = new BufferedWriter(fWriter);
                            String timeStamp = GearzBungee.getInstance().getReadable().format(new Date());
                            writer.write(timeStamp + toSend);
                            writer.close();
                        } catch (Exception e) {
                            GearzBungee.getInstance().getLogger().warning("Error logging chat message to " + file + ".");
                        }
                    }
                }
            });
        }
        channel.sendMessage(formatMessage(message, sender), sender);
        return channel;
    }

    private String formatMessage(String message, ProxiedPlayer player) {
        String chanFormat = getCurrentChannel(player).getFormat();
        chanFormat = chanFormat.replace("%message%", ChatColor.RESET + message).replace("%player%", player.getDisplayName());
        PermissionsDelegate perms = GearzBungee.getInstance().getPermissionsDelegate();
        if (perms != null) {
            String prefix = perms.getPrefix(player.getName().toLowerCase());
            String suffix = perms.getSuffix(player.getName().toLowerCase());
            if (prefix == null) prefix = "";
            else prefix = ChatColor.translateAlternateColorCodes('&', prefix);
            if (suffix == null) suffix = "";
            else suffix = ChatColor.translateAlternateColorCodes('&', suffix);
            chanFormat = chanFormat.replace("%prefix%", prefix).replace("%suffix%", suffix);
        } else {
            chanFormat = chanFormat.replace("%suffix%", "").replace("%prefix%", "");
        }
        chanFormat = ChatColor.translateAlternateColorCodes('&', chanFormat);
        return chanFormat;
    }
}
