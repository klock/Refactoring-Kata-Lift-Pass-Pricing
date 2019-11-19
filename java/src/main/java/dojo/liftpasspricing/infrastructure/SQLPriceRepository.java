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

import dojo.liftpasspricing.domain.PriceRepository;

public class SQLPriceRepository implements PriceRepository {

    public int getPriceForType(final String type) throws SQLException {
        try (Connection connection = SQLRepositories.getConnection();
             PreparedStatement costStmt = prepareStatementForPriceForType(type, connection);
             ResultSet result = costStmt.executeQuery()) {
            result.next();
            return result.getInt("cost");
        }
    }

    private PreparedStatement prepareStatementForPriceForType(final String type, final Connection connection) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT cost FROM base_price " + //
                        "WHERE type = ?");
        preparedStatement.setString(1, type);
        return preparedStatement;
    }

    public void insertBasePrice(final int price, final String type) throws SQLException {
        try (Connection connection = SQLRepositories.getConnection();
             PreparedStatement stmt = this.prepareStatementForInsertion(type, price, connection)) {
            stmt.execute();
        }
    }

    private PreparedStatement prepareStatementForInsertion(final String type, final int price, final Connection connection) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO base_price (type, cost) VALUES (?, ?) " + //
                        "ON DUPLICATE KEY UPDATE cost = ?");
        preparedStatement.setString(1, type);
        preparedStatement.setInt(2, price);
        preparedStatement.setInt(3, price);
        return preparedStatement;
    }
}
