package net.tbnr.gearz.settings;

import lombok.Getter;
import net.gearz.settings.SettingsManager;
import net.gearz.settings.base.BaseSetting;
import net.tbnr.util.player.TPlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Stores and retrieves setting data
 * which is stored as MetaData within
 * Bukkit's player object
 */
public final class PlayerSettingsManager extends SettingsManager {
    @Getter Plugin instance;
    @Getter Player player;

    public PlayerSettingsManager(Plugin instance, Player player) {
        this.instance = instance;
        this.player = player;
    }

    @Override
    public Object getRaw(BaseSetting setting) {
        return getMetadataValue(getMetadataKey(setting));
    }

    @Override
    public void setValue(BaseSetting setting, Object value) {
        if (instance == null) return;
        this.player.setMetadata(getMetadataKey(setting), new FixedMetadataValue(this.instance, value));
        TPlayerManager.getInstance().getPlayer(player).setSetting(setting, value);
    }

    @Override
    public void deleteValue(BaseSetting setting) {
        this.player.removeMetadata(getMetadataKey(setting), this.instance);
        TPlayerManager.getInstance().getPlayer(player).deleteSetting(setting);
    }

    private static String getMetadataKey(BaseSetting setting) {
        return "setting." + setting.getName().toLowerCase();
    }

    private Object getMetadataValue(String key) {
        if(this.instance == null) return null;
        List<MetadataValue> values = this.player.getMetadata(key);
        for(MetadataValue value : values) {
            if(value.getOwningPlugin().equals(this.instance)) {
                return value.value();
            }
        }
        return null;
    }
}
