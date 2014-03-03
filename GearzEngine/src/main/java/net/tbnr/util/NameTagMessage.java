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

import com.google.common.base.Preconditions;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.awt.image.BufferedImage;

/**
 * Created by George on 28/02/14.
 * <p/>
 * Purpose Of File:
 * <p/>
 * Latest Change:
 */
public class NameTagMessage extends ImageMessage {
	private NameTagSpawner spawner;
	private Location location;

	private double lineSpacing = 0.25d;

	public NameTagMessage(BufferedImage image, int height, char imgChar) {
		super(image, height, imgChar);
		initialize(height);
	}

	public NameTagMessage(ChatColor[][] chatColors, char imgChar) {
		super(chatColors, imgChar);
		this.location = Preconditions.checkNotNull(location, "location cannot be NULL");
		initialize(chatColors.length);
	}

	public NameTagMessage(String... imgLines) {
		super(imgLines);
		initialize(imgLines.length);
	}

	private void initialize(int height) {
		this.spawner = new NameTagSpawner(height);
	}

	@Override
	public NameTagMessage appendCenteredText(String... text) {
		super.appendCenteredText(text);
		return this;
	}

	@Override
	public NameTagMessage appendText(String... text) {
		super.appendText(text);
		return this;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Location getLocation() {
		return location;
	}

	/**
	 * Retrieve the default amount of meters in the y-axis between each name tag.
	 * @return The line spacing.
	 */
	public double getLineSpacing() {
		return lineSpacing;
	}

	/**
	 * Set the default amount of meters in the y-axis between each name tag.
	 * @param lineSpacing - the name spacing.
	 */
	public void setLineSpacing(double lineSpacing) {
		this.lineSpacing = lineSpacing;
	}

	@Override
	public void sendToPlayer(Player player) {
		sendToPlayer(player, location != null ? location : player.getLocation());
	}

	/**
	 * Send a floating image message to the given player at the specified starting location.
	 * @param player - the player.
	 * @param location - the starting location.
	 */
	public void sendToPlayer(Player player, Location location) {
		for (int i = 0; i < lines.length; i++) {
			spawner.setNameTag(i, player, location, -i * lineSpacing, lines);
		}
	}
}