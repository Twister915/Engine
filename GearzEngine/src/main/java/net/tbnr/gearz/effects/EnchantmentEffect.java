package net.tbnr.gearz.effects;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import net.tbnr.gearz.Gearz;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class EnchantmentEffect {

    /**
     * Example of how to hide enchantment name but still have effect
     * <p/>
     * // Get a CraftItemStack
     * ItemStack stack = new ItemStack(Material.IRON_AXE, 1);
     * stack = MinecraftReflection.getBukkitItemStack(stack);
     * <p/>
     * // We'll co-op Silk Touch 32 (as it doesn't make much sense) to mark this item as glowing
     * stack.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 32);
     * player.getInventory().addItem(stack);
     */

    public static void addEnchantmentListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Gearz.getInstance(), ConnectionSide.SERVER_SIDE, ListenerPriority.HIGH, Packets.Server.SET_SLOT, Packets.Server.WINDOW_ITEMS) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (event.getPacketID() == Packets.Server.SET_SLOT) {
                    addGlow(new ItemStack[]{event.getPacket().getItemModifier().read(0)});
                } else {
                    addGlow(event.getPacket().getItemArrayModifier().read(0));
                }
            }
        });
    }

    private static void addGlow(ItemStack[] stacks) {
        for (ItemStack stack : stacks) {
            if (stack != null) {
                if (stack.getEnchantmentLevel(Enchantment.SILK_TOUCH) == 32) {
                    NbtCompound compound = (NbtCompound) NbtFactory.fromItemTag(stack);
                    compound.put(NbtFactory.ofList("ench"));
                }
            }
        }
    }
}
