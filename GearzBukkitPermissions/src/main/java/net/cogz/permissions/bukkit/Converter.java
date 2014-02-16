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

package net.cogz.permissions.bukkit;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import net.tbnr.util.GUtility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jake on 2/2/14.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public class Converter implements GUtility {
    String username;
    String password;
    String mysqlDb;
    Integer port;
    String host;
    private BoneCP connectionPool;

    Map<Integer, String> rankMap = new HashMap<>();
    Map<Integer, String> playerMap = new HashMap<>();

    public Converter() throws Exception {
        username = "root";
        password = "5D3ecgJZ";
        mysqlDb = "tbnr2";
        port = 3306;
        host = "one.tbnr.pw";
        enable();
    }

    public void doStuff() throws SQLException {
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
                System.out.println(rankMap.get(entityId));
                permsManager.givePermToGroup(rankMap.get(entityId), permission, value);
            } else if (playerMap.containsKey(entityId)) {
                System.out.print(" and is player!");
                permsManager.givePermToPlayer(playerMap.get(entityId), permission, value);
            }
        }

        PreparedStatement inheritSelect = connection.prepareStatement("SELECT * FROM inheritances");
        ResultSet inheritResult = inheritSelect.executeQuery();
        while (inheritResult.next()) {
            Integer parentId = inheritResult.getInt("parent_id");
            Integer childId = inheritResult.getInt("child_id");
            System.out.println("Added inheritance parent: " + parentId + " child: " + childId);
            permsManager.addInheritance(permsManager.getGroup(rankMap.get(childId)), permsManager.getGroup(rankMap.get(parentId)));
        }
    }

    public void enable() {
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
