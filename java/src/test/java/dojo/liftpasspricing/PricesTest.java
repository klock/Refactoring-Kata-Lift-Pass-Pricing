package dojo.liftpasspricing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.commons.util.StringUtils;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import spark.Spark;

class PricesTest {

    @BeforeEach
    void createPrices() throws SQLException {
        Prices.createApp();
    }

    @AfterEach
    void stopApplication() {
        Spark.stop();
    }

    @Test
    void priceForType1jour() {
        int cost = queryApplication(null, "1jour", null);

        assertEquals(35, cost);
    }

    @Test
    void priceForTypeNightAndAgeNull() {
        int cost = queryApplication(null, "night", null);

        assertEquals(0, cost);
    }

    @ParameterizedTest(name = "{index} Age is {0}")
    @ValueSource(ints = {0, 1, 2, 3, 4, 5})
    void priceForAgeBetween0And6(int age) {
        int cost = queryApplication(String.valueOf(age), "1jour", null);

        assertEquals(0, cost);
    }

    @ParameterizedTest(name = "Age is {0}")
    @MethodSource("rangeFrom6To64")
    void priceForTypeNightAndAgeBetween6And63(int age) {
        int cost = queryApplication(String.valueOf(age), "night", null);

        assertEquals(19, cost);
    }

    @ParameterizedTest(name = "Age is {0}")
    @MethodSource("rangeFrom65To100")
    void priceForTypeNightAndAgeGreaterThan5(int age) {
        int cost = queryApplication(String.valueOf(age), "night", null);

        assertEquals(8, cost);
    }

    @ParameterizedTest(name = "Age is {0}")
    @MethodSource("rangeFrom6To14")
    void priceForTypeDifferentFromNightAndAgeLessThan15(int age) {
        int cost = queryApplication(String.valueOf(age), "1jour", null);

        assertEquals(25, cost);
    }

    @ParameterizedTest(name = "Age is {0}")
    @MethodSource("rangeFrom15To64")
    void priceForTypeDifferentFromNightAndAgeBetween15And64(int age) {
        int cost = queryApplication(String.valueOf(age), "1jour", null);

        assertEquals(35, cost);
    }

    @ParameterizedTest(name = "Age is {0}")
    @MethodSource("rangeFrom65To100")
    void priceForTypeDifferentFromNightAndAgeGreaterThan64(int age) {
        int cost = queryApplication(String.valueOf(age), "1jour", null);

        assertEquals(27, cost);
    }

    @ParameterizedTest(name = "Age is {0}")
    @MethodSource("rangeFrom6To14")
    void priceForDateIsHolidayAndTypeDifferentFromNightAndAgeLessThan15(int age) {
        int cost = queryApplication(String.valueOf(age), "1jour", "2019-02-18");

        assertEquals(25, cost);
    }

    @ParameterizedTest(name = "Age is {0}")
    @MethodSource("rangeFrom15To64")
    void priceForDateIsHolidayAndTypeDifferentFromNightAndAgeBetween15And64(int age) {
        int cost = queryApplication(String.valueOf(age), "1jour", "2019-02-25");

        assertEquals(35, cost);
    }

    @ParameterizedTest(name = "Age is {0}")
    @MethodSource("rangeFrom65To100")
    void priceForDateIsHolidayAndTypeDifferentFromNightAndAgeGreaterThan64(int age) {
        int cost = queryApplication(String.valueOf(age), "1jour", "2019-03-04");

        assertEquals(27, cost);
    }

    @ParameterizedTest(name = "Age is {0}")
    @MethodSource("rangeFrom65To100")
    void priceForDateIsHolidayAndMondayAndTypeDifferentFromNightAndAgeGreaterThan64(int age) {
        int cost = queryApplication(String.valueOf(age), "1jour", "2019-09-09");

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

    private int queryApplication(String age, String type, final String date) {
        RequestSpecification when = RestAssured
                .given().port(4567)
                .when();

        // Add query parameter 'age' if present
        if (StringUtils.isNotBlank(age)) {
            when = when.param("age", age);
        }
        // Add query parameter 'type' if present
        if (StringUtils.isNotBlank(type)) {
            when = when.param("type", type);
        }
        // Add query parameter 'date' if present
        if (StringUtils.isNotBlank(date)) {
            when = when.param("date", date);
        }

        final JsonPath jsonResponse =
                when
                        .get("/prices")
                .then()
                        .assertThat()
                        .statusCode(200)
                .assertThat()
                        .contentType("application/json")
                .extract().jsonPath();

        return jsonResponse.getInt("cost");
    }
}
