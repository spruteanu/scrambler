package org.prismus.scrambler.log

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class Log4jLogstash {

    protected
    static int specifierToGrok(StringBuilder sb, char ch, int i, String conversionPattern) {
        final matcher = Log4jConsumer.SPEC_PATTERN.matcher(conversionPattern.substring(i + 1))
        if (!matcher.find()) {
            throw new UnsupportedOperationException("Unsupported/unknown logging conversion pattern: '${conversionPattern.substring(i + 1)}'; of '$conversionPattern'")
        }
        String regEx = ''
        String spec = matcher.group(0)
        String padding = matcher.group(1)
        String maxLength = matcher.group(2)
        ch = spec.charAt(spec.length() - 1)
        switch (ch) {
            case 'c': // logging event category
                regEx = '[^ ]+'
//                consumer.group(EVENT_CATEGORY)
                break
            case 'C': // fully qualified class name of the caller
                regEx = '[^ ]+'
//                consumer.group(CALLER_CLASS)
                break
            case 'd': // date of the logging event. The date conversion specifier may be followed by a date format specifier enclosed between braces. For example, %d{HH:mm:ss,SSS} or %d{dd MMM yyyy HH:mm:ss,SSS}. If no date format specifier is given then ISO8601 format is assumed.
//                i = dateFormatToRegex(consumer, sb, i, conversionPattern)
                break
            case 'F': // file name where the logging request was issued.
                regEx = '[^ ]+'
//                consumer.group(CALLER_FILE_NAME)
                break
            case 'l': // file name where the logging request was issued. The location information depends on the JVM implementation but usually consists of the fully qualified name of the calling method followed by the callers source the file name and line number between parentheses.
                regEx = '[^ ]+'
//                consumer.group(CALLER_LOCATION)
                break
            case 'L': // line number from where the logging request was issued.
                regEx = '[\\d^ ]+'
//                consumer.group(CALLER_LINE)
                break
            case 'm': // message
                regEx = '.+'
//                consumer.group(MESSAGE)
                break
            case 'M': // method name where the logging request was issued.
                regEx = '[^ ]+'
//                consumer.group(CALLER_METHOD)
                break
            case 'n': // line break, skip it
                return i + 2
            case 'p': // priority of the logging event.
                regEx = '[\\w ]+'
//                consumer.group(PRIORITY)
                break
            case 'r': // number of milliseconds
                regEx = '[\\d^ ]+'
//                consumer.group(LOGGING_DURATION)
                break
            case 't': // name of the thread that generated the logging event.
                regEx = '.+'
//                consumer.group(THREAD_NAME)
                break
            case 'x': // NDC (nested diagnostic context) associated with the thread that generated the logging event.
                regEx = '[^ ]*'
//                consumer.group(THREAD_NDC)
                break
            case 'X': // MDC (mapped diagnostic context) associated with the thread that generated the logging event. The X conversion character must be followed by the key for the map placed between braces, as in %X{clientNumber} where clientNumber is the key. The value in the MDC corresponding to the key will be output.
                regEx = '[^ ]*'
//                consumer.group(THREAD_MDC)
                break
            default:
                throw new UnsupportedOperationException("Unsupported/unknown logging conversion pattern: '${conversionPattern.substring(i + 1)}'; of '$conversionPattern'")
        }
        String paddingRegEx = ''
        if (padding) {
            padding = padding.trim()
            if (padding.charAt(0) == '-' as char) {
                padding = padding.substring(1)
                regEx = '\\s*' + regEx
            }
            paddingRegEx = padding
        }
        if (maxLength) {
            if (!paddingRegEx) {
                paddingRegEx = '1'
            }
            maxLength = maxLength.substring(1).trim()
            regEx = "${regEx.substring(0, regEx.length() - 1)}{$paddingRegEx,${maxLength.trim()}}"
        } else {
            if (paddingRegEx) {
                regEx = "${regEx.substring(0, regEx.length() - 1)}{$paddingRegEx,}"
            }
        }
        if (regEx) {
            sb.append('(').append(regEx).append(')')
        }
        return i + spec.length()
    }

    protected static String conversionPatternToGrok(final String conversionPattern) {
        final sb = new StringBuilder()
        final cs = '%' as char
        final length = conversionPattern.length()
        final chars = conversionPattern.toCharArray()
        for (int i = 0; i < length; i++) {
            char ch = chars[i]
            if (ch == cs) {
                if (i < length && chars[i + 1] == cs) {
                    Log4jConsumer.appendNonSpecifierChar(sb, '\\' as char)
                    Log4jConsumer.appendNonSpecifierChar(sb, ch)
                    i++
                    continue
                }
                i = specifierToGrok(sb, ch, i, conversionPattern)
            } else {
                Log4jConsumer.appendNonSpecifierChar(sb, ch)
            }
        }
        final regex = sb.toString()
//        consumer.pattern = ~/^(?ms)$regex/
        return regex
    }

    protected
    static int dateFormatToRegex(StringBuilder sb, int index, String specString) {
        final pattern = ~/d(\{.+\})*/
        final matcher = pattern.matcher(specString.substring(index + 1))
        if (!matcher.find()) {
            throw new UnsupportedOperationException("Unsupported/unknown logging conversion pattern: ${specString.substring(index + 1)}; of $specString")
        }
        String format = matcher.group(1)
        String dateFormat = matcher.group(1)
        if (format) {
            switch (format.trim()) {
                case Log4jConsumer.ABSOLUTE_LDF:
                    dateFormat = Log4jConsumer.ABSOLUTE_DATE_FORMAT
                    index += Log4jConsumer.ABSOLUTE_LDF.length()
                    break
                case Log4jConsumer.DATE_LDF:
                    dateFormat = Log4jConsumer.DATE_FORMAT
                    index += Log4jConsumer.DATE_LDF.length()
                    break
                case Log4jConsumer.ISO8601_LDF:
                    dateFormat = Log4jConsumer.ISO8601_DATE_FORMAT
                    index += Log4jConsumer.ISO8601_LDF.length()
                    break
                default:
                    dateFormat = format.substring(1, format.length() - 1)
                    index += dateFormat.length() + 2
                    break
            }
        }
        if (!dateFormat) {
            dateFormat = Log4jConsumer.ISO8601_DATE_FORMAT
        }
        sb.append('(').append(Log4jConsumer.dateFormatToRegEx(dateFormat)).append(')')
//        consumer.dateFormat(dateFormat)
        return index
    }

}
