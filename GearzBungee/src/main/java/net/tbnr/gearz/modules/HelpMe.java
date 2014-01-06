package net.tbnr.gearz.modules;

import lombok.*;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.util.bungee.command.TCommand;
import net.tbnr.util.bungee.command.TCommandHandler;
import net.tbnr.util.bungee.command.TCommandSender;
import net.tbnr.util.bungee.command.TCommandStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Joey on 12/17/13.
 */
public class HelpMe implements TCommandHandler, Listener {
    private HashMap<String, Boolean> activeResponders = new HashMap<>();
    private ArrayList<Conversation> conversations = new ArrayList<>();

    @SuppressWarnings("UnusedParameters")
    @TCommand(name = "helpme", aliases = {"modreq", "staff"}, permission = "gearz.helpme", senders = {TCommandSender.Player}, usage = "/helpme <args>")
    public TCommandStatus helpme(CommandSender sender, TCommandSender type, TCommand meta, String[] args) {
        ProxiedPlayer sender1 = (ProxiedPlayer) sender;
        if (args.length > 0) {
            switch (args[0]) {
                case "duty":
                    if (!sender.hasPermission("gearz.helpme.staff")) return TCommandStatus.PERMISSIONS;
                    setDuty(sender1, !activeResponders.containsKey(sender1.getName()));
                    return TCommandStatus.SUCCESSFUL;
                case "done":
                    if (getConvoFor(sender1) == null) return TCommandStatus.INVALID_ARGS;
                    endConversation(sender1);
                    return TCommandStatus.SUCCESSFUL;
                case "cancel":
                    Conversation convoFor = getConvoFor(sender1);
                    if (convoFor == null) return TCommandStatus.INVALID_ARGS;
                    if (convoFor.isActive()) {
                        endConversation(sender1);
                    } else {
                        this.conversations.remove(convoFor);
                        sender1.sendMessage(GearzBungee.getInstance().getFormat("helpme-canceled", false, true));
                    }
                    return TCommandStatus.SUCCESSFUL;
                case "chat":
                    if (!sender.hasPermission("gearz.helpme.staff")) return TCommandStatus.PERMISSIONS;
                    if (getConvoFor(sender1) != null) return TCommandStatus.INVALID_ARGS;
                    List<Conversation> pendingConversations = getPendingConversations();
                    if (pendingConversations.size() == 0) {
                        sender.sendMessage(GearzBungee.getInstance().getFormat("helpme-no-conversations", false));
                        return TCommandStatus.SUCCESSFUL;
                    }
                    Conversation conversation = pendingConversations.get(0);
                    conversation.setStaffMember(sender1);
                    this.activeResponders.put(sender1.getName(), false);
                    conversation.startConvo();
                    return TCommandStatus.SUCCESSFUL;
            }
        } else {
            if (getConvoFor(sender1) != null) {
                sender1.sendMessage(GearzBungee.getInstance().getFormat("helpme-waiting", false));
                return TCommandStatus.SUCCESSFUL;
            }

            Conversation convo = new Conversation(sender1);
            this.conversations.add(convo);
            remindAllStaff();
            sender1.sendMessage(GearzBungee.getInstance().getFormat("helpme-starting", false));
            return TCommandStatus.SUCCESSFUL;
        }
        return TCommandStatus.INVALID_ARGS;
    }

