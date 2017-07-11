package org.prismus.scrambler.log

import groovy.transform.CompileStatic
import groovy.transform.PackageScope

/**
 * @author Serge Pruteanu
 */
@CompileStatic
@PackageScope
class IoEntryReader extends EntryReader implements Closeable {
    private Reader reader

    IoEntryReader(LogContext context, Reader reader, Object source = null) {
        super(context)
        this.reader = reader
        this.source = source
    }

    @Override
    protected LogEntry doRead() {
        LogEntry logEntry = null
        try {
            throw new RuntimeException()
        } finally {
            if (logEntry == null) {
                close()
            }
        }
    }

    @Override
    void close() throws IOException {
        if (reader) {
            reader.close()
        }
    }

    static IoEntryReader of(LogContext context, Reader reader, String source = null) {
        return new IoEntryReader(context, reader, source)
    }

    static IoEntryReader of(LogContext context, InputStream inputStream, String source = null) {
        return new IoEntryReader(context, new BufferedReader(new InputStreamReader(inputStream)), source)
    }

    static IoEntryReader of(LogContext context, File file, String source = null) {
        return new IoEntryReader(context, new BufferedReader(new FileReader(file)), source)
    }

    static IoEntryReader of(LogContext context, String content, String source = null) {
        return new IoEntryReader(context, new StringReader(content), source)
    }

}
