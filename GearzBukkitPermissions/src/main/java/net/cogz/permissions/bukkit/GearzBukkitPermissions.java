package net.cogz.permissions.bukkit;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Jake on 1/24/14.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public final class GearzBukkitPermissions extends JavaPlugin {
    @Getter private static GearzBukkitPermissions instance;
    @Getter public PermissionsManager permsManager;

    @SuppressWarnings("deprecation")
    @Override
    public void onEnable() {
        GearzBukkitPermissions.instance = this;
        this.permsManager = new PermissionsManager();
        saveConfig();
        getServer().getPluginManager().registerEvents(this.permsManager, this);
        PermissionsCommands permsCommands = new PermissionsCommands();
        getCommand("player").setExecutor(permsCommands);
        getCommand("group").setExecutor(permsCommands);
        getCommand("permissions").setExecutor(permsCommands);
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                try {
                    GearzBukkitPermissions.getInstance().getPermsManager().reload();
                } catch (Exception ex) {
                    GearzBukkitPermissions.getInstance().getLogger().severe(ex.getMessage());
                }
            }
        }, 0, 30 * 20);
        if (getConfig().getBoolean("converter", false)) {
            try {
                new Converter();
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    /**
     * Get the format without using any data.
     *
     * @param formatPath The path to the format!
     * @param color      Include colors in the passed args?
     * @return The formatted message.
     */
    public final String getFormat(String formatPath, boolean color) {
        return this.getFormat(formatPath, color, new String[]{});
    }

    public String compile(String[] args, int min, int max) {
        StringBuilder builder = new StringBuilder();

        for (int i = min; i < args.length; i++) {
            builder.append(args[i]);
            if (i == max) return builder.toString();
            builder.append(" ");
        }
        return builder.toString();
    }
}
