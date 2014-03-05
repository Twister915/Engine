package net.cogz.punishments.bungee;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.tbnr.util.FileUtil;
import net.tbnr.util.TPluginBungee;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Created by jake on 3/5/14.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public class GearzBungeePunishments extends TPluginBungee {
    @Getter private Properties strings;
    @Getter public static GearzBungeePunishments instance;
    PunishmentManager punishmentManager;

    @Override
    protected void start() {
        GearzBungeePunishments.instance = this;
        this.punishmentManager = new PunishmentManager();
        punishmentManager.database = punishmentManager.getDB();
        registerCommandHandler(new UnPunishCommands(punishmentManager));
        registerCommandHandler(new PunishmentCommands(punishmentManager));
        registerEvents(new PunishmentManager());
        if (!new File(getDataFolder() + File.separator + "strings.properties").exists()) saveStrings();
        this.strings = new Properties();
        reloadStrings();
    }

    @Override
    protected void stop() {

    }

    public void reloadStrings() {
        try {
            this.strings.load(new FileInputStream(getDataFolder() + File.separator + "strings.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveStrings() {
        FileUtil.writeEmbeddedResourceToLocalFile("strings.properties", new File(getDataFolder() + File.separator + "strings.properties"));
    }

    public void resetStrings() {
        saveStrings();
        reloadStrings();
    }

    public String getFormat(String key, boolean prefix, boolean color, String[]... datas) {
        if (this.strings.getProperty(key) == null) {
            return key;
        }
        String property = this.strings.getProperty(key);
        if (prefix)
            property = ChatColor.translateAlternateColorCodes('&', this.strings.getProperty("prefix")) + property;
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