    public void registerReminderTask(Integer seconds) {
        ProxyServer.getInstance().getScheduler().schedule(GearzBungee.getInstance(), new RemindTask(this), 0, seconds, TimeUnit.SECONDS);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(ChatEvent event) {
        if (event.isCommand() || event.isCancelled()) return;
        if (!(event.getSender() instanceof ProxiedPlayer)) return;
        Conversation conversation = getConvoFor((ProxiedPlayer) event.getSender());
        if (conversation != null) {
            if (!conversation.isActive() && event.getSender().equals(conversation.getPlayer())) {
                ((ProxiedPlayer) event.getSender()).sendMessage(GearzBungee.getInstance().getFormat("helpme-waiting", false));
                event.setCancelled(true);
                return;
            }
            conversation.handleMessage(event.getMessage(), (ProxiedPlayer) event.getSender());
            event.setCancelled(true);
        }
    }

    private Conversation getConvoFor(ProxiedPlayer player) {
        Conversation conversation = null;
        for (Conversation c : conversations) {
            if ((c.getPlayer() != null && c.getPlayer().equals(player)) || (c.getStaffMember() != null && c.getStaffMember().equals(player))) {
                conversation = c;
                break;
            }
        }
        return conversation;
    }

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        if (!event.getPlayer().hasPermission("gearz.helpme.staff")) return;
        event.getPlayer().sendMessage(GearzBungee.getInstance().getFormat("helpme-login-staff", false));
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        Conversation convoFor = getConvoFor(event.getPlayer());
        if (convoFor != null) {
            convoFor.setActive(false);
            if (convoFor.getStaffMember() == null) {
                return;
            }
            if (convoFor.getStaffMember().equals(event.getPlayer())) {
                convoFor.getPlayer().sendMessage(GearzBungee.getInstance().getFormat("helpme-disconnected", false));
            }
            if (convoFor.getPlayer().equals(event.getPlayer())) {
                convoFor.getStaffMember().sendMessage(GearzBungee.getInstance().getFormat("helpme-disconnected", false));
                remindStaff(event.getPlayer());
            }
            this.conversations.remove(convoFor);
        }
        if (this.activeResponders.containsKey(event.getPlayer().getName()))
            this.activeResponders.remove(event.getPlayer().getName());
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GearzBungee.handleCommandStatus(status, sender);
    }

    private void endConversation(ProxiedPlayer player) {
        Conversation convoFor = getConvoFor(player);
        endConversation(convoFor);
        if (getPendingConversations().size() > 0) {
            ProxiedPlayer staffMember = convoFor.getStaffMember();
            this.activeResponders.put(staffMember.getName(), true);
            remindStaff(staffMember);
        }
    }

    private void endConversation(Conversation convoFor) {
        convoFor.end();
        conversations.remove(convoFor);
    }

    private void remindStaff(ProxiedPlayer staffMember) {
        staffMember.sendMessage(GearzBungee.getInstance().getFormat("helpme-remind", false));
    }

    public void remindAllStaff() {
        if (getPendingConversations().size() == 0) return;
        for (Map.Entry<String, Boolean> stringBooleanEntry : activeResponders.entrySet()) {
            if (stringBooleanEntry.getValue()) {
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(stringBooleanEntry.getKey());
                if (player != null) {
                    remindStaff(player);
                }
            }
        }
    }

    private List<Conversation> getPendingConversations() {
        List<Conversation> conversations1 = new ArrayList<>();
        for (Conversation c : this.conversations) {
            if (!c.isActive()) conversations1.add(c);
        }
        return conversations1;
    }

    private void setDuty(ProxiedPlayer sender1, boolean duty) {
        String name = sender1.getName();
        if (duty) {
            activeResponders.put(name, true);
            sender1.sendMessage(GearzBungee.getInstance().getFormat("helpme-onduty", false));
        } else {
            activeResponders.remove(name);
            sender1.sendMessage(GearzBungee.getInstance().getFormat("helpme-offduty", false));
        }
    }

    @Data
    @EqualsAndHashCode
    @ToString
    @RequiredArgsConstructor
    public static class Conversation {
        private ProxiedPlayer staffMember;
        @NonNull
        private ProxiedPlayer player;
        @Getter
        private boolean active = false;
        @Getter
        private List<String> messages = new ArrayList<>();

        public void startConvo() {
            staffMember.sendMessage(GearzBungee.getInstance().getFormat("helpme-start", false, true, new String[]{"<player>", player.getName()}));
            player.sendMessage(GearzBungee.getInstance().getFormat("helpme-start", false, true, new String[]{"<player>", staffMember.getName()}));
            sendMessage(GearzBungee.getInstance().getFormat("helpme-remind-done", false));
            this.active = true;
        }

        public void handleMessage(String message, ProxiedPlayer player) {
            String format = GearzBungee.getInstance().getFormat("helpme-staff-chat", false, true, new String[]{"<player>", player.getName()}, new String[]{"<message>", message});
            sendMessage(format);
            this.messages.add(player.getName() + ":" + message);
        }

        public void end() {
            sendMessage(GearzBungee.getInstance().getFormat("helpme-end", false));
            this.active = false;
        }

        private void sendMessage(String format) {
            staffMember.sendMessage(format);
            player.sendMessage(format);
        }
    }

    @RequiredArgsConstructor
    public static class RemindTask implements Runnable {
        @NonNull
        private HelpMe module;

        @Override
        public void run() {
            module.remindAllStaff();
        }
    }
}
