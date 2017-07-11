package org.prismus.scrambler.log

import groovy.transform.CompileStatic
import groovy.transform.PackageScope

/**
 * @author Serge Pruteanu
 */
@CompileStatic
@PackageScope
class IoEntryReader extends EntryReader {
    private Reader reader

    IoEntryReader(LogContext context, Reader reader, Object source = null) {
        super(context)
        this.reader = reader
        this.source = source
    }

    @Override
    protected String readLine() {
        return reader.readLine()
    }

    @Override
    protected void doClose() {
        Utils.closeQuietly(reader)
    }

    static IoEntryReader of(LogContext context, Reader reader, String source = null) {
        return new IoEntryReader(context, reader, source)
    }

    static IoEntryReader of(LogContext context, InputStream inputStream, String source = null) {
        return new IoEntryReader(context, new BufferedReader(new InputStreamReader(inputStream)), source)
    }

    static IoEntryReader of(LogContext context, File file) {
        return new IoEntryReader(context, new BufferedReader(new FileReader(file)), file)
    }

    static IoEntryReader of(LogContext context, String content, String source = null) {
        return new IoEntryReader(context, new StringReader(content), source)
    }

}
