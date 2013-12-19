package org.prismus.scrambler.property;

import org.apache.commons.lang.time.DateUtils;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Serge Pruteanu
 */
public class RandomDate extends AbstractRandomRange<Date> {

    public RandomDate() {
        this(new Date());
    }

    public RandomDate(Date value) {
        super(value);
    }

    public RandomDate(Date minimum, Date maximum) {
        super(minimum, maximum);
    }

    public Date value() {
        Date value = super.value();
        if (minimum != null && maximum != null) {
            value = new Date(new RandomLong().between(minimum.getTime(), maximum.getTime()).value());
        } else {
            if (value == null) {
                value = new Timestamp(System.currentTimeMillis());
            }
            final Date minDate = DateUtils.round(value, Calendar.DATE);
            value = new Date(new RandomLong()
                    .between(
                            minDate.getTime(),
                            DateUtils.addDays(minDate, 1).getTime()
                    ).value()
            );
        }
        return value;
    }
}
