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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import dojo.liftpasspricing.domain.HolidayRepository;

public class MemoryHolidayRepository implements HolidayRepository {

    private List<Date> persistedDates;

    public MemoryHolidayRepository() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.persistedDates = new ArrayList<>(Arrays.asList(
                simpleDateFormat.parse("2019-02-18"),
                simpleDateFormat.parse("2019-02-25"),
                simpleDateFormat.parse("2019-03-04")));
    }

    @Override
    public boolean isHoliday(final Date date) throws SQLException {
        if (Objects.isNull(date)) {
            return false;
        }
        return persistedDates.stream()
                .anyMatch(d -> d.compareTo(date) == 0);
    }
}
