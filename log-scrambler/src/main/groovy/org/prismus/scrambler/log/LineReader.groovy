package org.prismus.scrambler.log

/**
 * @author Serge Pruteanu
 */
interface LineReader extends Closeable {
    static final String LINE_BREAK = System.getProperty('line.separator')
    String readLine()
}
