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

    private static final String TYPE_NIGHT = "night";

    static int computeCost(final Connection connection, final String type, final Integer age, final Date date) throws SQLException {
        final PricesRepository pricesRepository = new PricesRepository(connection);

        final int basePrice = pricesRepository.getPriceForType(type);
        boolean isHoliday = pricesRepository.isHoliday(date);

        double coefficient = computePriceCoefficient(age, date, isHoliday, TYPE_NIGHT.equals(type));

        return adjustPrice(applyCoefficient(basePrice, coefficient));
    }

    private static double computePriceCoefficient(final Integer age, final Date date, final boolean isHoliday, final boolean isTypeNight) {
        double coefficient = 1.0;

        if (age != null && age < 6) {
            coefficient = 0.0;
        } else {
            if (!isTypeNight) {

                if (date != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    if (!isHoliday && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                        coefficient = 0.65;
                    }
                }

                // TODO apply reduction for others
                if (age != null && age < 15) {
                    coefficient = .7;
                } else {
                    if (age != null && age > 64) {
                        coefficient = .75 * coefficient;
                    }
                }
            } else {
                if (age != null && age >= 6) {
                    if (age > 64) {
                        coefficient = .4;
                    } else {
                        coefficient = 1;
                    }
                } else {
                    coefficient = 0;
                }
            }
        }
        return coefficient;
    }

    private static double applyCoefficient(double basePrice, double coefficient) {
        return basePrice * coefficient;
    }

    private static int adjustPrice(final double cost) {
        return (int) Math.ceil(cost);
    }

    public static void insertBasePrice(final Connection connection, final int price, final String type) throws SQLException {
        new PricesRepository(connection).insertBasePrice(price, type);
    }
}
