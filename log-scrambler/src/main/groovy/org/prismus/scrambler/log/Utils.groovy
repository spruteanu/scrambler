package org.prismus.scrambler.log

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class Utils {

    static closeQuietly(Closeable inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close()
            } catch (Exception ignore) { }
        }
    }

}
