package net.cogz.permissions.bukkit;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    static String username;
    static String password;
    static String mysqlDb;
    static Integer port;
    static String host;
    static private BoneCP connectionPool;
    @SuppressWarnings("deprecation")
    @Override
    public void onEnable() {
        GearzBukkitPermissions.instance = this;
        this.permsManager = new PermissionsManager();
        try {
            newConverter();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    }

    public static void newConverter() throws Exception {
        username = "root";
        password = "5D3ecgJZ";
        mysqlDb = "tbnr2";
        port = 3306;
        host = "127.0.0.1";
        System.out.println(host);
        enable();
        doStuff();
    }

    public static void doStuff() throws SQLException {
        Connection connection = connectionPool.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM entities WHERE is_group='1'");
        ResultSet resultSet = stmt.executeQuery();
        while (resultSet.next()) {
            System.out.println("Name: " + resultSet.getString("display_name") + "Id: " + resultSet.getInt("id"));
        }
    }

    public static void enable() {
        BoneCPConfig config = new BoneCPConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + mysqlDb);
        config.setUser(username);
        config.setPassword(password);
        config.setMinConnectionsPerPartition(5);
        config.setMaxConnectionsPerPartition(20);
        config.setPartitionCount(1);
        try {
            connectionPool = new BoneCP(config);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            doStuff();
        } catch (SQLException e) {
            e.printStackTrace();
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
        if (!this.getConfig().contains(formatPath)) {
            return formatPath;
        }

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
