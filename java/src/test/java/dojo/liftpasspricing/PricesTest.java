package dojo.liftpasspricing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
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

    private Connection connection;

    @BeforeEach
    void createPrices() throws SQLException {
        connection = Prices.createApp();
    }

    @AfterEach
    void stopApplication() throws SQLException {
        Spark.stop();
        connection.close();
    }

    @Test
    void priceForType1jour() {
        JsonPath response = queryApplication(null, "1jour", null);

        assertEquals(35, response.getInt("cost"));
    }

    @Test
    void priceForTypeNightAndAgeNull() {
        JsonPath response = queryApplication(null, "night", null);

        assertEquals(0, response.getInt("cost"));
    }

    @ParameterizedTest(name = "{index} Age is {0}")
    @ValueSource(ints = {0, 1, 2, 3, 4, 5})
    void priceForAgeBetween0And6(int age) {
        JsonPath response = queryApplication(String.valueOf(age), "1jour", null);

        assertEquals(0, response.getInt("cost"));
    }

    @ParameterizedTest(name = "Age is {0}")
    @MethodSource("rangeFrom6To64")
    void priceForTypeNightAndAgeBetween6And63(int age) {
        JsonPath response = queryApplication(String.valueOf(age), "night", null);

        assertEquals(19, response.getInt("cost"));
    }

    @ParameterizedTest(name = "Age is {0}")
    @MethodSource("rangeFrom65To100")
    void priceForTypeNightAndAgeGreaterThan5(int age) {
        JsonPath response = queryApplication(String.valueOf(age), "night", null);

        assertEquals(8, response.getInt("cost"));
    }

    @ParameterizedTest(name = "Age is {0}")
    @MethodSource("rangeFrom6To14")
    void priceForTypeDifferentFromNightAndAgeLessThan15(int age) {
        JsonPath response = queryApplication(String.valueOf(age), "1jour", null);

        assertEquals(25, response.getInt("cost"));
    }

    @ParameterizedTest(name = "Age is {0}")
    @MethodSource("rangeFrom15To64")
    void priceForTypeDifferentFromNightAndAgeBetween15And64(int age) {
        JsonPath response = queryApplication(String.valueOf(age), "1jour", null);

        assertEquals(35, response.getInt("cost"));
    }

    @ParameterizedTest(name = "Age is {0}")
    @MethodSource("rangeFrom65To100")
    void priceForTypeDifferentFromNightAndAgeGreaterThan64(int age) {
        JsonPath response = queryApplication(String.valueOf(age), "1jour", null);

        assertEquals(27, response.getInt("cost"));
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

    private JsonPath queryApplication(String age, String type, final String date) {
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

        return
                when
                        .get("/prices")
                .then()
                        .assertThat()
                            .statusCode(200)
                        .assertThat()
                            .contentType("application/json")
                        .extract().jsonPath();
    }
}
