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

package net.tbnr.gearz.game.kits;

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
public final class GearzKitStatusEffect {
    @Setter(AccessLevel.PACKAGE)
    private PotionEffectType potionEffectType;
    @Setter(AccessLevel.PACKAGE)
    private Integer amplification;
    @Setter(AccessLevel.PACKAGE)
    private Integer length;
    @Setter(AccessLevel.PACKAGE)
    private boolean ambient;

    static GearzKitStatusEffect fromJSONResource(JSONObject object) throws GearzKitReadException {
        String name;
        try {
            name = object.getString("name");
        } catch (JSONException e) {
            throw GearzKit.exceptionFromJSON("Name of Status Effect not defined!", e);
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
            throw GearzKit.exceptionFromJSON("Invalid amplified or not found", e);
        }
        PotionEffectType type = PotionEffectType.getByName(name);
        if (type == null) {
            throw new GearzKitReadException("Invalid Status Effect name supplied!");
        }
        return new GearzKitStatusEffect(type, amplification, length, ambient);
    }

    PotionEffect getPotionEffect() {
        return new PotionEffect(this.potionEffectType, this.length == Integer.MAX_VALUE ? Integer.MAX_VALUE : this.length * 20, this.amplification, this.ambient);
    }
}
