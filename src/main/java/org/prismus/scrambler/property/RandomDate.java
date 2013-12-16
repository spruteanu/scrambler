package org.prismus.scrambler.property;

import org.apache.commons.lang.time.DateUtils;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Serge Pruteanu
 */
public class RandomDate extends RandomRange<Date> {

    public RandomDate() {
        this(null, new Date());
    }

    public RandomDate(String name) {
        this(name, new Date());
    }

    public RandomDate(String name, Date value) {
        super(name, value);
    }

    public RandomDate(String name, Date minimum, Date maximum) {
        super(name, minimum, maximum);
    }

    public Date value() {
        Date value = super.value();
        if (minimum != null && maximum != null) {
            value = new Date(new RandomLong(getName()).between(minimum.getTime(), maximum.getTime()).value());
        } else {
            if (value == null) {
                value = new Timestamp(System.currentTimeMillis());
            }
            final Date minDate = DateUtils.round(value, Calendar.DATE);
            value = new Date(new RandomLong(getName())
                    .between(
                            minDate.getTime(),
                            DateUtils.addDays(minDate, 1).getTime()
                    ).value()
            );
        }
        return value;
    }
}
