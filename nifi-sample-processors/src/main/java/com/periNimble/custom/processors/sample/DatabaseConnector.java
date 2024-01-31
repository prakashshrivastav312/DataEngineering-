package com.periNimble.custom.processors.sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {

    public static Connection connect() {
        try {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://ibba-dev-1.cb5hwvdvchhy.ap-south-1.rds.amazonaws.com/ibba-dev"; // Replace with your database URL
            String user = "ibba_write_user";
            String password = "aqWXNOxdUs4gfdwaq";
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
