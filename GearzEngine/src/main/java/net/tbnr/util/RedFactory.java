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
import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.packets.wrapper.WrapperPlayServerAnimation;
import net.tbnr.gearz.packets.wrapper.WrapperPlayServerAnimation.Animations;
import net.tbnr.util.player.TPlayer;
import net.tbnr.util.player.TPlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
public class RedFactory implements GUtility {
	private static final boolean debug = false; //TODO use gearz debug mode
	private static final Logger log = null; //TODO get logger

	private static List<TPlayer> redPlayers = new ArrayList<>();
	private static ProtocolManager protocolManager;
	private static WrapperPlayServerAnimation wrapperPlayServerAnimation;

	public RedFactory() {
		protocolManager = ProtocolLibrary.getProtocolManager();
		wrapperPlayServerAnimation = getWrapper();
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
				// loop through the player list
				for (TPlayer tPlayer : redPlayers) {

					WrapperPlayServerAnimation wrapper = wrapperPlayServerAnimation;
					wrapper.setEntityID(tPlayer.getPlayer().getEntityId());
					PacketContainer packetContainer = wrapper.getHandle();

					try {
						Player p = tPlayer.getPlayer();
						if (p == null || !p.isValid()) continue;

						for (Player pl : p.getWorld().getPlayers()) {
							if(pl.equals(p)) continue;

							// only send if the player is in range
							if (pl.getLocation().distance(p.getLocation()) <= 50) {
								protocolManager.sendServerPacket(pl, packetContainer);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.runTaskTimer(Gearz.getInstance(), 0, 20);
	}

	private static WrapperPlayServerAnimation getWrapper() {
		WrapperPlayServerAnimation fakeHit = new WrapperPlayServerAnimation();
		fakeHit.setAnimation(Animations.DAMAGE_ANIMATION);
		return fakeHit;
	}
}
