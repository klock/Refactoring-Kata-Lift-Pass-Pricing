package dojo.liftpasspricing.boundary;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        int costResult = queryApplication(null, "1jour", null);

        assertEquals(35, costResult);
    }

    @Test
    void priceForTypeNightAndAgeNull() {
        int costResult = queryApplication(null, "night", null);

        assertEquals(0, costResult);
    }

    @Test
    void priceForType1jourAndDateInHolidayAndAge10() {
        int costResult = queryApplication("10", "1jour", "2019-02-18");
        assertEquals(25, costResult);
    }

    @Test
    void insertPriceFor1WeekAndQueryIt() {
        final String type = "1week";
        final int cost = 432;
        putPrice(type, cost);

        final int costResult = queryApplication(null, type, null);

        assertEquals(cost, costResult);
    }

    @Test
    void insertPriceFor1WeekEndAndQueryIt() {
        final String type = "1weekend";
        final int cost = 76;
        putPrice(type, cost);

        final int costResult = queryApplication(null, type, null);

        assertEquals(cost, costResult);
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

    private void putPrice(String type, int cost) {
        RequestSpecification when = RestAssured
                .given().port(4567)
                .when();

        // Add query parameter 'type' if present
        if (StringUtils.isNotBlank(type)) {
            when = when.param("type", type);
        }
        // Add query parameter 'cost' if present
        if (StringUtils.isNotBlank(type)) {
            when = when.param("cost", cost);
        }

        when.put("/prices").then()
                .assertThat()
                .statusCode(200);
    }
}
