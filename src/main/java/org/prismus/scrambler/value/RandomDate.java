package org.prismus.scrambler.value;

import org.prismus.scrambler.Value;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @author Serge Pruteanu
 */
public class RandomDate extends AbstractRandomRange<Date> {

    private final RandomLong randomLong;
    private Value<Date> dateValue;

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
        randomLong = new RandomLong();
        dateValue = new Constant<Date>(new IncrementalDate().usingDefaults().next());
    }

    public RandomDate withDateValue(Value<Date> dateValue) {
        this.dateValue = dateValue;
        return this;
    }

    @Override
    protected Date min(Date val1, Date val2) {
        return val1 != null ? val2 != null ? val1.compareTo(val2) > 0 ? val2 : val1 : null : null;
    }

    @Override
    protected Date max(Date val1, Date val2) {
        return val1 != null ? val2 != null ? val1.compareTo(val2) > 0 ? val1 : val2 : val1 : val2;
    }

    public Date next() {
        Date value = get();
        if (minimum != null && maximum != null) {
            value = nextValue(minimum, maximum);
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
            value = nextValue(value, dateValue.next());
        }
        setValue(value);
        return value;
    }

    Date nextValue(Date minimum, Date maximum) {
        return new Date(randomLong.between(minimum.getTime(), maximum.getTime()).next());
    }

}
