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

import java.text.ParseException;

import dojo.liftpasspricing.domain.HolidayRepository;
import dojo.liftpasspricing.domain.PriceRepository;
import dojo.liftpasspricing.domain.Repositories;

public class MemoryRepositories implements Repositories {

    private final HolidayRepository holidayRepository;
    private final PriceRepository priceRepository;

    public MemoryRepositories() throws ParseException {
        this.holidayRepository = new MemoryHolidayRepository();
        this.priceRepository = new MemoryPriceRepository();
    }

    @Override
    public HolidayRepository getHolidayRepository() {
        return this.holidayRepository;
    }

    @Override
    public PriceRepository getPriceRepository() {
        return this.priceRepository;
    }
}
