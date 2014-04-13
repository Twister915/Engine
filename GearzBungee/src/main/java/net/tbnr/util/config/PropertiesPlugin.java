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

package net.tbnr.util.config;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import net.tbnr.util.FileUtil;
import net.tbnr.util.TPluginBungee;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * <p>
 * Latest Change:
 * <p>
 *
 * @author Jake
 * @since 4/12/2014
 */
public class PropertiesPlugin extends TPluginBungee {
    @Getter private Properties properties;
    protected Plugin plugin;
    @Getter protected String fileName = "strings.properties";

    @Override
    protected void start() {
        if (!new File(this.plugin.getDataFolder() + File.separator + getFileName()).exists()) {
            saveProperties();
        }
        this.properties = new Properties();
        reloadProperties();
    }

    @Override
    protected void stop() {}

    public void saveProperties() {
        FileUtil.writeEmbeddedResourceToLocalFile(getFileName(), new File(plugin.getDataFolder() + File.separator + getFileName()), plugin.getClass());
    }

    public void reloadProperties() {
        try {
            this.properties.load(new FileInputStream(plugin.getDataFolder() + File.separator + getFileName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void resetProperties() {
        saveProperties();
        reloadProperties();
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
        return getFormat(key, prefix, color, null);
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
