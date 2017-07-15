package org.prismus.scrambler.log

import groovy.transform.CompileStatic
import groovy.transform.PackageScope

/**
 * @author Serge Pruteanu
 */
@CompileStatic
@PackageScope
class RfLogReader extends LogReader {
    private RandomAccessFile rf

    RfLogReader(LogContext context, RandomAccessFile rf) {
        super(context)
        this.rf = rf
        this.source = source
    }

    @Override
    protected String readLine() {
        return rf.readLine()
    }

    @Override
    protected void doClose() {
        Utils.closeQuietly(rf)
        rf = null
    }

    static RfLogReader of(LogContext context, RandomAccessFile rf) {
        return new RfLogReader(context, rf)
    }

}
