package org.prismus.scrambler.log

import groovy.transform.CompileStatic
import groovy.transform.PackageScope

import java.util.regex.Pattern

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class MessageExceptionConsumer implements LogConsumer {
    @PackageScope
    static final String EXCEPTION_REGEX = '^.+Exception[^\\r\\n]++(?:\\s+at .++)+'
    private static final Pattern EXCEPTION_PATTERN = ~/(?ms)(${EXCEPTION_REGEX})/
    private static final Pattern MESSAGE_WITH_EXCEPTION_PATTERN = ~/(?ms)([^\r\n]+)[\r\n]+(${EXCEPTION_REGEX})/

    static final String ERROR_MESSAGE = 'ErrorMessage'
    static final String EXCEPTION = 'Exception'
    String group

    MessageExceptionConsumer(String group = null) {
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

    @PackageScope
    Throwable toException(String exception) {
        // todo Serge: is it needed to convert to exception object? possible yes, if to lookup for an exception of specific type
        Throwable result = null
        if (!exception) {
            return result
        }
        final lines = exception.split('[\r\n]')
        if (lines) {
            final EXCEPTION_CLASS_MESSAGE_PATTERN = ~/^.+\s(.+Exception)(:\s.+[^\r\n])*/
            final STACK_TRACE_PATTERN = ~/^\s* at\s(.+)\((.+):(\d+)\)/
            String exceptionType = lines[0]
            String detailedMessage = null

            result = Class.forName(exceptionType) as Throwable
            final stackTraces = []
            for (final line : lines) {
//                new StackTraceElement()
            }
            if (detailedMessage) {
                DefaultObjectProvider.setInstanceProperties(result, [detailMessage: detailedMessage] as Map<String, Object>)
            }
            if (stackTraces) {
                result.setStackTrace(stackTraces.toArray() as StackTraceElement[])
            }
        }
        return result
    }

}
