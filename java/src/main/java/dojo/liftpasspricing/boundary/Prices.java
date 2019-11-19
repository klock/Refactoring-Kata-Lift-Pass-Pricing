package dojo.liftpasspricing.boundary;

import static spark.Spark.after;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.put;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import dojo.liftpasspricing.PricesService;
import dojo.liftpasspricing.domain.Cost;

public class Prices {

    public static void createApp() throws SQLException {

        final Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lift_pass", "root", "mysql");
        final PricesService service = new PricesService();

        port(4567);

        put(PricesRoutes.BASE_URI, (req, res) -> {
            final String costQP = req.queryParams(PricesRoutes.QP.COST);
            int price = Integer.parseInt(costQP);
            String type = req.queryParams(PricesRoutes.QP.TYPE);

            service.insertBasePrice(price, type);

            return "";
        });

        get(PricesRoutes.BASE_URI, (req, res) -> {
            final String ageQP = req.queryParams(PricesRoutes.QP.AGE);
            final String typeQP = req.queryParams(PricesRoutes.QP.TYPE);
            final String dateQP = req.queryParams(PricesRoutes.QP.DATE);

            final Integer age = ageQP != null ? Integer.valueOf(ageQP) : null;
            DateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd");
            final Date date = Objects.isNull(dateQP) ? null : isoFormat.parse(dateQP);

            final Cost cost = service.computeCost(typeQP, age, date);
            return cost.toJson();
        });

        after((req, res) -> res.type("application/json"));
    }

}
