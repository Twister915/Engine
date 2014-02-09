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

package net.cogz.perms;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Jake on 2/2/14.
 */
public class Converter {

    public static void main(String[] args) {
        enable();
    }

    static String username;
    static String password;
    static String mysqlDb;
    static Integer port;
    static String host;
    static private BoneCP connectionPool;

    public static void newMySql() throws Exception {
        username = "root";
        password = "5D3ecgJZ";
        mysqlDb = "tbnr2";
        port = 3306;
        host = "127.0.0.1";
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
        try {
            newMySql();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
