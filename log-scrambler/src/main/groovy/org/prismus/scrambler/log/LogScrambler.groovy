package org.prismus.scrambler.log

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class LogScrambler {

    LogContext parse(InputStream inputStream) {
        return parse(new LogContext(), inputStream)
    }

    protected LogContext parse(LogContext context, Reader reader, String line) {
        throw new RuntimeException('implement me')
        return context
    }

    protected LogContext parse(LogContext context, InputStream inputStream) {
        throw new RuntimeException()
    }

}
