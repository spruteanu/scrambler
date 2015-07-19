package org.prismus.scrambler.value;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Serge Pruteanu
 */
public class IncrementalDate extends Constant<Date> {
    private static final int DEFAULT_STEP = 24 * 60 * 1000;
    private static final int DEFAULT_CALENDAR_FIELD = Calendar.MILLISECOND;

    private Integer calendarField;
    private Integer step;
    private Calendar calendar;

    public IncrementalDate() {
        this(new Date(), DEFAULT_STEP, null);
    }

    public IncrementalDate(Integer step) {
        this(new Date(), step, null);
    }

    public IncrementalDate(Date value) {
        this(value, DEFAULT_STEP, null);
    }

    public IncrementalDate(Date value, Integer step) {
        this(value, step, null);
    }

    public IncrementalDate(Date value, Integer step, Integer calendarField) {
        super(value);
        this.step = step;
        this.calendarField = calendarField;
        calendar = Calendar.getInstance();
    }

    public void setStep(int step) {
        this.step = step;
    }

    public void setCalendarField(int calendarField) {
        this.calendarField = calendarField;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public Date next() {
        Date value = get();
        if (value == null) {
            value = new Timestamp(System.currentTimeMillis());
        }

        calendar.setTime(value);
        calendar.add(
                calendarField == null ? DEFAULT_CALENDAR_FIELD : calendarField,
                step == null ? DEFAULT_STEP : step
        );
        value = calendar.getTime();

        setValue(value);
        return value;
    }

}
