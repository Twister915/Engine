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

package net.tbnr.util;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.cloning.Cloner;
import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.packets.wrapper.WrapperPlayServerAnimation;
import net.tbnr.gearz.packets.wrapper.WrapperPlayServerAnimation.Animations;
import net.tbnr.util.player.TPlayer;
import net.tbnr.util.player.TPlayerDisconnectEvent;
import net.tbnr.util.player.TPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by George on 14/03/14.
 * <p/>
 * Purpose Of File:
 * <p/>
 * Latest Change:
 */
public class RedFactory implements GUtility, Listener {
	private static final boolean debug = false; //TODO use gearz debug mode
	private static final Logger log = null; //TODO get logger

	private static ArrayList<TPlayer> redPlayers = new ArrayList<TPlayer>();

	public RedFactory() {
		start();
	}

	public static void addRed(Player p) {
		addRed(TPlayerManager.getInstance().getPlayer(p));
	}

	public static void addRed(TPlayer p) {
		redPlayers.add(p);
	}

	/*
	* removeRed(TPlayer player)
	* TPlayer player - the player, stop being red
	*/
	public static void removeRed(TPlayer player) {
		if (redPlayers.contains(player)) {
			redPlayers.remove(player);
		}
	}

	/*
	* removeRed(Player p)
	* Player p  - the player, stop being red
	*/
	public static void removeRed(Player p) {
		removeRed(TPlayerManager.getInstance().getPlayer(p));
	}

	/*
	* isRed(TPlayer p)
	* TPlayer p - returns if the player is red or not
	*/
	public static boolean isRed(TPlayer p){
		return !redPlayers.contains(p);
	}

	/*
	* isRed(Player p)
	* Player p - returns if the player is red or not
	*/
	public static boolean isRed(Player p){
		return isRed(TPlayerManager.getInstance().getPlayer(p));
	}

	private void start() {
		new BukkitRunnable() {
			@Override
			public void run() {
				ArrayList<TPlayer> redPlayersClone = (ArrayList<TPlayer>) redPlayers.clone();

				// loop through the player list
				for (TPlayer tPlayer : redPlayersClone) {
					Player p = tPlayer.getPlayer();

					WeakReference<WrapperPlayServerAnimation> wrapper = new WeakReference<WrapperPlayServerAnimation>(new WrapperPlayServerAnimation());
					wrapper.get().setAnimation(Animations.DAMAGE_ANIMATION);
					wrapper.get().setEntityID(p.getEntityId());

					try {
						if (p == null || !p.isValid()) continue;

						for (Player pl : p.getWorld().getPlayers()) {
							if(pl.equals(p) || pl.getLocation().distanceSquared(p.getLocation()) > 2500) continue;

							// only send if the player is in range
							wrapper.get().sendPacket(pl);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.runTaskTimer(Gearz.getInstance(), 0, 20);
	}

	@EventHandler
	void onPlayerLeaveEvent(TPlayerDisconnectEvent event) {
		if(!redPlayers.contains(event.getPlayer())) return;
		redPlayers.remove(event.getPlayer());
	}
}
