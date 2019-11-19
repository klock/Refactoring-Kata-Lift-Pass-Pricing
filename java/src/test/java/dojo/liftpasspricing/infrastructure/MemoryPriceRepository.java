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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import dojo.liftpasspricing.domain.PriceRepository;

public class MemoryPriceRepository implements PriceRepository {

    private Map<String, Integer> persistedPrices;

    public MemoryPriceRepository() {
        this.persistedPrices = new HashMap<>();
        this.persistedPrices.put("1jour", 35);
        this.persistedPrices.put("night", 19);
    }

    @Override
    public int getPriceForType(final String type) throws SQLException {
        if (Objects.isNull(type) || !persistedPrices.containsKey(type)) {
            return 0;
        }
        return this.persistedPrices.get(type);
    }

    @Override
    public void insertBasePrice(final int price, final String type) throws SQLException {
        persistedPrices.put(type, price);
    }
}
