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

package net.cogz.engine.hub;

import lombok.Getter;
import net.cogz.engine.hub.annotations.HubItems;
import net.cogz.engine.hub.modules.Spawn;
import net.tbnr.gearz.server.Server;
import net.tbnr.gearz.server.ServerManager;
import net.tbnr.util.IPUtils;
import net.tbnr.util.TPlugin;
import org.bukkit.Bukkit;

import java.net.SocketException;

/**
 * <p/>
 * Latest Change:
 * <p/>
 *
 * @author Jake
 * @since 5/16/2014
 */
public class GearzHub extends TPlugin {
    @Getter
    public static GearzHub instance;
    @Getter
    private HubItems hubItems;

    public GearzHub(String itemsPackage) {
        this.hubItems = new HubItems(itemsPackage);
    }

    public void enableHub() {}

    public void disableHub() {}

    @Override
    public void enable() {
        GearzHub.instance = this;
        registerEvents(this.hubItems);
        Spawn spawn = new Spawn();
        registerCommands(spawn);
        registerEvents(spawn);

        //Save the hub to the database
        ServerManager.setGame("lobby");
        ServerManager.setStatusString("HUB_DEFAULT");
        ServerManager.setOpenForJoining(true);
        Server thisServer = ServerManager.getThisServer();
        try {
            thisServer.setAddress(IPUtils.getExternalIP());
        } catch (SocketException e) {
            e.printStackTrace();
        }
        thisServer.setPort(Bukkit.getPort());
        thisServer.save();
        enableHub();
    }

    @Override
    public void disable() {
        disableHub();
    }

    @Override
    public String getStorablePrefix() {
        return "gearzhub";
    }
}
