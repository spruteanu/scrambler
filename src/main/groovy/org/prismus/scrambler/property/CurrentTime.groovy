package org.prismus.scrambler.property

import groovy.transform.CompileStatic

import java.sql.Timestamp
import java.util.Date

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class CurrentTime extends Generic<Date> {
    CurrentTime() {
        super()
    }

    CurrentTime(String name) {
        super(name)
    }

    Date value() {
        return new Timestamp(System.currentTimeMillis())
    }
}
