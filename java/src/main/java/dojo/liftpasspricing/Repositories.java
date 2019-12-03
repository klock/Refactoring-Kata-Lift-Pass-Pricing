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
import java.sql.SQLException;

import dojo.liftpasspricing.infrastructure.SQLHolidayRepository;
import dojo.liftpasspricing.infrastructure.SQLPriceRepository;

public class Repositories {

    public Connection connection;

    public Repositories(Connection connection) {
        this.connection = connection;
    }

    public Repositories() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lift_pass", "root", "mysql");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static SQLPriceRepository price() {
        return SingletonHolder.INSTANCE.getPrice();
    }

    public static SQLHolidayRepository holiday() {
        return SingletonHolder.INSTANCE.getHoliday();
    }

    public static void close() {
        try {
            SingletonHolder.INSTANCE.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private SQLHolidayRepository getHoliday() {
        return new SQLHolidayRepository(connection);
    }

    private SQLPriceRepository getPrice() {
        return new SQLPriceRepository(connection);
    }

    public static class SingletonHolder {
        public static Repositories INSTANCE = new Repositories();
    }
}
