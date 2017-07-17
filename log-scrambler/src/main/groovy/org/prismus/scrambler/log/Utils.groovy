package org.prismus.scrambler.log

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class Utils {
    static final String LINE_BREAK = System.getProperty('line.separator')

    static closeQuietly(Closeable inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close()
            } catch (Exception ignore) { }
        }
    }

}
