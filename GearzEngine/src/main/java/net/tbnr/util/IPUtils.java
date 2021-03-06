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

package net.tbnr.util;

import net.tbnr.gearz.Gearz;
import net.tbnr.util.annotations.GUtility;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Created by George on 24/12/13.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public final class IPUtils implements GUtility {

    /**
     * Returns player ping via the eventHandler passed in
     * Standard timeout of 9999 milliseconds ~ 9.999 seconds
     *
     * @param ip ~ Player to ping
     */
    public static void getPing(final InetAddress ip, final PingCallbackEventHandler ev) {
        Bukkit.getScheduler().runTaskAsynchronously(Gearz.getInstance(), new BukkitRunnable() {
            @SuppressWarnings("ResultOfMethodCallIgnored")
            @Override
            public void run() {
                float oldTime = System.currentTimeMillis();

                //give up trying to reach it at 9.999 seconds
                try {
	                ip.isReachable(9999);
                } catch (IOException e) {
                    /*If Not reachable fail silently and return 9999 as it's the default ping*/
                    ev.onPingCallback(new PingCallbackEvent(9999));
                    return;
                }

                float newTime = System.currentTimeMillis();

                //time it took in miliseconds
                float totalTime = newTime - oldTime;

                ev.onPingCallback(new PingCallbackEvent(totalTime));
            }

        });
    }

    public static String getExternalIP() throws SocketException, IndexOutOfBoundsException {
        if (!Bukkit.getIp().equals("")) return Bukkit.getIp();
        NetworkInterface eth0 = NetworkInterface.getByName(Gearz.getInstance().getConfig().getString("network_interface"));

        if (eth0 == null) eth0 = NetworkInterface.getByName("eth0");

        Enumeration<InetAddress> inetAddresses = eth0.getInetAddresses();
        ArrayList<InetAddress> list = Collections.list(inetAddresses);
        InetAddress fin = null;
        for (InetAddress inetAddress : list) {
            if (inetAddress.getHostAddress().matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}")) {
                fin = inetAddress;
                break;
            }
        }
        return fin == null ? null : fin.getHostAddress();
    }

    public interface PingCallbackEventHandler {
        public void onPingCallback(PingCallbackEvent e);
    }

    public static class PingCallbackEvent {

        float ping = 9999;

        public PingCallbackEvent(float ping) {
            this.ping = ping;
        }

        public float getPing() {
            return ping;
        }

    }
}
