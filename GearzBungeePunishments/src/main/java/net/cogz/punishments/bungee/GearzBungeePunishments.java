/*
 * Copyright (c) 2014.
 * Cogz Development LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.cogz.punishments.bungee;

import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import net.tbnr.util.config.PropertiesPlugin;

/**
 * Bungee Punishments Plugin
 */
public class GearzBungeePunishments extends PropertiesPlugin {
    @Getter public static GearzBungeePunishments instance;

    @Override
    public Plugin getPlugin() {
        return this;
    }

    @Override
    protected void start() {
        super.start();
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
        super.stop();
    }
}
