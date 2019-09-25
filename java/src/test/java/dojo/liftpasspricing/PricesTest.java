package dojo.liftpasspricing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
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
        JsonPath response = queryApplication(null, "1jour");

        assertEquals(35, response.getInt("cost"));
    }

    private JsonPath queryApplication(String age, String type) {
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
