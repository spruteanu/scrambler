package org.prismus.scrambler.log

import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class ContextBuilderTest extends Specification {

    void 'verify list files'() {
        final folder = new File(ContextBuilderTest.protectionDomain.codeSource.location.path)
        expect:
        0 < ContextBuilder.listFolderFiles(folder).size()
        2 == ContextBuilder.listFolderFiles(folder, '*.log').size()
        1 == ContextBuilder.listFolderFiles(folder, '*sample-1.log').size()
    }

}
