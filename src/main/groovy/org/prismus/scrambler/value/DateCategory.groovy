package org.prismus.scrambler.value

import groovy.transform.CompileStatic

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
class DateCategory {

    @CompileStatic
    public static IncrementalDate increment(Date self) {
        return new IncrementalDate(self)
    }

    @CompileStatic
    public static IncrementalDate increment(Date self, Integer step) {
        return new IncrementalDate(self, step)
    }

    @CompileStatic
    public static IncrementalDate increment(Date self, Integer step, Integer calendarField) {
        return new IncrementalDate(self, step, calendarField)
    }

    @CompileStatic
    public static ArrayValue<Date> incrementArray(Date self, Integer step, Integer count) {
        return new ArrayValue<Date>(Date.class, count, increment(self, step))
    }

    @CompileStatic
    public static IncrementalDate increment(Date self, Map<Integer, Integer> calendarFieldStepMap) {
        return new IncrementalDate(self).incrementBy(calendarFieldStepMap)
    }

    @CompileStatic
    public static ArrayValue<Date> incrementArray(Date self, Map<Integer, Integer> calendarFieldStepMap, Integer count) {
        return new ArrayValue<Date>(Date.class, count, increment(self, calendarFieldStepMap))
    }

    @CompileStatic
    public static ArrayValue<Date> incrementArray(Date self, Integer step, Integer calendarField, Integer count) {
        return new ArrayValue<Date>(Date.class, count, increment(self, step, calendarField))
    }

    @CompileStatic
    public static ArrayValue<Date> incrementArray(Date self, Integer count) {
        return new ArrayValue<Date>(Date.class, count, increment(self))
    }

    @CompileStatic
    public static RandomDate random(Date value) {
        return new RandomDate(value)
    }

    @CompileStatic
    public static RandomDate random(Date self, Date minimum, Date maximum) {
        return new RandomDate(self, minimum, maximum)
    }

    @CompileStatic
    public static ArrayValue<Date> randomArray(Date self, Date minimum, Date maximum, Integer count) {
        final RandomDate randomDate = new RandomDate(self, minimum, maximum)
        randomDate.next()
        return new ArrayValue<Date>(Date.class, count, randomDate)
    }

}
