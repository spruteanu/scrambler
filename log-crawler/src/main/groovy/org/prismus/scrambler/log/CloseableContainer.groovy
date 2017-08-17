package org.prismus.scrambler.log

import groovy.transform.CompileStatic
import groovy.transform.PackageScope

/**
 * @author Serge Pruteanu
 */
@CompileStatic
@PackageScope
class CloseableContainer implements Closeable, AutoCloseable {

    private final LinkedList<Closeable> closeables = []

    CloseableContainer add(Closeable closeable) {
        closeables.add(closeable)
        return this
    }

    CloseableContainer addAll(CloseableContainer container) {
        closeables.addAll(container.closeables)
        return this
    }

    @Override
    void close() throws IOException {
        for (final closeable : closeables) {
            Utils.closeQuietly(closeable)
        }
    }
}
