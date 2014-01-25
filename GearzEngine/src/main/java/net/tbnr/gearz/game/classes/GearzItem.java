package net.tbnr.gearz.game.classes;

import com.comphenix.protocol.utility.MinecraftReflection;
import lombok.*;
import net.tbnr.gearz.Gearz;
import net.tbnr.util.ColoringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode
@RequiredArgsConstructor
public final class GearzItem {
    @Setter(AccessLevel.PACKAGE) @NonNull
    private Material material;
    @Setter(AccessLevel.PACKAGE) @NonNull
    private Integer quantity;
    @Setter(AccessLevel.PACKAGE)
    private Short data;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection") @Setter(AccessLevel.PACKAGE)
    private Map<Enchantment, Integer> enchantments;
    @NonNull
    private GearzItemMeta itemMeta;
    private Integer slot;

    static GearzItem fromJsonObject(JSONObject object) throws GearzClassReadException {
        String materialName;
        Integer quantity = 1;
        try {
            materialName = object.getString("item_type");
        } catch (JSONException ex) {
            throw GearzClass.exceptionFromJSON("Could not read class", ex);
        }
        try {
            quantity = object.getInt("quantity");
        } catch (JSONException ignored) {
        }
        Material material = Material.getMaterial(materialName);
        if (material == null) {
            throw new GearzClassReadException("Invalid Material Specified: " + materialName);
        }
        JSONArray enchants = null;
        Short data = null;
        try {
            if (object.has("enchants")) {
                enchants = object.getJSONArray("enchants");
            }
            if (object.has("data")) {
                data = (short) object.getInt("data");
            }
        } catch (JSONException ignored) {
        }
        HashMap<Enchantment, Integer> enchantmentMap = null;
        if (enchants != null) {
            enchantmentMap = new HashMap<>();
            for (int x = 0; x < enchants.length(); x++) {
                try {
                    JSONObject enchantObject = enchants.getJSONObject(x);
                    String enchant_name = enchantObject.getString("name");
                    int level = enchantObject.getInt("level");
                    Enchantment e = Enchantment.getByName(enchant_name);
                    if (e == null || level < 1) {
                        throw new GearzClassReadException("Invalid Enchantment " + x + " " + enchant_name + " " + level);
                    }
                    enchantmentMap.put(e, level);
                    Gearz.getInstance().getLogger().info("Added enchant " + x + " " + e.getName() + ":" + level);
                } catch (JSONException e) {
                    throw GearzClass.exceptionFromJSON("Could not read enchantment " + x, e);
                }
            }
        }
        GearzItemMeta itemMeta = new GearzItemMeta();
        try {
            if (object.has("title")) {
                itemMeta.setTitle(object.getString("title"));
            }
            if (object.has("lore")) {
                JSONArray loreJSON = object.getJSONArray("lore");
                List<String> lore = new ArrayList<>();
                for (int y = 0; y < loreJSON.length(); y++) {
                    lore.add(loreJSON.getString(y));
                }
                itemMeta.setLore(lore);
            }
            if (object.has("owner")) {
                itemMeta.setOwner(object.getString("owner"));
            }
            if (object.has("color")) {
                Color decode = Color.decode(object.getString("color"));
                org.bukkit.Color color = org.bukkit.Color.fromRGB(decode.getRed(), decode.getGreen(), decode.getBlue());
                itemMeta.setColor(color);
            }
        } catch (JSONException ex) {
            throw GearzClass.exceptionFromJSON("Could not read meta", ex);
        }
        GearzItem gearzItem = new GearzItem(material, quantity, itemMeta);
        if (enchantmentMap != null) {
            gearzItem.setEnchantments(enchantmentMap);
        }
        if (data != null) {
            gearzItem.setData(data);
        }
        return gearzItem;
    }

    public ItemStack getItemStack() {
        ItemStack stack = new ItemStack(this.material);
        stack = MinecraftReflection.getBukkitItemStack(stack);
        stack.setAmount(this.quantity);
        if (this.enchantments != null) {
            for (Map.Entry<Enchantment, Integer> enchantmentIntegerEntry : this.enchantments.entrySet()) {
                stack.addUnsafeEnchantment(enchantmentIntegerEntry.getKey(), enchantmentIntegerEntry.getValue());
                Gearz.getInstance().getLogger().info("Assigned enchant " + enchantmentIntegerEntry.getKey().getName() + ":" + enchantmentIntegerEntry.getValue());
            }
        }
        ItemMeta itemMeta1 = stack.getItemMeta();
        if (this.itemMeta.getTitle() != null) {
            itemMeta1.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.itemMeta.getTitle()));
        }
        if (this.itemMeta.getLore() != null) {
            itemMeta1.setLore(ColoringUtils.colorStringList(this.itemMeta.getLore()));
        }
        if (this.itemMeta.getOwner() != null && itemMeta1 instanceof SkullMeta) {
            ((SkullMeta) itemMeta1).setOwner(this.itemMeta.getOwner());
        }
        if (this.itemMeta.getColor() != null && itemMeta1 instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) itemMeta1).setColor(this.itemMeta.getColor());
        }
        stack.setItemMeta(itemMeta1);
        if (this.data != null) {
            stack.setDurability(this.data);
        }
        return stack;
    }
}
