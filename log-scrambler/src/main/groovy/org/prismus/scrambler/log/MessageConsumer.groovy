package org.prismus.scrambler.log

import groovy.transform.CompileStatic
import groovy.transform.PackageScope

import java.util.regex.Pattern

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class MessageConsumer implements LogConsumer {
    @PackageScope
    static final String EXCEPTION_REGEX = '^.+Exception[^\\n]++(?:\\s+at .++)+'
    private static final Pattern EXCEPTION_PATTERN = ~/(?ms)(${EXCEPTION_REGEX})/
    private static final Pattern MESSAGE_WITH_EXCEPTION_PATTERN = ~/(?ms)([^\n]+)\n(${EXCEPTION_REGEX})/

    static final String ERROR_MESSAGE = 'ErrorMessage'
    static final String EXCEPTION = 'Exception'
    String group

    MessageConsumer(String group = null) {
        this.group = group
    }

    @Override
    void consume(LogEntry entry) {
        final value = entry.getLogValue(group)
        if (value && EXCEPTION_PATTERN.matcher(value.toString()).matches()) {
            String errorMessage = null
            String exception = null

            final matcher = MESSAGE_WITH_EXCEPTION_PATTERN.matcher(value.toString())
            if (matcher.find()) {
                errorMessage = matcher.group(1)
                exception = matcher.group(2)
            }
            if (errorMessage) {
                entry.putLogValue(ERROR_MESSAGE, errorMessage)
            }
            if (exception) {
                entry.putLogValue(EXCEPTION, exception)
            }
        }
    }

}
