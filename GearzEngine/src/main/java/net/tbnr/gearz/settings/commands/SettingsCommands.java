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

package net.tbnr.gearz.settings.commands;

import net.gearz.settings.SettingsManager;
import net.gearz.settings.base.BaseSetting;
import net.gearz.settings.type.Toggleable;
import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.settings.PlayerSettings;
import net.tbnr.util.command.TCommand;
import net.tbnr.util.command.TCommandHandler;
import net.tbnr.util.command.TCommandSender;
import net.tbnr.util.command.TCommandStatus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Commands to manage player settings
 */
public class SettingsCommands implements TCommandHandler {

    @TCommand(
            name = "toggle",
            usage = "/toggle <setting>",
            permission = "gearz.settings.toggle",
            senders = {TCommandSender.Player})
    @SuppressWarnings("unused")
    public TCommandStatus toggle(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        if (args.length < 1) {
            return TCommandStatus.FEW_ARGS;
        }

        Player player = (Player) sender;
        BaseSetting setting = PlayerSettings.getRegistry().getSetting(args[0]);
        if (setting != null) {
            if (setting.getType() instanceof Toggleable) {
                SettingsManager manager = PlayerSettings.getManager(player);
                Object value = ((Toggleable) setting.getType()).getNextState(manager.getValue(setting));
                manager.setValue(setting, value);
                sender.sendMessage(Gearz.getInstance().getFormat("formats.settings-value", false, new String[]{"<value>", value + ""}, new String[]{"<setting>", setting.getName()}));

            } else {
                sender.sendMessage(Gearz.getInstance().getFormat("formats.setting-not-toggleable"));
            }
        } else {
            sender.sendMessage(Gearz.getInstance().getFormat("formats.setting-not-found"));
        }

        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            name = "set",
            usage = "/set <setting> <value>",
            permission = "gearz.settings.set",
            senders = {TCommandSender.Player})
    @SuppressWarnings("unused")
    public TCommandStatus set(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        if (args.length < 1) {
            return TCommandStatus.FEW_ARGS;
        }

        Player player = (Player) sender;
        BaseSetting setting = PlayerSettings.getRegistry().getSetting(args[0]);
        if (setting != null) {
            String raw = args[1];
            Object value;
            try {
                value = setting.getType().parse(raw);
            } catch (IllegalArgumentException e) {
                sender.sendMessage(Gearz.getInstance().getFormat("formats.setting-bad-parse"));
                return TCommandStatus.SUCCESSFUL;
            }
            SettingsManager manager = PlayerSettings.getManager(player);
            manager.setValue(setting, value);
            sender.sendMessage(Gearz.getInstance().getFormat("formats.settings-value", false, new String[]{"<value>", value + ""}, new String[]{"<setting>", setting.getName()}));
        } else {
            sender.sendMessage(Gearz.getInstance().getFormat("formats.setting-not-found"));
        }

        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            name = "get",
            usage = "/get <setting>",
            permission = "gearz.settings.get",
            senders = {TCommandSender.Player})
    @SuppressWarnings("unused")
    public TCommandStatus get(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        if (args.length < 1) {
            return TCommandStatus.FEW_ARGS;
        }

        Player player = (Player) sender;
        BaseSetting setting = PlayerSettings.getRegistry().getSetting(args[0]);
        if (setting != null) {
            SettingsManager manager = PlayerSettings.getManager(player);
            Object value = manager.getValue(setting);
            sender.sendMessage(Gearz.getInstance().getFormat("formats.settings-value", false, new String[]{"<value>", value + ""}, new String[]{"<setting>", setting.getName()}));
        } else {
            sender.sendMessage(Gearz.getInstance().getFormat("formats.setting-not-found"));
        }

        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            name = "settings",
            usage = "/settings",
            permission = "gearz.settings.settings",
            senders = {TCommandSender.Player, TCommandSender.Console})
    @SuppressWarnings("unused")
    public TCommandStatus settings(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        sender.sendMessage(Gearz.getInstance().getFormat("formats.settings-header"));
        for (BaseSetting setting : PlayerSettings.getRegistry().getSettings()) {
            sender.sendMessage(getSettingFormat(setting));
        }

        return TCommandStatus.SUCCESSFUL;
    }

    /**
     * Gets a display format for the setting list
     * @param setting setting to format
     * @return formatted setting
     */
    private String getSettingFormat(BaseSetting setting) {
        return Gearz.getInstance().getFormat("formats.settings-list", false, new String[]{"<setting>", setting.getName()}, new String[]{"<description>", setting.getDescription()});
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        Gearz.getInstance().handleCommandStatus(status, sender, senderType);
    }
}
