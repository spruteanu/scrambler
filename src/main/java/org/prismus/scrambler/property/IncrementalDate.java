package org.prismus.scrambler.property;

import org.apache.commons.lang.time.DateUtils;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @author Serge Pruteanu
 */
public class IncrementalDate extends Constant<Date> {
    private static final long DEFAULT_STEP = 24 * 60 * 1000L;

    private Long step;

    public IncrementalDate() {
        this(null, new Date(), DEFAULT_STEP);
    }

    public IncrementalDate(String name) {
        this(name, new Date(), DEFAULT_STEP);
    }

    public IncrementalDate(String name, Long step) {
        this(name, new Date(), step);
    }

    public IncrementalDate(String name, Date value) {
        this(name, value, DEFAULT_STEP);
    }

    public IncrementalDate(String name, Date value, Long step) {
        super(name, value);
        this.step = step != null ? step : DEFAULT_STEP;
    }

    public void setStep(long step) {
        this.step = step;
    }

    public Date value() {
        Date value = super.value();
        value = value == null
                ? new Timestamp(System.currentTimeMillis())
                : DateUtils.addMilliseconds(value, step.intValue());
        setValue(value);
        return value;
    }
}
