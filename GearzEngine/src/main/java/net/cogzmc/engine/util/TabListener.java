/*
 * Copyright (c) 2014.
 * CogzMC LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.cogzmc.engine.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;
import net.cogzmc.engine.gearz.Gearz;

/**
 * Created by rigor789 on 2013.12.27..
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public class TabListener {
    public TabListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Gearz.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.TAB_COMPLETE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (event.getPacketType() == PacketType.Play.Client.TAB_COMPLETE) {
                    try {
                        if (event.getPlayer().hasPermission("gearz.staff")) return;
                        PacketContainer packet = event.getPacket();
                        String message = packet.getSpecificModifier(String.class).read(0);
                        if ((message.startsWith("/"))) event.setCancelled(true);
                    } catch (FieldAccessException ignored) {

                    }
                }
            }
        });
    }
}
