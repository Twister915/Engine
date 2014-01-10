package net.tbnr.gearz.chat;

import com.mongodb.BasicDBList;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.gearz.punishments.LoginHandler;
import net.tbnr.gearz.punishments.PunishmentType;
import net.tbnr.util.bungee.command.TCommand;
import net.tbnr.util.bungee.command.TCommandHandler;
import net.tbnr.util.bungee.command.TCommandSender;
import net.tbnr.util.bungee.command.TCommandStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 9/29/13
 * Time: 12:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class ChatManager implements Listener, TCommandHandler {
    public static enum SpyType {
        Chat,
        Command,
        All
    }

    private Map<String, SpyType> spies = new HashMap<>();
    public SimpleDateFormat longReadable = new SimpleDateFormat("MM/dd/yyyy hh:mm zzzz");

    @EventHandler(priority = EventPriority.LOWEST)
    @SuppressWarnings("unused")
    public void onChat(ChatEvent event) {
        if (event.isCancelled()) return;
        if (event.isCommand()) return;
        if (event.getMessage().contains("\\")) {
            event.getSender().disconnect("Bad.");
            event.setCancelled(true);
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();

        if (GearzBungee.getInstance().getChat().isPlayerInConversation(player)) return;

        if (GearzBungee.getInstance().getChat().isMuted()) {
            event.setCancelled(true);
            player.sendMessage(GearzBungee.getInstance().getFormat("chat-muted"));
            return;
        }

        if (GearzBungee.getInstance().getChat().isPlayerMuted(player)) {
            LoginHandler.MuteData muteData = GearzBungee.getInstance().getChat().getMute(player);
            if (muteData.getPunishmentType() == PunishmentType.MUTE) {
                player.sendMessage(GearzBungee.getInstance().getFormat("muted", false, false, new String[]{"<reason>", muteData.getReason()}, new String[]{"<issuer>", muteData.getIssuer()}));
            } else if (muteData.getPunishmentType() == PunishmentType.TEMP_MUTE) {
                player.sendMessage(GearzBungee.getInstance().getFormat("temp-muted", false, false, new String[]{"<reason>", muteData.getReason()}, new String[]{"<issuer>", muteData.getIssuer()}, new String[]{"<end>", longReadable.format(muteData.getEnd())}));
            }
            event.setCancelled(true);
            return;
        }

        Filter.FilterData filterData = Filter.filter(event.getMessage(), player);
        if (filterData.isCancelled()) {
            event.setCancelled(true);
            return;
        }

        event.setMessage(filterData.getMessage());

    }

    @TCommand(name = "spy", permission = "gearz.spy", senders = {TCommandSender.Player}, usage = "/spy [off|chat|command|all]", aliases = {"cs", "commandspy", "cw", "commandwatcher", "chatspy"})
    @SuppressWarnings("unused")
    public TCommandStatus spyCommand(CommandSender sender, TCommandSender type, TCommand meta, String[] args) {
        if (args.length < 1) return TCommandStatus.HELP;
        SpyType sType = null;
        String formatKey = null;
        if (args[0].equalsIgnoreCase("off")) {
            this.spies.remove(sender.getName());
            sender.sendMessage(GearzBungee.getInstance().getFormat("spy-off"));
            return TCommandStatus.SUCCESSFUL;
        }
        if (args[0].equalsIgnoreCase("command")) {
            sType = SpyType.Command;
            formatKey = "spy-on-command";
        }
        if (args[0].equalsIgnoreCase("chat")) {
            sType = SpyType.Chat;
            formatKey = "spy-on-chat";
        }
        if (args[0].equalsIgnoreCase("all")) {
            sType = SpyType.All;
            formatKey = "spy-on-all";
        }
        if (sType == null) {
            return TCommandStatus.INVALID_ARGS;
        }
        this.spies.put(sender.getName(), sType);
        sender.sendMessage(GearzBungee.getInstance().getFormat(formatKey));
        return TCommandStatus.SUCCESSFUL;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onChatMonitor(ChatEvent event) {
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        String m = GearzBungee.getInstance().getFormat("spy-message", false, false, new String[]{"<message>", event.getMessage()}, new String[]{"<sender>", player.getName()}, new String[]{"<server>", player.getServer().getInfo().getName()});
        for (Map.Entry<String, SpyType> p : this.spies.entrySet()) {
            ProxiedPlayer player1 = ProxyServer.getInstance().getPlayer(p.getKey());
            if (player1 == null) continue;
            if (event.isCancelled()) continue;
            if ((p.getValue() == SpyType.All) || (p.getValue() == SpyType.Command && event.isCommand()) || (p.getValue() == SpyType.Chat && !event.isCommand())) {
                player1.sendMessage(m);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    @SuppressWarnings("unused")
    public void onPlayerLeave(PlayerDisconnectEvent event) {
        if (this.spies.containsKey(event.getPlayer().getName())) this.spies.remove(event.getPlayer().getName());
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GearzBungee.handleCommandStatus(status, sender);
    }

    @TCommand(
            name = "censor",
            permission = "gearz.censor",
            usage = "/censor [list|remove|add] [message (required if applicable)]",
            senders = {TCommandSender.Player, TCommandSender.Console},
            aliases = {})
    @SuppressWarnings("unused")
    public TCommandStatus censors(CommandSender sender, TCommandSender type, TCommand meta, String[] args) {
        if (args.length < 1) return TCommandStatus.HELP;
        String command = args[0];
        Object[] censoredWords1 = GearzBungee.getInstance().getCensoredWords();
        if (command.equalsIgnoreCase("list")) {
            sender.sendMessage(GearzBungee.getInstance().getFormat("header-censorlist", false));
            int index = 0;
            for (Object o : censoredWords1) {
                index++;
                if (!(o instanceof String)) continue;
                String s = (String) o;
                sender.sendMessage(GearzBungee.getInstance().getFormat("list-motdlist", false, true, new String[]{"<index>", String.valueOf(index)}, new String[]{"<motd>", s}));
            }
            return TCommandStatus.SUCCESSFUL;
        }

        if (args.length < 2) return TCommandStatus.FEW_ARGS;
        List<String> strings = new ArrayList<>();
        for (Object o : censoredWords1) {
            if (o instanceof String) strings.add((String) o);
        }

        if (command.equalsIgnoreCase("remove")) {
            Integer toRemove = Integer.parseInt(args[1]);
            if (toRemove < 1 || toRemove > censoredWords1.length) {
                sender.sendMessage(GearzBungee.getInstance().
                        getFormat("index-out-of-range", false));
                return TCommandStatus.SUCCESSFUL;
            }
            String s = strings.get(toRemove - 1);
            strings.remove(toRemove - 1);
            sender.sendMessage(GearzBungee.getInstance().getFormat("removed-motd", false, true, new String[]{"<motd>", s}));
        } else if (command.equalsIgnoreCase("add")) {
            StringBuilder build = new StringBuilder();
            int index = 1;
            while (index < args.length) {
                build.append(args[index]).append(" ");
                index++;
            }
            String s = build.substring(0, build.length() - 1);
            strings.add(s);
            sender.sendMessage(GearzBungee.getInstance().getFormat("added-motd", false, true, new String[]{"<motd>", s}));
        } else {
            return TCommandStatus.INVALID_ARGS;
        }
        BasicDBList basicDBList = new BasicDBList();
        basicDBList.addAll(strings);
        GearzBungee.getInstance().setCensoredWords(basicDBList);
        GearzBungee.getInstance().getChat().updateCensor();
        return TCommandStatus.SUCCESSFUL;
    }

}
