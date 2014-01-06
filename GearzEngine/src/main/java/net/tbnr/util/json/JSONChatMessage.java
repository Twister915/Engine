package net.tbnr.util.json;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class JSONChatMessage {
    private JSONObject chatObject;

    public JSONChatMessage(String text, JSONChatColor color, List<JSONChatFormat> formats) {
        chatObject = new JSONObject();
        chatObject.put("text", text);
        if (color != null) {
            chatObject.put("color", color.getColorString());
        }
        if (formats != null) {
            for (JSONChatFormat format : formats) {
                chatObject.put(format.getFormatString(), true);
            }
        }
    }

    public void addExtra(JSONChatExtra extraObject) {
        if (!chatObject.containsKey("extra")) {
            chatObject.put("extra", new JSONArray());
        }
        JSONArray extra = (JSONArray) chatObject.get("extra");
        extra.add(extraObject.toJSON());
        chatObject.put("extra", extra);
    }

    public void sendToPlayer(Player player) {
        PacketContainer chat = new PacketContainer(PacketType.Play.Server.CHAT);
        chat.getChatComponents().write(0, WrappedChatComponent.fromJson(chatObject.toJSONString()));

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, chat);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return chatObject.toJSONString();
    }
}
