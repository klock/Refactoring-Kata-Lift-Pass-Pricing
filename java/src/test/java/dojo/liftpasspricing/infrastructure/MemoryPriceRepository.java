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

import java.sql.SQLException;

import dojo.liftpasspricing.domain.PriceRepository;

public class MemoryPriceRepository implements PriceRepository {

    @Override
    public int getPriceForType(final String type) throws SQLException {
        if ("1jour".equals(type)) {
            return 35;
        } else if ("night".equals(type)) {
            return 19;
        }
        return 0;
    }

    @Override
    public void insertBasePrice(final int price, final String type) throws SQLException {
        // TODO MKL
    }
}
