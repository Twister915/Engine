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
    static Set<SelectData> fullMap = new HashSet<>();

    public static SelectData getByEntityId(Integer id) {
        for (SelectData selectData : fullMap) {
            if (selectData.getId().equals(id)) return selectData;
        }
        return null;
    }

    public static void newConverter() throws Exception {
        username = "root";
        password = "5D3ecgJZ";
        mysqlDb = "tbnr2";
        port = 3306;
        host = "one.tbnr.pw";
        enable();
    }

    public static class SelectData {
        @Getter Integer id;
        @Getter String name;
        @Getter boolean group;

        public SelectData(Integer id, String name, Integer group) {
            this.id = id;
            this.name = name;
            this.group = group == 1;
        }
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

        PreparedStatement playerSelect2 = connection.prepareStatement("SELECT * FROM entities");
        ResultSet playerResult2 = playerSelect2.executeQuery();
        while (playerResult2.next()) {
            fullMap.add(new SelectData(playerResult2.getInt("id"), playerResult2.getString("name"), playerResult2.getInt("is_group")));
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
            SelectData selectData = getByEntityId(entityId);
            if (selectData.isGroup()) {
                permsManager.givePermToGroup(permsManager.getGroup(selectData.getName()), permission, value);
            } else {
                permsManager.givePermToPlayer(selectData.getName(), permission, value);
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
