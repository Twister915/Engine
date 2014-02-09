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

package net.tbnr.gearz.modules;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Listener;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.util.bungee.command.TCommandHandler;
import net.tbnr.util.bungee.command.TCommandSender;
import net.tbnr.util.bungee.command.TCommandStatus;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 10/23/13
 * Time: 1:53 PM
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings("unused")
public class FriendsModule implements TCommandHandler, Listener {
    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GearzBungee.handleCommandStatus(status, sender);
    }
}
