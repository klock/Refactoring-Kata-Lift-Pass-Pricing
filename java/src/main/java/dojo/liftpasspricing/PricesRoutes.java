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
package dojo.liftpasspricing;

public interface PricesRoutes {

    String BASE_URI = "/prices";

    interface QP {
        String COST = "cost";
        String TYPE = "type";
        String AGE = "age";
        String DATE = "date";
    }
}
