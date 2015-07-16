package org.prismus.scrambler.value;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Serge Pruteanu
 */
public class RandomDate extends AbstractRandomRange<Date> {

    public RandomDate() {
        this(null, null, null);
    }

    public RandomDate(Date value) {
        this(value, null, null);
    }

    public RandomDate(Date minimum, Date maximum) {
        this(null, minimum, maximum);
    }

    public RandomDate(Date value, Date minimum, Date maximum) {
        super(value, minimum, maximum);
    }

    public Date next() {
        Date value = super.next();
        if (minimum != null && maximum != null) {
            value = new Date(new RandomLong().between(minimum.getTime(), maximum.getTime()).next());
        } else {
            if (value == null) {
                value = minimum;
            }
            if (value == null) {
                value = maximum;
            }
            if (value == null) {
                value = new Timestamp(System.currentTimeMillis());
            }
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(value);
            calendar.add(Calendar.DATE, 1);
            value = new Date(new RandomLong().between(
                    value.getTime(),
                    calendar.getTimeInMillis()
            ).next());
        }
        setValue(value);
        return value;
    }
}
