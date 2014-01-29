package net.cogz.permissions.bukkit;

import lombok.Getter;
import net.tbnr.util.TPlugin;
import org.bukkit.Bukkit;

/**
 * Created by Jake on 1/24/14.
 */
public class GearzBukkitPermissions extends TPlugin {
    @Getter private static GearzBukkitPermissions instance;
    @Getter public PermissionsManager permsManager;

    @SuppressWarnings("deprecation")
    @Override
    public void enable() {
        GearzBukkitPermissions.instance = this;
        this.permsManager = new PermissionsManager();
        registerEvents(this.permsManager);
        registerCommands(new PermissionsCommands());
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                try {
                    GearzBukkitPermissions.getInstance().getPermsManager().reload();
                } catch (Exception ex) {
                    GearzBukkitPermissions.getInstance().getLogger().severe(ex.getMessage());
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
