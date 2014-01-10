package net.tbnr.gearz.game.classes;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.Color;

import java.util.List;

@Data
@ToString
@EqualsAndHashCode
public final class GearzItemMeta {
    private String title;
    private List<String> lore;
    private Color color;
    private String owner;
}
