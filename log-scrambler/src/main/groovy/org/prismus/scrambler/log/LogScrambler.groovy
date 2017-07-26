package org.prismus.scrambler.log

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class LogScrambler {
    // todo Serge: is facade needed?

    static void main(String[] args) {
        LogContext.builder(args).build().consume()
    }

}
