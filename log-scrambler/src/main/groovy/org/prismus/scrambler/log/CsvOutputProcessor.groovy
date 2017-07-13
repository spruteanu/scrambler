package org.prismus.scrambler.log

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class CsvOutputProcessor implements EntryProcessor, Closeable {
    OutputStream outputStream

    @Override
    LogEntry process(LogEntry entry) {
        throw new RuntimeException()
    }

    @Override
    void close() throws IOException {
        Utils.closeQuietly(outputStream)
    }

}
