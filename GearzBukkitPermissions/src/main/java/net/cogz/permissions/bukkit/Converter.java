package net.cogz.permissions.bukkit;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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

    static Map<Integer, String> rankMap = new HashMap<>();
    static Map<String, String> playerMap = new HashMap<>();

    public static void newConverter() throws Exception {
        for (int x = 0; x < 20; x++) {
            System.out.println("WTFUCK");
        }
        username = "root";
        password = "5D3ecgJZ";
        mysqlDb = "tbnr2";
        port = 3306;
        host = "one.tbnr.pw";
        System.out.println(host + " swag");
        enable();
    }

    public static void doStuff() throws SQLException {
        PermissionsManager permsManager = GearzBukkitPermissions.getInstance().getPermsManager();
        Connection connection = connectionPool.getConnection();
        PreparedStatement groupSelect = connection.prepareStatement("SELECT * FROM entities WHERE is_group='1'");
        ResultSet groupResult = groupSelect.executeQuery();
        System.out.println("Size:" + groupResult.getFetchSize());
        while (groupResult.next()) {
            System.out.println("Found group: " + groupResult.getString("display_name"));
            rankMap.put(groupResult.getInt("id"), groupResult.getString("display_name"));
            permsManager.createGroup(groupResult.getString("display_name"), false);
        }

        PreparedStatement entitySelect = connection.prepareStatement("SELECT * FROM entities WHERE is_group='0'");
        ResultSet entityResult = entitySelect.executeQuery();
        while (entityResult.next()) {
            String caseName = entityResult.getString("name");
            String displayName = entityResult.getString("display_name");
            System.out.println("Found player with lower case name " + caseName + " with the real name, " + displayName);
            playerMap.put(caseName, displayName);
        }

        PreparedStatement playerSelect = connection.prepareStatement("SELECT * FROM memberships");
        ResultSet playerResult = playerSelect.executeQuery();
        while (playerResult.next()) {
            Integer groupId = playerResult.getInt("group_id");
            String realName = playerMap.get(playerResult.getString("member"));
            System.out.println("Adding player " + realName + " to the group " + rankMap.get(groupId));
            permsManager.setGroup(realName, rankMap.get(groupId));
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
