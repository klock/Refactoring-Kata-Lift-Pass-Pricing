package dojo.liftpasspricing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class PricesServiceTest {

    private Connection connection;

    @BeforeEach
    void prepare() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lift_pass", "root", "mysql");
    }

    @AfterEach
    void stopApplication() throws SQLException {
        connection.close();
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
        int cost = 0;
        try {
            cost = new PricesService(connection).computeCost(type, age, parseDate(date));
            return cost;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cost;
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
}
