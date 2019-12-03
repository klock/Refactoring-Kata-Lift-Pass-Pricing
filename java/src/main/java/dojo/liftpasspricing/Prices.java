package dojo.liftpasspricing;

import static spark.Spark.after;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.put;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import spark.Request;

public class Prices {

    public static final String NIGHT = "night";
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    public static Connection createApp() throws SQLException {

        final Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lift_pass", "root", "mysql");

        port(4567);

        put("/prices", (req, res) -> {
            int liftPassCost = Integer.parseInt(req.queryParams("cost"));
            String liftPassType = getTypeParam(req);

            Repositories.SingletonHolder.INSTANCE = new Repositories(connection);
            Repositories.price().persist(liftPassCost, liftPassType);

            return "";
        });

        get("/prices", (req, res) -> {
            final Integer age = getAgeParam(req) != null ? Integer.valueOf(getAgeParam(req)) : null;
            final String type = getTypeParam(req);
            final Date dateParam = getDateParam(req);

            Repositories.SingletonHolder.INSTANCE = new Repositories(connection);
            return getPrice(age, type, dateParam);
        });

        after((req, res) -> {
            res.type("application/json");
        });

        return Repositories.SingletonHolder.INSTANCE.connection;
    }

    private static Object getPrice(final Integer age, final String type, final Date dateParam) {
        if (age != null && age < 6) {
            return costToJson(0);
        }
        return costToJson(calculateCost(age, type, dateParam).getCost());
    }

    private static Cost calculateCost(final Integer age, final String type, final Date dateParam) {
        int reduction = 0;
        final int costForType = Repositories.price().getCostForType(type);
        double cost;
        if (!type.equals(NIGHT)) {

            boolean isHoliday = Repositories.holiday().isHoliday(dateParam);

            if (dateParam != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateParam);
                if (!isHoliday && calendar.get(Calendar.DAY_OF_WEEK) == 2) {
                    reduction = 35;
                }
            }

            if (age != null && age < 15) {
                cost = costForType * .7;
            } else {
                if (age == null) {
                    cost = costForType * (1 - reduction / 100.0);
                } else {
                    if (age > 64) {
                        cost = costForType * .75 * (1 - reduction / 100.0);
                    } else {
                        cost = costForType * (1 - reduction / 100.0);
                    }
                }
            }
        } else {
            if (age != null && age >= 6) {
                if (age > 64) {
                    cost = costForType * .4;
                } else {
                    cost = costForType;
                }
            } else {
                cost = 0;
            }
        }
        return new Cost(cost);
    }

    private static String costToJson(final double cost) {
        return "{ \"cost\": " + (int) Math.ceil(cost) + "}";
    }

    private static Date getDateParam(final Request req) throws ParseException {
        DateFormat isoFormat = new SimpleDateFormat(DATE_PATTERN);
        return req.queryParams("date") != null ? isoFormat.parse(req.queryParams("date")) : null;
    }

    private static String getTypeParam(final Request req) {
        return req.queryParams("type");
    }

    private static String getAgeParam(final Request req) {
        return req.queryParams("age");
    }

}
