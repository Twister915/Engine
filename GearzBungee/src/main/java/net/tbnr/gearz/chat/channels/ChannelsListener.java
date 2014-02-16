/*
 * Copyright (c) 2014.
 * Cogz Development LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.tbnr.gearz.chat.channels;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.gearz.punishments.LoginHandler;
import net.tbnr.gearz.punishments.PunishmentType;

import java.text.SimpleDateFormat;

/**
 * Created by Jake on 1/16/14.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public class ChannelsListener implements Listener {
    public final SimpleDateFormat longReadable = new SimpleDateFormat("MM/dd/yyyy hh:mm zzzz");

    @EventHandler
    @SuppressWarnings("unused")
    public void onChat(ChatEvent event) {
        if (!GearzBungee.getInstance().getChannelManager().isEnabled()) return;
        if (event.isCancelled()) return;
        if (event.isCommand()) return;
        if (event.getMessage().contains("\\")) {
            event.getSender().disconnect("Bad.");
            event.setCancelled(true);
            return;
        }
        ProxiedPlayer sender = (ProxiedPlayer) event.getSender();

        if (GearzBungee.getInstance().getChat().isPlayerInConversation(sender)) return;
        if (GearzBungee.getInstance().getChat().isMuted()) {
            event.setCancelled(true);
            sender.sendMessage(GearzBungee.getInstance().getFormat("chat-muted"));
            return;
        }
        if (GearzBungee.getInstance().getChat().isPlayerMuted(sender.getName())) {
            LoginHandler.MuteData muteData = GearzBungee.getInstance().getChat().getMute(sender.getName());
            if (muteData.getPunishmentType() == PunishmentType.MUTE) {
                sender.sendMessage(GearzBungee.getInstance().getFormat("muted", false, false, new String[]{"<reason>", muteData.getReason()}, new String[]{"<issuer>", muteData.getIssuer()}));
            } else if (muteData.getPunishmentType() == PunishmentType.TEMP_MUTE) {
                sender.sendMessage(GearzBungee.getInstance().getFormat("temp-muted", false, false, new String[]{"<reason>", muteData.getReason()}, new String[]{"<issuer>", muteData.getIssuer()}, new String[]{"<end>", longReadable.format(muteData.getEnd())}));
            }
            event.setCancelled(true);
            return;
        }

        String message = event.getMessage();
        GearzBungee.getInstance().getChannelManager().sendMessage(sender, message);
        event.setCancelled(true);
    }
}
