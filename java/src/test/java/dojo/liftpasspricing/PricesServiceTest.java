package dojo.liftpasspricing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import dojo.liftpasspricing.domain.Cost;
import dojo.liftpasspricing.infrastructure.MemoryRepositories;

class PricesServiceTest {

    private PricesService pricesService;

    @BeforeEach
    void setUp() throws ParseException {
        pricesService = new PricesService(new MemoryRepositories());
    }

    @Test
    void priceForType1jour() {
        final int cost = invokeComputeCost("1jour", null, null);

        assertEquals(35, cost);
    }

    @Test
    void priceForTypeNightAndAgeNull() {
        int cost = invokeComputeCost("night", null, null);

        assertEquals(0, cost);
    }

    @ParameterizedTest(name = "{index} Age is {0}")
    @ValueSource(ints = {0, 1, 2, 3, 4, 5})
    void priceForAgeBetween0And6(int age) {
        int cost = invokeComputeCost("1jour", age, null);

        assertEquals(0, cost);
    }

    @ParameterizedTest(name = "Age is {0}")
    @MethodSource("rangeFrom6To64")
    void priceForTypeNightAndAgeBetween6And63(int age) {
        int cost = invokeComputeCost("night", age, null);

        assertEquals(19, cost);
    }

    @ParameterizedTest(name = "Age is {0}")
    @MethodSource("rangeFrom65To100")
    void priceForTypeNightAndAgeGreaterThan5(int age) {
        int cost = invokeComputeCost("night", age, null);

        assertEquals(8, cost);
    }

    @ParameterizedTest(name = "Age is {0}")
    @MethodSource("rangeFrom6To14")
    void priceForTypeDifferentFromNightAndAgeLessThan15(int age) {
        int cost = invokeComputeCost("1jour", age, null);

        assertEquals(25, cost);
    }

    @ParameterizedTest(name = "Age is {0}")
    @MethodSource("rangeFrom15To64")
    void priceForTypeDifferentFromNightAndAgeBetween15And64(int age) {
        int cost = invokeComputeCost("1jour", age, null);

        assertEquals(35, cost);
    }

    @ParameterizedTest(name = "Age is {0}")
    @MethodSource("rangeFrom65To100")
    void priceForTypeDifferentFromNightAndAgeGreaterThan64(int age) {
        int cost = invokeComputeCost("1jour", age, null);

        assertEquals(27, cost);
    }

    @ParameterizedTest(name = "Age is {0}")
    @MethodSource("rangeFrom6To14")
    void priceForDateIsHolidayAndTypeDifferentFromNightAndAgeLessThan15(int age) {
        int cost = invokeComputeCost("1jour", age, "2019-02-18");

        assertEquals(25, cost);
    }

    @ParameterizedTest(name = "Age is {0}")
    @MethodSource("rangeFrom15To64")
    void priceForDateIsHolidayAndTypeDifferentFromNightAndAgeBetween15And64(int age) {
        int cost = invokeComputeCost("1jour", age, "2019-02-25");

        assertEquals(35, cost);
    }

    @ParameterizedTest(name = "Age is {0}")
    @MethodSource("rangeFrom65To100")
    void priceForDateIsHolidayAndTypeDifferentFromNightAndAgeGreaterThan64(int age) {
        int cost = invokeComputeCost("1jour", age, "2019-03-04");

        assertEquals(27, cost);
    }

    @ParameterizedTest(name = "Age is {0}")
    @MethodSource("rangeFrom65To100")
    void priceForDateIsHolidayAndMondayAndTypeDifferentFromNightAndAgeGreaterThan64(int age) {
        int cost = invokeComputeCost("1jour", age, "2019-09-09");

        assertEquals(18, cost);
    }

    @Test
    void insertPriceFor1WeekAndQueryIt() {
        final String type = "1week";
        final int price = 432;
        invokeInsertPrice(type, price);

        final int costResult = invokeComputeCost(type, null, null);

        assertEquals(price, costResult);
    }

    @Test
    void insertPriceFor1WeekEndAndQueryIt() {
        final String type = "1weekend";
        final int price = 76;
        invokeInsertPrice(type, price);

        final int costResult = invokeComputeCost(type, null, null);

        assertEquals(price, costResult);
    }

    private static IntStream rangeFrom6To64() {
        return IntStream.range(6, 64);
    }

    private static IntStream rangeFrom15To64() {
        return IntStream.range(15, 64);
    }

    private static IntStream rangeFrom65To100() {
        return IntStream.range(65, 100);
    }

    private static IntStream rangeFrom6To14() {
        return IntStream.range(6, 14);
    }

    private int invokeComputeCost(final String type, final Integer age, final String date) {
        Cost cost = new Cost(0);
        try {
            cost = pricesService.computeCost(type, age, parseDate(date));
            return cost.getCost();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cost.getCost();
    }

    Date parseDate(String dateStr) {
        DateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = Objects.isNull(dateStr) ? null : isoFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    private void invokeInsertPrice(String type, int price) {
        try {
            pricesService.insertBasePrice(price, type);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
