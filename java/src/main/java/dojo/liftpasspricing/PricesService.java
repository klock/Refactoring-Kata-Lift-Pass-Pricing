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

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import dojo.liftpasspricing.domain.Cost;
import dojo.liftpasspricing.domain.Repositories;
import dojo.liftpasspricing.infrastructure.SQLRepositories;

public class PricesService {

    private static final String TYPE_NIGHT = "night";

    private Repositories repositories;

    public PricesService() {
        repositories = new SQLRepositories();
    }

    public PricesService(Repositories repositories) {
        this.repositories = repositories;
    }

    public Cost computeCost(final String type, final Integer age, final Date date) throws SQLException {

        final int basePrice = repositories.getPriceRepository().getPriceForType(type);

        double coefficient = computePriceCoefficient(age, date, TYPE_NIGHT.equals(type));

        return new Cost(adjustPrice(applyCoefficient(basePrice, coefficient)));
    }

    private double computePriceCoefficient(final Integer age, final Date date, final boolean isTypeNight) throws SQLException {
        double coefficient = 1.0;

        boolean isHoliday = repositories.getHolidayRepository().isHoliday(date);

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

    public void insertBasePrice(final int price, final String type) throws SQLException {
        repositories.getPriceRepository().insertBasePrice(price, type);
    }
}
