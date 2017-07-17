package org.prismus.scrambler.log

/**
 * @author Serge Pruteanu
 */
interface LineReader extends Closeable {
    String readLine()
}
