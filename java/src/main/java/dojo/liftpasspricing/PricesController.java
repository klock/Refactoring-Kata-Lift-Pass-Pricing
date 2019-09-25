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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import spark.Request;

public class PricesController {

    public String insertBasePrice(final Connection connection, final Request req) throws SQLException {
        int liftPassCost = Integer.parseInt(req.queryParams("cost"));
        String liftPassType = req.queryParams("type");

        try (PreparedStatement stmt = connection.prepareStatement( //
                "INSERT INTO base_price (type, cost) VALUES (?, ?) " + //
                        "ON DUPLICATE KEY UPDATE cost = ?")) {
            stmt.setString(1, liftPassType);
            stmt.setInt(2, liftPassCost);
            stmt.setInt(3, liftPassCost);
            stmt.execute();
        }

        return "";
    }

    public String computeCost(final Connection connection, final Request req) throws SQLException, ParseException {
        final Integer age = req.queryParams("age") != null ? Integer.valueOf(req.queryParams("age")) : null;

        try (PreparedStatement costStmt = connection.prepareStatement( //
                "SELECT cost FROM base_price " + //
                "WHERE type = ?")) {
            costStmt.setString(1, req.queryParams("type"));
            try (ResultSet result = costStmt.executeQuery()) {
                result.next();

                int reduction;
                boolean isHoliday = false;

                if (age != null && age < 6) {
                    return "{ \"cost\": 0}";
                } else {
                    reduction = 0;

                    if (!req.queryParams("type").equals("night")) {
                        DateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd");

                        try (PreparedStatement holidayStmt = connection.prepareStatement( //
                                "SELECT * FROM holidays")) {
                            try (ResultSet holidays = holidayStmt.executeQuery()) {

                                while (holidays.next()) {
                                    Date holiday = holidays.getDate("holiday");
                                    if (req.queryParams("date") != null) {
                                        Date d = isoFormat.parse(req.queryParams("date"));
                                        if (d.getYear() == holiday.getYear() && //
                                            d.getMonth() == holiday.getMonth() && //
                                            d.getDate() == holiday.getDate()) {
                                            isHoliday = true;
                                        }
                                    }
                                }

                            }
                        }

                        if (req.queryParams("date") != null) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(isoFormat.parse(req.queryParams("date")));
                            if (!isHoliday && calendar.get(Calendar.DAY_OF_WEEK) == 2) {
                                reduction = 35;
                            }
                        }

                        // TODO apply reduction for others
                        if (age != null && age < 15) {
                            return "{ \"cost\": " + (int) Math.ceil(result.getInt("cost") * .7) + "}";
                        } else {
                            if (age == null) {
                                double cost = result.getInt("cost") * (1 - reduction / 100.0);
                                return "{ \"cost\": " + (int) Math.ceil(cost) + "}";
                            } else {
                                if (age > 64) {
                                    double cost = result.getInt("cost") * .75 * (1 - reduction / 100.0);
                                    return "{ \"cost\": " + (int) Math.ceil(cost) + "}";
                                } else {
                                    double cost = result.getInt("cost") * (1 - reduction / 100.0);
                                    return "{ \"cost\": " + (int) Math.ceil(cost) + "}";
                                }
                            }
                        }
                    } else {
                        if (age != null && age >= 6) {
                            if (age > 64) {
                                return "{ \"cost\": " + (int) Math.ceil(result.getInt("cost") * .4) + "}";
                            } else {
                                return "{ \"cost\": " + result.getInt("cost") + "}";
                            }
                        } else {
                            return "{ \"cost\": 0}";
                        }
                    }
                }
            }
        }
    }
}
