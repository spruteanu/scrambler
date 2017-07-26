package org.prismus.scrambler.log

import groovy.transform.CompileStatic

import java.util.regex.Pattern

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class Log4jConsumer extends RegexConsumer {
    private static final String ABSOLUTE = '{ABSOLUTE}'
    private static final String DATE = '{DATE}'
    private static final String ISO8601 = '{ISO8601}'
    static final String ABSOLUTE_DATE_FORMAT = 'HH:mm:ss,SSS'
    static final String ISO8601_DATE_FORMAT = 'yyyy-MM-dd HH:mm:ss,SSS' // ISO8601DateFormat
    static final String DATE_FORMAT = 'dd MMM yyyy HH:mm:ss,SSS'

    private static final Set<Character> REG_EX_CHARS_SET = toSet('[]{}\\^$|?*+()')
    private static Pattern SPEC_PATTERN = ~/([-\d]*)([\.\d]*)[cCdFlLmMnprtxX]/

    static final String EVENT_CATEGORY = 'EventCategory'
    static final String CALLER_CLASS = 'CallerClass'
    static final String TIMESTAMP = 'Timestamp'
    static final String CALLER_FILE_NAME = 'CallerFileName'
    static final String CALLER_LOCATION = 'CallerLocation'
    static final String CALLER_LINE = 'CallerLine'
    static final String MESSAGE = 'Message'
    static final String CALLER_METHOD = 'Method'
    static final String PRIORITY = 'Priority'
    static final String LOGGING_DURATION = 'LoggingDuration'
    static final String THREAD_NAME = 'Thread'
    static final String THREAD_MDC = 'ThreadMdc'
    static final String THREAD_NDC = 'ThreadNdc'

    String timestampFormat

    Log4jConsumer registerTimestamp(String timestampFormat) {
        group(TIMESTAMP)
        this.timestampFormat = timestampFormat
        return this
    }

    Log4jConsumer timestampProcessor(String timestampFormat = null) {
        if (!timestampFormat) {
            timestampFormat = this.timestampFormat
        }
        groupConsumer(TIMESTAMP, DateFormatConsumer.of(timestampFormat, TIMESTAMP))
        return this
    }

    Log4jConsumer group(String group, Integer index = null, LogConsumer processor = null) {
        if (index == null) {
            index = groupIndexMap.size() + 1
        }
        super.group(group, index, processor)
        return this
    }

    static Log4jConsumer ofPattern(String conversionPattern) {
        final processor = new Log4jConsumer()
        conversionPatternToRegex(processor, conversionPattern)
        return processor
    }

    protected static void appendNonSpecifierChar(StringBuilder sb, char ch) {
        if (REG_EX_CHARS_SET.contains(ch)) {
            sb.append('\\' as char)
            sb.append(ch)
        } else {
            sb.append(ch)
        }
    }

    protected
    static int appendDateFormatRegex(Log4jConsumer processor, StringBuilder sb, int index, String specString) {
        final pattern = ~/d(\{.+\})*/
        final matcher = pattern.matcher(specString.substring(index + 1))
        if (!matcher.find()) {
            throw new UnsupportedOperationException("Unsupported/unknown logging conversion pattern: ${specString.substring(index + 1)}; of $specString")
        }
        String format = matcher.group(1)
        String dateFormat = matcher.group(1)
        if (format) {
            switch (format.trim()) {
                case ABSOLUTE:
                    dateFormat = ABSOLUTE_DATE_FORMAT
                    index += ABSOLUTE.length()
                    break
                case DATE:
                    dateFormat = DATE_FORMAT
                    index += DATE.length()
                    break
                case ISO8601:
                    dateFormat = ISO8601_DATE_FORMAT
                    index += ISO8601.length()
                    break
                default:
                    dateFormat = format.substring(1, format.length() - 1)
                    index += dateFormat.length() + 2
                    break
            }
        }
        if (!dateFormat) {
            dateFormat = ISO8601_DATE_FORMAT
        }
        sb.append('(').append(dateFormatToRegEx(dateFormat)).append(')')
        processor.registerTimestamp(dateFormat)
        return index
    }

    protected
    static int appendSpecifierRegex(Log4jConsumer processor, StringBuilder sb, char ch, int i, String conversionPattern) {
        final matcher = SPEC_PATTERN.matcher(conversionPattern.substring(i + 1))
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
                processor.group(EVENT_CATEGORY)
                break
            case 'C': // fully qualified class name of the caller
                regEx = '[^ ]+'
                processor.group(CALLER_CLASS)
                break
            case 'd': // date of the logging event. The date conversion specifier may be followed by a date format specifier enclosed between braces. For example, %d{HH:mm:ss,SSS} or %d{dd MMM yyyy HH:mm:ss,SSS}. If no date format specifier is given then ISO8601 format is assumed.
                i = appendDateFormatRegex(processor, sb, i, conversionPattern)
                break
            case 'F': // file name where the logging request was issued.
                regEx = '[^ ]+'
                processor.group(CALLER_FILE_NAME)
                break
            case 'l': // file name where the logging request was issued. The location information depends on the JVM implementation but usually consists of the fully qualified name of the calling method followed by the callers source the file name and line number between parentheses.
                regEx = '[^ ]+'
                processor.group(CALLER_LOCATION)
                break
            case 'L': // line number from where the logging request was issued.
                regEx = '[\\d^ ]+'
                processor.group(CALLER_LINE)
                break
            case 'm': // message
                regEx = '.+'
                processor.group(MESSAGE)
                break
            case 'M': // method name where the logging request was issued.
                regEx = '[^ ]+'
                processor.group(CALLER_METHOD)
                break
            case 'n': // line break, skip it
                return i + 2
            case 'p': // priority of the logging event.
                regEx = '[\\w ]+'
                processor.group(PRIORITY)
                break
            case 'r': // number of milliseconds
                regEx = '[\\d^ ]+'
                processor.group(LOGGING_DURATION)
                break
            case 't': // name of the thread that generated the logging event.
                regEx = '.+'
                processor.group(THREAD_NAME)
                break
            case 'x': // NDC (nested diagnostic context) associated with the thread that generated the logging event.
                regEx = '[^ ]*'
                processor.group(THREAD_NDC)
                break
            case 'X': // MDC (mapped diagnostic context) associated with the thread that generated the logging event. The X conversion character must be followed by the key for the map placed between braces, as in %X{clientNumber} where clientNumber is the key. The value in the MDC corresponding to the key will be output.
                regEx = '[^ ]*'
                processor.group(THREAD_MDC)
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

    static String conversionPatternToRegex(final Log4jConsumer processor, final String conversionPattern) {
        final sb = new StringBuilder()
        final cs = '%' as char
        final length = conversionPattern.length()
        final chars = conversionPattern.toCharArray()
        for (int i = 0; i < length; i++) {
            char ch = chars[i]
            if (ch == cs) {
                if (i < length && chars[i + 1] == cs) {
                    appendNonSpecifierChar(sb, '\\' as char)
                    appendNonSpecifierChar(sb, ch)
                    i++
                    continue
                }
                i = appendSpecifierRegex(processor, sb, ch, i, conversionPattern)
            } else {
                appendNonSpecifierChar(sb, ch)
            }
        }
        final regex = sb.toString()
        processor.pattern = ~/(?ms)$regex/
        return regex
    }

    private static Set<Character> toSet(String chString) {
        final set = new HashSet<Character>(chString.length())
        for (char ch : chString.toCharArray()) {
            set.add(ch)
        }
        return set
    }

}
