package net.cogz.permissions.bukkit;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import net.cogz.permissions.PermGroup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Jake on 2/2/14.
 */
public class Converter {
    static String username;
    static String password;
    static String mysqlDb;
    static Integer port;
    static String host;
    static private BoneCP connectionPool;

    public static void newConverter() {
        for (int x = 0; x < 20; x++) {
            System.out.println("WTFUCK");
        }
        username = "root";
        password = "5D3ecgJZ";
        mysqlDb = "tbnr2";
        port = 3306;
        host = "127.0.0.1";
        System.out.println(host);
        enable();
    }

    public static void doStuff() throws SQLException {
        PermissionsManager permsManager = GearzBukkitPermissions.getInstance().getPermsManager();
        Connection connection = connectionPool.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM entities WHERE is_group='1'");
        ResultSet resultSet = stmt.executeQuery();
        System.out.println("Size:" + resultSet.getFetchSize());
        while (resultSet.next()) {
            System.out.println("Name: " + resultSet.getString("display_name") + " Id: " + resultSet.getInt("id"));
            permsManager.createGroup(resultSet.getString("display_name"));
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
}
