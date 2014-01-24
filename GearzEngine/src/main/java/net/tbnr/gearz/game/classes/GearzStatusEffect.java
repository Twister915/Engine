package net.tbnr.gearz.game.classes;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.JSONException;
import org.json.JSONObject;

@Data
@AllArgsConstructor
public final class GearzStatusEffect {
    @Setter(AccessLevel.PACKAGE)
    private final PotionEffectType potionEffectType;
    @Setter(AccessLevel.PACKAGE)
    private final Integer amplification;
    @Setter(AccessLevel.PACKAGE)
    private final Integer length;
    @Setter(AccessLevel.PACKAGE)
    private final boolean ambient;

    static GearzStatusEffect fromJSONResource(JSONObject object) throws GearzClassReadException {
        String name;
        try {
            name = object.getString("name");
        } catch (JSONException e) {
            throw GearzClass.exceptionFromJSON("Name of Status Effect not defined!", e);
        }
        Integer amplification = 0;
        Integer length = Integer.MAX_VALUE;
        boolean ambient = true;
        try {
            if (object.has("level")) {
                amplification = object.getInt("level");
            }
            if (object.has("length")) {
                length = object.getInt("length");
            }
            if (object.has("ambient")) {
                ambient = object.getBoolean("ambient");
            }
        } catch (JSONException e) {
            throw GearzClass.exceptionFromJSON("Invalid amplified or not found", e);
        }
        PotionEffectType type = PotionEffectType.getByName(name);
        if (type == null) {
            throw new GearzClassReadException("Invalid Status Effect name supplied!");
        }
        return new GearzStatusEffect(type, amplification, length, ambient);
    }

    PotionEffect getPotionEffect() {
        return new PotionEffect(this.potionEffectType, this.length == Integer.MAX_VALUE ? Integer.MAX_VALUE : this.length * 20, this.amplification, this.ambient);
    }
}
