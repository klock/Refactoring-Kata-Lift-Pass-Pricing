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

import dojo.liftpasspricing.Repositories;

public class SQLPriceRepository {

    private Connection connection;

    public SQLPriceRepository(final Connection connection) {
        this.connection = connection;
    }

    public void persist(final int liftPassCost, final String liftPassType) {
        try (PreparedStatement stmt = this.connection.prepareStatement( //
                "INSERT INTO base_price (type, cost) VALUES (?, ?) " + //
                        "ON DUPLICATE KEY UPDATE cost = ?")) {
            stmt.setString(1, liftPassType);
            stmt.setInt(2, liftPassCost);
            stmt.setInt(3, liftPassCost);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getCostForType(final String type) {
        try {
            final AtomicInteger atomicInteger = new AtomicInteger();
            try (PreparedStatement costStmt = this.connection.prepareStatement( //
                    "SELECT cost FROM base_price " + //
                            "WHERE type = ?")) {
                costStmt.setString(1, type);
                try (ResultSet result = costStmt.executeQuery()) {
                    result.next();
                    atomicInteger.set(result.getInt("cost"));
                }
            }
            return atomicInteger.get();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // TODO
        }
    }
}
