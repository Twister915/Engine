package net.tbnr.gearz.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.tbnr.gearz.GearzBungee;


/**
 * Created by jake on 12/29/13.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public class PrivateConversation implements Listener {
    Conversation conversation;

    public PrivateConversation(ProxiedPlayer sender, ProxiedPlayer target) {
        this.conversation = new Conversation(sender, target);
        GearzBungee.getInstance().getChat().getConversations().add(conversation);
        sender.sendMessage(GearzBungee.getInstance().getFormat("conversation-start", false, true, new String[]{"<player>", target.getName()}));
        GearzBungee.getInstance().registerEvents(this);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    @SuppressWarnings("unused")
    public void onChat(ChatEvent event) {
        if (event.isCancelled()) return;
        if (event.isCommand()) return;
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();

        if (!GearzBungee.getInstance().getChat().isPlayerInConversation(player)) return;

        Filter.FilterData filterData = Filter.filter(event.getMessage(), player);
        if (!filterData.isCancelled()) {

            GearzBungee.getInstance().getChat().getConversationForPlayer(player).sendMessage(filterData.getMessage());

            event.setCancelled(true);
        } else {
            event.setCancelled(true);
        }

    }


    @AllArgsConstructor
    @Data
    @EqualsAndHashCode
    public class Conversation {
        private ProxiedPlayer sender;
        private ProxiedPlayer target;

        public void sendMessage(String message) {
            String sendToSender = GearzBungee.getInstance().getFormat("messaging-message", false, false, new String[]{"<sender>", target.getName()}, new String[]{"<message>", message}, new String[]{"<direction>", "to"});

            String sendToTarget = GearzBungee.getInstance().getFormat("messaging-message", false, false, new String[]{"<sender>", sender.getName()}, new String[]{"<message>", message}, new String[]{"<direction>", "from"});

            sender.sendMessage(sendToSender);
            target.sendMessage(sendToTarget);

        }

        public void end() {
            conversation.getSender().sendMessage(GearzBungee.getInstance().getFormat("conversation-end", false, true, new String[]{"<player>", conversation.getTarget().getName()}));
            GearzBungee.getInstance().getChat().getConversations().remove(this);
        }
    }

}
