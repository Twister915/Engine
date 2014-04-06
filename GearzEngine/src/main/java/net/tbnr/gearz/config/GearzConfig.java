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

package net.tbnr.gearz.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class that can be used to create custom configuration files.
 * <p>
 * Latest Change: Added the class
 * <p>
 * @author Jake
 * @since 3/29/2014
 */
public class GearzConfig {
    private String fileName;
    private Plugin instance;
    private FileConfiguration customConfig = null;
    private File customConfigFile = null;

    public GearzConfig(Plugin plugin, String fileName) {
        this.fileName = fileName;
        this.instance = plugin;
    }

    public void reloadConfig() {
        if (customConfigFile == null) {
            customConfigFile = new File(instance.getDataFolder(), fileName);
        }
        customConfig = YamlConfiguration.loadConfiguration(customConfigFile);

        InputStream defConfigStream = instance.getResource(fileName);
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            customConfig.setDefaults(defConfig);
        }
    }

    public FileConfiguration getConfig() {
        if (customConfig == null) {
            reloadConfig();
        }
        return customConfig;
    }

    public void saveConfig() {
        if (customConfig == null || customConfigFile == null) {
            return;
        }
        try {
            getConfig().save(customConfigFile);
        } catch (IOException ex) {
            instance.getLogger().severe("Unable to save config " + fileName + "!");
        }
    }

    public void saveDefaultConfig() {
        if (customConfigFile == null) {
            customConfigFile = new File(instance.getDataFolder(), fileName);
        }
        if (!customConfigFile.exists()) {
            instance.saveResource(fileName, false);
        }
    }
}
