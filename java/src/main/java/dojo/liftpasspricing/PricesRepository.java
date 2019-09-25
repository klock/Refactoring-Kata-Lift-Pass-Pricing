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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class PricesRepository {

    public void insertBasePrice(final Connection connection, final int price, final String type) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement( //
                "INSERT INTO base_price (type, cost) VALUES (?, ?) " + //
                        "ON DUPLICATE KEY UPDATE cost = ?")) {
            stmt.setString(1, type);
            stmt.setInt(2, price);
            stmt.setInt(3, price);
            stmt.execute();
        }
    }

    public int getPriceForType(final Connection connection, final String type) throws SQLException {
        try (PreparedStatement costStmt = connection.prepareStatement( //
                "SELECT cost FROM base_price " + //
                        "WHERE type = ?")) {
            costStmt.setString(1, type);
            try (ResultSet result = costStmt.executeQuery()) {
                result.next();
                return result.getInt("cost");
            }
        }
    }

    public boolean isHoliday(final Connection connection, final Date date) throws SQLException {
        boolean isHoliday = false;
        try (PreparedStatement holidayStmt = connection.prepareStatement( //
                "SELECT * FROM holidays")) {
            try (ResultSet holidays = holidayStmt.executeQuery()) {

                while (holidays.next()) {
                    Date holiday = holidays.getDate("holiday");
                    if (date != null && (date.getYear() == holiday.getYear() && //
                            date.getMonth() == holiday.getMonth() && //
                            date.getDate() == holiday.getDate())) {
                        isHoliday = true;
                    }
                }

            }
        }
        return isHoliday;
    }

}
