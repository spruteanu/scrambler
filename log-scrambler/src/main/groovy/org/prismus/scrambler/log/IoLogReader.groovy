package org.prismus.scrambler.log

import groovy.transform.CompileStatic
import groovy.transform.PackageScope

/**
 * @author Serge Pruteanu
 */
@CompileStatic
@PackageScope
class IoLogReader extends LogReader {
    Reader reader

    IoLogReader(LogContext context, Reader reader, Object source = null) {
        super(context)
        this.reader = reader
        this.source = source
    }

    IoLogReader withReader(Reader reader) {
        this.reader = reader
        return this
    }

    @Override
    protected String readLine() {
        return reader.readLine()
    }

    @Override
    protected void doClose() {
        Utils.closeQuietly(reader)
        reader = null
    }

    static IoLogReader of(LogContext context, Reader reader, String source = null) {
        return new IoLogReader(context, reader, source)
    }

    static IoLogReader of(LogContext context, InputStream inputStream, String source = null) {
        return new IoLogReader(context, new BufferedReader(new InputStreamReader(inputStream)), source)
    }

    static IoLogReader of(LogContext context, File file) {
        return new IoLogReader(context, new BufferedReader(new FileReader(file)), file)
    }

    static IoLogReader of(LogContext context, String content, String source = null) {
        return new IoLogReader(context, new StringReader(content), source)
    }

}
