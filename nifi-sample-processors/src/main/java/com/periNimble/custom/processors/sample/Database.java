package com.periNimble.custom.processors.sample;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Database {

    public static void main(String[] args) {
        String meterId = "MTR040222"; // Replace with the actual meter_id value you want to query

        try (Connection connection = DatabaseConnector.connect()) {
            if (connection != null) {
                String query = "SELECT daily_avg FROM dailyaveragekwh WHERE meter_id = ? ORDER BY avg_date DESC LIMIT 1";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    // Set the parameter for the meter_id
                    statement.setString(1, meterId);

                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            double dailyAvg = resultSet.getDouble("daily_avg");
                            System.out.println("Meter ID: " + meterId + ", Daily Average: " + dailyAvg);
                        } else {
                            System.out.println("No records found for meter ID: " + meterId);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
