package net.cogz.permissions.bungee;

import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.TimeUnit;

/**
 * Created by Jake on 1/24/14.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public class GearzBungeePermissions extends Plugin {
    @Getter
    private static GearzBungeePermissions instance;
    @Getter private PermissionsManager permsManager;

    @Override
    public void onEnable() {
        GearzBungeePermissions.instance = this;
        this.permsManager = new PermissionsManager();
        ProxyServer.getInstance().getPluginManager().registerListener(this, permsManager);
        ProxyServer.getInstance().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {
                try {
                    GearzBungeePermissions.getInstance().getPermsManager().reload();
                } catch (Exception ex) {
                    GearzBungeePermissions.getInstance().getLogger().severe(ex.getMessage());
                }
            }
        }, 0, 30, TimeUnit.SECONDS);
    }

    @Override
    public void onDisable() {
    }
}
