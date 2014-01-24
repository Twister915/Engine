package net.tbnr.gearz;

import net.tbnr.gearz.server.ServerManager;
import net.tbnr.util.TPlugin;

/**
 * Created by Jake on 1/12/14.
 */
public class GearzMapDev extends TPlugin {
    private static GearzMapDev instance;

    public static GearzMapDev getInstance() {
        return GearzMapDev.instance;
    }
    @Override
    public void enable() {
        getConfig().options().copyDefaults(true);
        saveConfig();
        GearzMapDev.instance = this;
        ServerManager.setOpenForJoining(true);
        registerCommands(new InstallCommands());
    }

    @Override
    public void disable() {

    }

    @Override
    public String getStorablePrefix() {
        return "mapdev";
    }
}
