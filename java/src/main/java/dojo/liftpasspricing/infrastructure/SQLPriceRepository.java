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
import java.util.concurrent.atomic.AtomicInteger;

public class SQLPriceRepository {

    public int getCostForType(final Connection connection, final String type) throws SQLException {
        final AtomicInteger atomicInteger = new AtomicInteger();
        try (PreparedStatement costStmt = connection.prepareStatement( //
                "SELECT cost FROM base_price " + //
                        "WHERE type = ?")) {
            costStmt.setString(1, type);
            try (ResultSet result = costStmt.executeQuery()) {
                result.next();
                atomicInteger.set(result.getInt("cost"));
            }
        }
        return atomicInteger.get();
    }
}
