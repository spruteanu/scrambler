package org.prismus.scrambler.value;

import java.util.Date;
import java.util.Map;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class DateValue {
    public static IncrementalDate increment(Date self) {
        return new IncrementalDate(self);
    }

    public static IncrementalDate increment(Date self, Integer step) {
        return new IncrementalDate(self, step);
    }

    public static IncrementalDate increment(Date self, Integer step, Integer calendarField) {
        return new IncrementalDate(self, step, calendarField);
    }

    public static ArrayValue<Date> incrementArray(Date self, Integer step, Integer count) {
        return new ArrayValue<Date>(Date.class, count, increment(self, step));
    }

    public static IncrementalDate increment(Date self, Map<Integer, Integer> calendarFieldStepMap) {
        return new IncrementalDate(self).incrementBy(calendarFieldStepMap);
    }

    public static ArrayValue<Date> incrementArray(Date self, Map<Integer, Integer> calendarFieldStepMap, Integer count) {
        return new ArrayValue<Date>(Date.class, count, increment(self, calendarFieldStepMap));
    }

    public static ArrayValue<Date> incrementArray(Date self, Integer step, Integer calendarField, Integer count) {
        return new ArrayValue<Date>(Date.class, count, increment(self, step, calendarField));
    }

    public static ArrayValue<Date> incrementArray(Date self, Integer count) {
        return new ArrayValue<Date>(Date.class, count, increment(self));
    }

    public static RandomDate random(Date value) {
        return new RandomDate(value);
    }

    public static RandomDate random(Date minimum, Date maximum) { // todo Serge: review signature to use 3 args: val, min, max
        return new RandomDate(minimum, maximum);
    }

    public static RandomDate random(Date val, Date minimum, Date maximum) {
        return new RandomDate(val, minimum, maximum);
    }

    public static ArrayValue<Date> randomArray(Date val, Date minimum, Date maximum, Integer count) {
        final RandomDate randomDate = new RandomDate(val, minimum, maximum);
        randomDate.next();
        return new ArrayValue<Date>(Date.class, count, randomDate);
    }
}
