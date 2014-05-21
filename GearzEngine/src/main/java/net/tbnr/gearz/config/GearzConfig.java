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

package net.tbnr.gearz.config;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
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
@RequiredArgsConstructor
public class GearzConfig {
    private final Plugin instance;
    private final String fileName;
    private FileConfiguration customConfig = null;
    private File customConfigFile = null;

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

    /**
     * Get a String format from the config.
     *
     * @param formatPath Supplied configuration path.
     * @param color      Include colors in the passed args?
     * @param data       The data arrays. Used to insert variables into the config string. Associates Key to Value.
     * @return The formatted String
     */
    public final String getFormat(String formatPath, boolean color, String[]... data) {
        //Added default value
        String string = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString(formatPath, ""));
        if (data != null) {
            for (String[] dataPart : data) {
                if (dataPart.length < 2) continue;
                string = string.replaceAll(dataPart[0], dataPart[1]);
            }
        }
        if (color) {
            string = ChatColor.translateAlternateColorCodes('&', string);
        }
        return string;
    }

    /**
     * Get the format without using any data.
     *
     * @param formatPath The path to the format!
     * @return The formatted message.
     */
    public final String getFormat(String formatPath) {
        return this.getFormat(formatPath, true);
    }
}
