package org.prismus.scrambler.value;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Serge Pruteanu
 */
class IncrementalDate extends Constant<Date> {
    private static final int DEFAULT_STEP = 24 * 60 * 1000;

    private Integer calendarField;
    private Integer step;

    public IncrementalDate() {
        this(new Date(), DEFAULT_STEP);
    }

    public IncrementalDate(Integer step) {
        this(new Date(), step);
    }

    public IncrementalDate(Date value) {
        this(value, DEFAULT_STEP);
    }

    public IncrementalDate(Date value, Integer step) {
        this(value, step, null);
    }

    public IncrementalDate(Date value, Integer step, Integer calendarField) {
        super(value);
        if (step != null) {
            this.step = step;
        } else {
            if (calendarField == null) {
                this.step = DEFAULT_STEP;
                this.calendarField = Calendar.MILLISECOND;
            } else {
                this.step = 1;
            }
        }
        this.calendarField = calendarField;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public void setCalendarField(int calendarField) {
        this.calendarField = calendarField;
    }

    public Date next() {
        Date value = get();
        if (value == null) {
            value = new Timestamp(System.currentTimeMillis());
        } else {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(value);
            calendar.add(calendarField, step);
            value = calendar.getTime();
        }
        setValue(value);
        return value;
    }

}
