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

package net.tbnr.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class JsonChatUtil implements GUtility {

    public static void sendUrlWithPopup(String textBefore, String textAfter, String text, String url, boolean underlined, Player player) {
        String json = "{text:\"" + text + "\", " + (underlined ? "underlined:true, " : "") + "hoverEvent:{ action:show_text, value:\"" + url + "\" }, clickEvent:{ action:open_url, value:\"" + url + "\"} }";
        sendJsonMessage(json, player);
    }

    public static void sendClickableUrl(String textBefore, String textAfter, String url, boolean underlined, Player player) {
        String json = "{text:\"" + url + "\", " + (underlined ? "underlined:true, " : "") + "clickEvent:{ action:open_url, value:\"" + url + "\"} }";
        sendJsonMessage(json, player);
    }

    public static void sendJsonMessage(String json, Player player) {
        PacketContainer chat = new PacketContainer(PacketType.Play.Server.CHAT);
        chat.getChatComponents().write(0, WrappedChatComponent.fromJson(json));

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, chat);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
