package dojo.liftpasspricing.infrastructure;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;

class SQLPriceRepositoryTest {

    @Test
    void getPriceForNight() throws SQLException {
        final String type = "night";

        final int priceResult = queryRepository(type);

        assertEquals(19, priceResult);
    }

    @Test
    void getPriceFor1Jour() throws SQLException {
        final String type = "1jour";

        final int priceResult = queryRepository(type);

        assertEquals(35, priceResult);
    }

    @Test
    void getPriceForWhatever() throws SQLException {
        final String type = "Whatever";

        assertThrows(SQLException.class, () -> queryRepository(type));
    }

    @Test
    void insertAndQueryPriceAndUpdateAndQueryAgain() throws SQLException {
        final String type = "plop";
        final int firstPrice = 11;
        final int secondPrice = 15;

        insertInRepository(firstPrice, type);
       int priceResult = queryRepository(type);

        assertEquals(firstPrice, priceResult);

        insertInRepository(secondPrice, type);
        priceResult = queryRepository(type);

        assertEquals(secondPrice, priceResult);
    }

    private int queryRepository(final String type) throws SQLException {
        final SQLPriceRepository repository = new SQLPriceRepository(DbUtil.createConnection());
        return repository.getPriceForType(type);
    }


    private void insertInRepository(final int price, final String type) throws SQLException {
        final SQLPriceRepository repository = new SQLPriceRepository(DbUtil.createConnection());
        repository.insertBasePrice(price, type);
    }
}
