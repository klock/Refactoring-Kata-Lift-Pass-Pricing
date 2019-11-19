/*
 *                  C O P Y R I G H T  (c) 2014
 *     A G F A   H E A L T H C A R E   C O R P O R A T I O N
 *                     All Rights Reserved
 *
 *
 *         THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF
 *                       AGFA CORPORATION
 *        The copyright notice above does not evidence any
 *       actual or intended publication of such source code.
 */
package dojo.liftpasspricing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class PricesRepository {

    public void insertBasePrice(final int price, final String type) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement stmt = prepareStatementForInsertion(type, price, connection)) {
            stmt.execute();
        }
    }

    private PreparedStatement prepareStatementForInsertion(final String type, final int price, final Connection connection) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO base_price (type, cost) VALUES (?, ?) " + //
                        "ON DUPLICATE KEY UPDATE cost = ?");
        preparedStatement.setString(1, type);
        preparedStatement.setInt(2, price);
        preparedStatement.setInt(3, price);
        return preparedStatement;
    }

    public int getPriceForType(final String type) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement costStmt = prepareStatementForPriceForType(type, connection);
             ResultSet result = costStmt.executeQuery()) {
            result.next();
            return result.getInt("cost");
        }
    }

    private PreparedStatement prepareStatementForPriceForType(final String type, final Connection connection) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT cost FROM base_price " + //
                        "WHERE type = ?");
        preparedStatement.setString(1, type);
        return preparedStatement;
    }

    public boolean isHoliday(final Date date) throws SQLException {
        boolean isHoliday = false;
        try (Connection connection = getConnection();
             PreparedStatement holidayStmt = prepareStatementForHoliday(connection);
             ResultSet holidays = holidayStmt.executeQuery()) {
            while (holidays.next()) {
                Date holiday = holidays.getDate("holiday");
                if (date != null && (date.getYear() == holiday.getYear() && //
                        date.getMonth() == holiday.getMonth() && //
                        date.getDate() == holiday.getDate())) {
                    isHoliday = true;
                }
            }
        }
        return isHoliday;
    }

    private PreparedStatement prepareStatementForHoliday(final Connection connection) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM holidays");
        return preparedStatement;
    }

    private Connection getConnection() throws SQLException {
        // TODO get those from a property file
        final String host = "localhost";
        final String port = "3306";
        final String schema = "lift_pass";
        final String user = "root";
        final String password = "mysql";
        return DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + schema, user, password);
    }
}