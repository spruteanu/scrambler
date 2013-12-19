package org.prismus.scrambler.property;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @author Serge Pruteanu
 */
public class CurrentTime extends Constant<Date> {
    public CurrentTime() {
        super();
    }

    public Date value() {
        return new Timestamp(System.currentTimeMillis());
    }

}
