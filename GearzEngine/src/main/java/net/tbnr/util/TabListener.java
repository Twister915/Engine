package net.tbnr.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;
import net.tbnr.gearz.Gearz;

/**
 * Created by rigor789 on 2013.12.27..
 */
public class TabListener {

    public TabListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Gearz.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.TAB_COMPLETE) {
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
