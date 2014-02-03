package net.cogz.permissions.bukkit;

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
    String username;
    String password;
    String mysqlDb;
    Integer port;
    String host;
    BoneCP connectionPool;

    public Converter() throws Exception {
        System.out.println("THIS MAKE NO FUCKING SENSE");
        username = "root";
        password = "5D3ecgJZ";
        mysqlDb = "tbnr2";
        port = 3306;
        host = "127.0.0.1";
        System.out.println(host);
        enable();
        doStuff();
    }

    public void doStuff() throws SQLException {
        Connection connection = connectionPool.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM entities WHERE is_group='1'");
        ResultSet resultSet = stmt.executeQuery();
        while (resultSet.next()) {
            System.out.println("Name: " + resultSet.getString("display_name") + "Id: " + resultSet.getInt("id"));
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
