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

package net.tbnr.gearz;

import net.tbnr.gearz.server.ServerManager;
import net.tbnr.util.TPlugin;

/**
 * Created by Jake on 1/12/14.
 * <p/>
 * Purpose Of File:
 * <p/>
 * Latest Change:
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
        registerCommands(new PackageCommands());
    }

    @Override
    public void disable() {

    }

    @Override
    public String getStorablePrefix() {
        return "mapdev";
    }
}
