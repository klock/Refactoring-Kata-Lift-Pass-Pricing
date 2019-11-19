/*
 *                  C O P Y R I G H T  (c) 2014
 *     A G F A   H E A L T H C A R E   C O R P O R A T I O N
 *                     All Rights Reserved
 *
 *
 *         THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF
 *                       AGFA CORPORATION
 *        The copyright notice above does not evidence any
 *       actual or intended publication of such source code.
 */
package dojo.liftpasspricing.infrastructure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbUtil {

    public static Connection createConnection() throws SQLException {
        // TODO get those from a property file
        final String host = "localhost";
        final String port = "3306";
        final String schema = "lift_pass";
        final String user = "root";
        final String password = "mysql";
        return DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + schema, user, password);
    }
}
