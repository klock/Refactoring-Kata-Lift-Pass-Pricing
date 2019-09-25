package dojo.liftpasspricing;

import static spark.Spark.after;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.put;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Prices {

    public static Connection createApp() throws SQLException {

        final Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lift_pass", "root", "mysql");

        port(4567);

        put(PricesRoutes.BASE_URI, (req, res) -> PricesController.insertBasePrice(connection, req));

        get(PricesRoutes.BASE_URI, (req, res) -> Prices.toJson(PricesController.computeCost(connection, req)));

        after((req, res) -> res.type("application/json"));

        return connection;
    }

    private static String toJson(int cost) {
        return "{ \"cost\": "+ cost + "}";
    }
}
