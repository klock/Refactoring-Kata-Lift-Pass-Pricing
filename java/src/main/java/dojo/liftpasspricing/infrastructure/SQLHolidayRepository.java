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
package dojo.liftpasspricing.infrastructure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class SQLHolidayRepository {
    public static final String HOLIDAY = "holiday";

    public boolean isHoliday(final Connection connection, final Date dateParam) throws SQLException {
        try (PreparedStatement holidayStmt = connection.prepareStatement(
                "SELECT * FROM holidays")) {
            try (ResultSet holidays = holidayStmt.executeQuery()) {

                while (holidays.next()) {
                    Date holiday = holidays.getDate(HOLIDAY);
                    if (dateParam != null && isSameDay(dateParam, holiday)) {
                        return true;
                    }
                }

            }
        }
        return false;
    }

    private boolean isSameDay(final Date dateParam, final Date holiday) {
        return dateParam.getYear() == holiday.getYear() &&
                dateParam.getMonth() == holiday.getMonth() &&
                dateParam.getDate() == holiday.getDate();
    }
}
