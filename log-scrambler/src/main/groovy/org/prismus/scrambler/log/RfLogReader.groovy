package org.prismus.scrambler.log

import groovy.transform.CompileStatic
import groovy.transform.PackageScope

/**
 * @author Serge Pruteanu
 */
@CompileStatic
@PackageScope
class RfLogReader extends LogReader {
    RandomAccessFile rf

    RfLogReader() {
    }

    RfLogReader(LogContext context, RandomAccessFile rf, Object source = null) {
        super(context)
        this.rf = rf
        this.source = source
    }

    RfLogReader withFile(RandomAccessFile rf) {
        this.rf = rf
        return this
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
