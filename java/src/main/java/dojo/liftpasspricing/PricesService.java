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
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

public class PricesService {

    static int computeCost(final Connection connection, final String type, final Integer age, final Date date) throws SQLException {

        final int basePrice = new PricesRepository().getPriceForType(connection, type);

        if (age != null && age < 6) {
            return 0;
        } else {
            int reduction = 0;

            if (!type.equals("night")) {

                boolean isHoliday = new PricesRepository().isHoliday(connection, date);

                if (date != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    if (!isHoliday && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                        reduction = 35;
                    }
                }

                // TODO apply reduction for others
                if (age != null && age < 15) {
                    return (int) Math.ceil(basePrice * .7);
                } else {
                    if (age == null) {
                        double cost = basePrice * (1 - reduction / 100.0);
                        return (int) Math.ceil(cost);
                    } else {
                        if (age > 64) {
                            double cost = basePrice * .75 * (1 - reduction / 100.0);
                            return (int) Math.ceil(cost);
                        } else {
                            double cost = basePrice * (1 - reduction / 100.0);
                            return (int) Math.ceil(cost);
                        }
                    }
                }
            } else {
                if (age != null && age >= 6) {
                    if (age > 64) {
                        return (int) Math.ceil(basePrice * .4);
                    } else {
                        return (int) Math.ceil(basePrice);
                    }
                } else {
                    return 0;
                }
            }
        }
    }

    private static double applyReduction(int basePrice, int reduction) {
        return basePrice * (1 - reduction / 100.0);
    }

    private static int adjustPrice(final double cost) {
        return (int) Math.ceil(cost);
    }

    public static void insertBasePrice(final Connection connection, final int price, final String type) throws SQLException {
        new PricesRepository().insertBasePrice(connection, price, type);
    }
}
