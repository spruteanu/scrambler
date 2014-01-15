package org.prismus.scrambler.value;

import org.apache.commons.lang.time.DateUtils;

import java.sql.Timestamp;
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
            value = new Date(new RandomLong().between(
                    value.getTime(),
                    DateUtils.addDays(value, 1).getTime()
            ).next());
        }
        setValue(value);
        return value;
    }
}
