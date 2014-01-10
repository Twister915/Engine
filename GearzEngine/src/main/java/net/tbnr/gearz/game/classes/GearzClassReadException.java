package net.tbnr.gearz.game.classes;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.tbnr.gearz.GearzException;
import org.json.JSONException;

@EqualsAndHashCode(callSuper = false)
@Data
public final class GearzClassReadException extends GearzException {
    private JSONException jsonException;

    public GearzClassReadException(String s) {
        super(s);
    }
}
