package dojo.liftpasspricing.infrastructure;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;

class SQLHolidayRepositoryTest {

    @Test
    void isHoliday_20190218() throws SQLException, ParseException {
        final String dateStr = "2019-02-18";
        final boolean isHoliday = callRepository(dateStr);
        assertTrue(isHoliday);
    }
    @Test
    void isHoliday_20190225() throws SQLException, ParseException {
        final String dateStr = "2019-02-25";
        final boolean isHoliday = callRepository(dateStr);
        assertTrue(isHoliday);
    }

    @Test
    void isHoliday_20190304() throws SQLException, ParseException {
        final String dateStr = "2019-03-04";
        final boolean isHoliday = callRepository(dateStr);
        assertTrue(isHoliday);
    }

    @Test
    void isHoliday_20191212() throws SQLException, ParseException {
        final String dateStr = "2019-12-12";
        final boolean isHoliday = callRepository(dateStr);
        assertFalse(isHoliday);
    }

    private boolean callRepository(final String dateStr) throws SQLException, ParseException {
        final SQLHolidayRepository repository = new SQLHolidayRepository(DbUtil.createConnection());
        return repository.isHoliday(createDate(dateStr));
    }

    private Date createDate(String dateStr) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.parse(dateStr);
    }
}
