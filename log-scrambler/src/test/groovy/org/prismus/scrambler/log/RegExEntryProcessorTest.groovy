package org.prismus.scrambler.log

import spock.lang.Specification

/**
 * Created by SergeP on 7/11/2017.
 */
class RegExEntryProcessorTest extends Specification {

    void 'verify date format parser'() {
        expect:
        RegExEntryProcessor.dateFormatToRegEx('yyyy/MM/dd HH:mm:ss.SSS') == '\\w+/\\w+/\\w+ \\w+:\\w+:\\w+.\\w+'
        RegExEntryProcessor.dateFormatToRegEx('yyyy-MM-dd HH:mm:ss.SSS') == '\\w+-\\w+-\\w+ \\w+:\\w+:\\w+.\\w+'

        and: 'check converted regex matches value'
        '2008-09-06 10:51:45,473' =~ /${RegExEntryProcessor.dateFormatToRegEx('yyyy-MM-dd HH:mm:ss.SSS')}/
    }

    void 'verify reg ex parser'() {
        expect:
        true
    }

}
