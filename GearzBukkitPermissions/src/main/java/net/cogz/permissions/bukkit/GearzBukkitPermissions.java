/*
 * Copyright (c) 2014.
 * CogzMC LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.cogz.permissions.bukkit;

import lombok.Getter;
import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.activerecord.GModel;
import net.tbnr.util.TPlugin;
import org.bukkit.Bukkit;


/**
 * Bukkit Plugin implementation of the
 * Gearz permissions API
 *
 * <p>
 * Latest Change: Run reload ASync
 * <p>
 *
 * @author Jake
 * @since Unknown
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
    }

    @Override
    public void disable() { }

    @Override
    public String getStorablePrefix() {
        return "bukkitperms";
    }
}
