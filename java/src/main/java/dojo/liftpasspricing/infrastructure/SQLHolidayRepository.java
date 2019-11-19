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

import dojo.liftpasspricing.domain.HolidayRepository;

public class SQLHolidayRepository implements HolidayRepository {

    public boolean isHoliday(final Date date) throws SQLException {
        try (Connection connection = SQLRepositories.getConnection();
             PreparedStatement holidayStmt = prepareStatementForHoliday(connection);
             ResultSet holidays = holidayStmt.executeQuery()) {
            while (holidays.next()) {
                Date holiday = holidays.getDate("holiday");
                if (isSameDay(date, holiday)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isSameDay(final Date date, final Date holiday) {
        return date != null
                && (date.getYear() == holiday.getYear()
                && date.getMonth() == holiday.getMonth()
                && date.getDate() == holiday.getDate());
    }

    private PreparedStatement prepareStatementForHoliday(final Connection connection) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM holidays");
        return preparedStatement;
    }
}
