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

package net.cogz.punishments.bungee;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.tbnr.util.TPluginBungee;
import net.tbnr.util.io.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Bungee Punishments Plugin
 */
public class GearzBungeePunishments extends TPluginBungee {
    @Getter public static GearzBungeePunishments instance;
    @Getter private Properties properties;

    @Override
    protected void start() {
        GearzBungeePunishments.instance = this;
        if (!new File(this.getDataFolder() + File.separator + "strings.properties").exists()) {
            saveProperties();
        }
        this.properties = new Properties();
        reloadProperties();
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

    public void saveProperties() {
        FileUtil.writeEmbeddedResourceToLocalFile("strings.properties", new File(this.getDataFolder() + File.separator + "strings.properties"), this.getClass());
    }

    public void reloadProperties() {
        try {
            this.properties.load(new FileInputStream(this.getDataFolder() + File.separator + "strings.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFormat(String key, boolean prefix, boolean color, String[]... datas) {
        if (this.properties.getProperty(key) == null) {
            return key;
        }
        String property = this.properties.getProperty(key);
        if (prefix)
            property = ChatColor.translateAlternateColorCodes('&', this.properties.getProperty("prefix")) + property;
        property = ChatColor.translateAlternateColorCodes('&', property);
        if (datas == null) return property;
        for (String[] data : datas) {
            if (data.length != 2) continue;
            property = property.replaceAll(data[0], data[1]);
        }
        if (color) property = ChatColor.translateAlternateColorCodes('&', property);
        return property;
    }

    public String getFormat(String key, boolean prefix, boolean color) {
        return getFormat(key, prefix, color, new String[]{});
    }

    public String getFormat(String key, String[]... data) {
        return getFormat(key, false, false, data);
    }

    public String getFormat(String key, boolean prefix) {
        return getFormat(key, prefix, true);
    }

    public String getFormat(String key) {
        return getFormat(key, true);
    }
}
