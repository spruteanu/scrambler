package org.prismus.scrambler.value

import groovy.transform.CompileStatic

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@CompileStatic
class DateCategory {

    public static IncrementalDate increment(Date self, Integer calendarField = null, Integer step = null) {
        return new IncrementalDate(self, step, calendarField)
    }

    static IncrementalDate increment(Date self, Map<Integer, Integer> calendarFieldStepMap) {
        return new IncrementalDate(self).incrementBy(calendarFieldStepMap)
    }

    static ArrayValue<Date> incrementArray(Date self, Integer step = null, Integer count = null) {
        return new ArrayValue<Date>(Date.class, count, increment(self, step))
    }

    static ArrayValue<Date> incrementArray(Date self, Map<Integer, Integer> calendarFieldStepMap, Integer count = null) {
        return new ArrayValue<Date>(Date.class, count, increment(self, calendarFieldStepMap))
    }

    static ArrayValue<Date> incrementArray(Date self, Integer calendarField, Integer step, Integer count) {
        return new ArrayValue<Date>(Date.class, count, increment(self, step, calendarField))
    }

    static RandomDate random(Date self, Date minimum = null, Date maximum = null) {
        return new RandomDate(self, minimum, maximum)
    }

    static ArrayValue<Date> randomArray(Date self, Date minimum = null, Date maximum = null, Integer count = null) {
        final RandomDate randomDate = new RandomDate(self, minimum, maximum)
        randomDate.next()
        return new ArrayValue<Date>(Date.class, count, randomDate)
    }

}
