package net.cogz.permissions.bukkit;

import lombok.Getter;
import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.activerecord.GModel;
import net.tbnr.util.TPlugin;
import org.bukkit.Bukkit;

/**
 * Created by Jake on 1/24/14.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public final class GearzBukkitPermissions extends TPlugin {
    @Getter private static GearzBukkitPermissions instance;
    @Getter public PermissionsManager permsManager;

    @Override
    public void enable() {
        GearzBukkitPermissions.instance = this;
        GModel.setDefaultDatabase(Gearz.getInstance().getMongoDB());
        this.permsManager = new PermissionsManager();
        permsManager.reload();
        getConfig().options().copyDefaults(true);
        saveConfig();
        getServer().getPluginManager().registerEvents(this.permsManager, this);
        Gearz.getInstance().setPermissionsDelegate(permsManager);
        Gearz.getInstance().activatePermissionsFeatures();
        PermissionsCommands permsCommands = new PermissionsCommands();
        registerCommands(permsCommands);
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
        if (getConfig().getBoolean("converter", false)) {
            try {
                new Converter();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void disable() {
    }

    @Override
    public String getStorablePrefix() {
        return "bukkitperms";
    }
}
