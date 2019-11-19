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
import java.sql.SQLException;

import dojo.liftpasspricing.domain.Repositories;

public class SQLRepositories implements Repositories {

    private SQLPriceRepository sqlPriceRepository;
    private SQLHolidayRepository sqlHolidayRepository;

    public SQLRepositories() throws SQLException {
        Connection connection = DbUtil.createConnection();
        this.sqlPriceRepository = new SQLPriceRepository(connection);
        this.sqlHolidayRepository = new SQLHolidayRepository(connection);
    }

    @Override
    public SQLPriceRepository getPriceRepository() {
        return sqlPriceRepository;
    }

    @Override
    public SQLHolidayRepository getHolidayRepository() {
        return sqlHolidayRepository;
    }

}
