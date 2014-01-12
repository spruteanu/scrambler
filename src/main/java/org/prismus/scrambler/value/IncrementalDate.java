package org.prismus.scrambler.value;

import org.apache.commons.lang.time.DateUtils;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Serge Pruteanu
 */
public class IncrementalDate extends Constant<Date> {
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
        Date value = super.next();
        if (value == null) {
            value = new Timestamp(System.currentTimeMillis());
        } else {
            if (calendarField != null) {
                switch (calendarField) {
                    case Calendar.YEAR:
                        DateUtils.addYears(value, calendarField);
                        break;
                    case Calendar.MONTH:
                        DateUtils.addMonths(value, calendarField);
                        break;
                    case Calendar.WEEK_OF_YEAR:
                        DateUtils.addWeeks(value, calendarField);
                        break;
                    case Calendar.DAY_OF_MONTH:
                        DateUtils.addDays(value, calendarField);
                        break;
                    case Calendar.HOUR_OF_DAY:
                        DateUtils.addHours(value, calendarField);
                        break;
                    case Calendar.MINUTE:
                        DateUtils.addMinutes(value, calendarField);
                        break;
                    case Calendar.SECOND:
                        DateUtils.addSeconds(value, calendarField);
                        break;
                    default:
                        value = DateUtils.addMilliseconds(value, step);
                        break;
                }
            } else {
                value = DateUtils.addMilliseconds(value, step);
            }
        }
        setValue(value);
        return value;
    }

}
