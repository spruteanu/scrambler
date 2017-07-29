package org.prismus.scrambler.log

import groovy.transform.CompileStatic

import java.util.function.Predicate

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class ClosurePredicate implements Predicate<LogEntry> {
    final Closure closure

    ClosurePredicate(Closure closure) {
        this.closure = closure
    }

    @Override
    boolean test(LogEntry logEntry) {
        return closure.call(logEntry)
    }

}
