package org.prismus.scrambler.property

import groovy.transform.CompileStatic
import org.apache.commons.lang.time.DateUtils

import java.sql.Timestamp

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class RandomDate extends RandomRange<Date> {

    RandomDate() {
        this(null, new Date())
    }

    RandomDate(String name) {
        this(name, new Date())
    }

    RandomDate(String name, Date value) {
        super(name, value)
    }

    RandomDate(String name, Date minimum, Date maximum) {
        super(name, minimum, maximum)
    }

    Date value() {
        Date value = super.value()
        if (minimum != null && maximum != null) {
            value = new Date(new RandomLong(getName()).between(minimum.getTime(), maximum.getTime()).value())
        } else {
            if (value == null) {
                value = new Timestamp(System.currentTimeMillis())
            }
            final Date minDate = DateUtils.round(value, Calendar.DATE)
            value = new Date(new RandomLong(getName()).between(
                    minDate.getTime(),
                    DateUtils.addDays(minDate, 1).getTime()
            ).value())
        }
        return value
    }
}
