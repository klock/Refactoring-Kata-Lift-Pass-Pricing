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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import spark.Request;

public class PricesController {

    public static String insertBasePrice(final Connection connection, final Request req) throws SQLException {
        final String costQP = req.queryParams(PricesRoutes.QP.COST);
        int price = Integer.parseInt(costQP);
        String type = req.queryParams(PricesRoutes.QP.TYPE);

        PricesService.insertBasePrice(connection, price, type);

        return "";
    }

    public static int computeCost(final Connection connection, final Request req) throws SQLException, ParseException {
        final String ageQP = req.queryParams(PricesRoutes.QP.AGE);
        final String typeQP = req.queryParams(PricesRoutes.QP.TYPE);
        final String dateQP = req.queryParams(PricesRoutes.QP.DATE);

        final Integer age = ageQP != null ? Integer.valueOf(ageQP) : null;
        DateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd");
        final Date date = Objects.isNull(dateQP) ? null : isoFormat.parse(dateQP);

        return PricesService.computeCost(connection, typeQP, age, date);
    }

}
