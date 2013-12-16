package org.prismus.scrambler.property

import groovy.transform.CompileStatic
import org.apache.commons.lang.time.DateUtils

import java.sql.Timestamp

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class IncrementalDate extends Generic<Date> {
    private static final long DEFAULT_STEP = 24 * 60 * 1000L

    private Long step

    IncrementalDate() {
        this(null, new Date(), DEFAULT_STEP)
    }

    IncrementalDate(String name) {
        this(name, new Date(), DEFAULT_STEP)
    }

    IncrementalDate(String name, Long step) {
        this(name, new Date(), step)
    }

    IncrementalDate(String name, Date value) {
        this(name, value, DEFAULT_STEP)
    }

    IncrementalDate(String name, Date value, Long step) {
        super(name, value)
        this.step = step != null ? step : DEFAULT_STEP
    }

    void setStep(long step) {
        this.step = step
    }

    Date value() {
        Date value = super.value()
        value = value == null ? new Timestamp(System.currentTimeMillis()) : DateUtils.addMilliseconds(value, step.intValue())
        setValue(value)
        return value
    }
}
