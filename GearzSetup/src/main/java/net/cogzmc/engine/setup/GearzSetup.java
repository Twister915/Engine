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

package net.cogzmc.engine.setup;

import net.cogzmc.engine.server.ServerManager;
import net.cogzmc.engine.util.TPlugin;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 10/11/13
 * Time: 12:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class GearzSetup extends TPlugin {
    private static GearzSetup instance;

    public static GearzSetup getInstance() {
        return GearzSetup.instance;
    }

    @Override
    public void enable() {
        GearzSetup.instance = this;
        getConfig().options().copyDefaults(true);
        saveConfig();
        GameSetupFactory setupFactory = new GameSetupFactory();
        registerEvents(setupFactory);
        registerCommands(setupFactory);
        ServerManager.setOpenForJoining(true);
    }

    @Override
    public void disable() {
    }

    @Override
    public String getStorablePrefix() {
        return "setup";
    }
}
