package net.tbnr.gearz.game.classes;

import lombok.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an ingame class
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public final class GearzClass {
    @Setter(AccessLevel.PACKAGE) @NonNull
    private String name;
    @Setter(AccessLevel.PACKAGE)
    private List<GearzItem> armour;
    @Setter(AccessLevel.PACKAGE) @NonNull
    private List<GearzItem> items;
    @Setter(AccessLevel.PACKAGE)
    private List<GearzStatusEffect> statusEffects;
    @Setter(AccessLevel.PACKAGE) @NonNull
    private GearzItem representationItem;

    public static GearzClass classFromJsonObject(JSONObject object) throws GearzClassReadException {
        String name;
        JSONObject repItem;
        JSONArray items;
        try {
            name = object.getString("name");
            repItem = object.getJSONObject("repItem");
            items = object.getJSONArray("items");
        } catch (JSONException e) {
            throw exceptionFromJSON("Missing critical field in class definition", e);
        }
        JSONArray armour = null;
        try {
            armour = object.getJSONArray("armour");
        } catch (JSONException ignored) {
        }
        List<GearzItem> gearzItems = new ArrayList<>();
        List<GearzItem> gearzArmour = null;
        for (int x = 0; x < items.length(); x++) {
            JSONObject item;
            try {
                item = items.getJSONObject(x);
            } catch (JSONException e) {
                throw exceptionFromJSON("Invalid object specified for item at index " + x, e);
            }
            GearzItem gItem = GearzItem.fromJsonObject(item);
            gearzItems.add(gItem);
        }
        if (armour != null) {
            gearzArmour = new ArrayList<>();
            for (int x = 0; x < armour.length(); x++) {
                JSONObject item;
                try {
                    item = armour.getJSONObject(x);
                } catch (JSONException e) {
                    throw exceptionFromJSON("Invalid Armour Block defined at " + x, e);
                }
                GearzItem gItem = GearzItem.fromJsonObject(item);
                gearzArmour.add(gItem);
            }
        }
        JSONArray statusEfs = null;
        try {
            statusEfs = object.getJSONArray("status_effects");
        } catch (JSONException ignored) {
        }
        List<GearzStatusEffect> statusEffects = null;
        if (statusEfs != null) {
            statusEffects = new ArrayList<>();
            for (int x = 0; x < statusEfs.length(); x++) {
                JSONObject jsonObject;
                try {
                    jsonObject = statusEfs.getJSONObject(x);
                } catch (JSONException e) {
                    throw exceptionFromJSON("Error reading status effect at " + x, e);
                }
                statusEffects.add(GearzStatusEffect.fromJSONResource(jsonObject));
            }
        }
        GearzItem repItem1 = GearzItem.fromJsonObject(repItem);
        return new GearzClass(name, gearzArmour, gearzItems, statusEffects, repItem1);
    }

    static GearzClassReadException exceptionFromJSON(String reason, JSONException ex) {
        GearzClassReadException readException = new GearzClassReadException(reason);
        readException.setJsonException(ex);
        return readException;
    }
}
