package org.prismus.scrambler.log

import groovy.transform.CompileStatic
import groovy.transform.PackageScope

/**
 * @author Serge Pruteanu
 */
@CompileStatic
@PackageScope
class ClosureConsumer implements LogConsumer {
    private final Closure closure

    ClosureConsumer(Closure closure) {
        this.closure = closure
    }

    @Override
    void consume(LogEntry entry) {
        closure.call(entry)
    }

}
