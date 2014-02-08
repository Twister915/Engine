package net.cogz.permissions.bukkit;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import lombok.Getter;
import org.bukkit.event.HandlerList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    static Map<Integer, String> playerMap = new HashMap<>();

    public static void newConverter() throws Exception {
        username = "root";
        password = "5D3ecgJZ";
        mysqlDb = "tbnr2";
        port = 3306;
        host = "one.tbnr.pw";
        enable();
    }

    public static void doStuff() throws SQLException {
        PermissionsManager permsManager = GearzBukkitPermissions.getInstance().getPermsManager();
        Connection connection = connectionPool.getConnection();
        PreparedStatement groupSelect = connection.prepareStatement("SELECT * FROM entities WHERE is_group='1'");
        ResultSet groupResult = groupSelect.executeQuery();
        while (groupResult.next()) {
            System.out.println("Found group: " + groupResult.getString("display_name"));
            rankMap.put(groupResult.getInt("id"), groupResult.getString("display_name"));
            permsManager.createGroup(groupResult.getString("display_name"), false);
        }

        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM entities WHERE is_group='0'");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            System.out.println("Found player: " + resultSet.getString("name"));
            playerMap.put(resultSet.getInt("id"), resultSet.getString("name"));
        }

        PreparedStatement playerSelect = connection.prepareStatement("SELECT * FROM memberships");
        ResultSet playerResult = playerSelect.executeQuery();
        while (playerResult.next()) {
            Integer groupId = playerResult.getInt("group_id");
            String name = playerResult.getString("member");
            System.out.println("Player " + name + " added to group " + rankMap.get(groupId));
            permsManager.setGroup(name, rankMap.get(groupId));
        }

        PreparedStatement permissionsSelect = connection.prepareStatement("SELECT * FROM entries");
        ResultSet permissionsResult = permissionsSelect.executeQuery();
        while (permissionsResult.next()) {
            Integer entityId = permissionsResult.getInt("entity_id");
            String permission = permissionsResult.getString("permission");
            boolean value = permissionsResult.getInt("value") == 1;
            System.out.println("Found entity " + entityId + " with permission " + permission + " and value " + value);
            if (rankMap.containsKey(entityId)) {
                System.out.print(" and is group!");
                permsManager.givePermToGroup(permsManager.getGroup(rankMap.get(entityId)), permission, value);
            } else if (playerMap.containsKey(entityId)) {
                System.out.print(" and is player!");
                permsManager.givePermToPlayer(playerMap.get(entityId), permission, value);
            }
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
