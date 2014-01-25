package net.gogz.permissions.bukkit;

import lombok.Getter;
import net.tbnr.util.TPlugin;
import org.bukkit.Bukkit;

/**
 * Created by Jake on 1/24/14.
 */
public class GearzBukkitPerimssions extends TPlugin {
    @Getter private static GearzBukkitPerimssions instance;
    @Getter private PermissionsManager permsManager;

    @SuppressWarnings("deprecation")
    @Override
    public void enable() {
        GearzBukkitPerimssions.instance = this;
        this.permsManager = new PermissionsManager();
        registerEvents(this.permsManager);
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                try {
                    GearzBukkitPerimssions.getInstance().getPermsManager().reload();
                } catch (Exception ex) {
                    GearzBukkitPerimssions.getInstance().getLogger().severe(ex.getMessage());
                }
            }
        }, 0, 30 * 20);
    }

    @Override
    public void disable() {
    }

    @Override
    public String getStorablePrefix() {
        return "bukkitperms";
    }
}
