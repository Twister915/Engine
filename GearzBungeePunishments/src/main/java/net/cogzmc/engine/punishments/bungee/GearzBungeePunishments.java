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

package net.cogzmc.engine.punishments.bungee;

import lombok.Getter;
import net.cogzmc.engine.util.TPluginBungee;

/**
 * Bungee Punishments Plugin
 */
public class GearzBungeePunishments extends TPluginBungee {
    @Getter public static GearzBungeePunishments instance;

    @Override
    protected void start() {
        GearzBungeePunishments.instance = this;
        PunishmentManager punishmentManager = new PunishmentManager();
        punishmentManager.database = punishmentManager.getDB();
        registerCommandHandler(new UnPunishCommands(punishmentManager));
        registerCommandHandler(new PunishmentCommands(punishmentManager));
        registerEvents(punishmentManager);
        punishmentManager.loadIpBans();
    }

    @Override
    protected void stop() {
    }
}
