package org.prismus.scrambler.log

import groovy.transform.CompileStatic

import java.util.regex.Pattern

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class Log4jScrambler {
    private static final String DEFAULT_LOG4J_DATE_FORMAT = 'yyyy-MM-dd HH:mm:ss.SSS' // ISO8601DateFormat
    private static final Set<Character> REG_EX_CHARS_SET = toSet('[]{}\\^$|?*+()')
    private static Pattern SPEC_PATTERN = ~/([-\d]*)([\.\d]*)[cCdFlLmMnprtxX]/

    protected
    static int appendNonSpecifierChar(LogContext logContext, StringBuilder sb, char ch, int index, String specString) {
        if (REG_EX_CHARS_SET.contains(ch)) {
            sb.append('\\' as char)
            sb.append(ch)
        } else {
            sb.append(ch)
        }
        return index
    }

    protected
    static int appendDateFormatRegEx(LogContext logContext, StringBuilder sb, char ch, int index, String specString) {
        final pattern = ~/d(\{.+\})*/
        final matcher = pattern.matcher(specString.substring(index + 1))
        if (!matcher.find()) {
            throw new UnsupportedOperationException("Unsupported/unknown logging conversion pattern: ${specString.substring(index + 1)}; of $specString")
        }
        String format = matcher.group(1)
        String dateFormat = matcher.group(1)
        if (format) {
            switch (format.trim()) {
                case '{ABSOLUTE}':
                    dateFormat = 'HH:mm:ss,SSS'
                    index += '{ABSOLUTE}'.length()
                    break
                case '{DATE}':
                    dateFormat = 'dd MMM yyyy HH:mm:ss,SSS'
                    index += '{DATE}'.length()
                    break
                case '{ISO8601}':
                    dateFormat = DEFAULT_LOG4J_DATE_FORMAT
                    index += '{ISO8601}'.length()
                    break
                default:
                    dateFormat = format.substring(1, format.length() - 1)
                    index += dateFormat.length() + 2
                    break
            }
        }
        if (!dateFormat) {
            dateFormat = DEFAULT_LOG4J_DATE_FORMAT
        }
        sb.append('(').append(RegExEntryProcessor.dateFormatToRegEx(dateFormat)).append(')')
        return index
    }

    protected
    static int appendSpecifierRegEx(LogContext logContext, StringBuilder sb, char ch, int i, String conversionPattern) {
        final matcher = SPEC_PATTERN.matcher(conversionPattern.substring(i + 1))
        if (!matcher.find()) {
            throw new UnsupportedOperationException("Unsupported/unknown logging conversion pattern: ${conversionPattern.substring(i + 1)}; of $conversionPattern")
        }
        String regEx = ''
        String spec = matcher.group(0)
        String padding = matcher.group(1)
        String maxLength = matcher.group(2)
        ch = spec.charAt(spec.length() - 1)
        switch (ch) {
            case 'c': // logging event category
                regEx = '[^ ]+'
                break
            case 'C': // fully qualified class name of the caller
                regEx = '[^ ]+'
                break
            case 'd': // date of the logging event. The date conversion specifier may be followed by a date format specifier enclosed between braces. For example, %d{HH:mm:ss,SSS} or %d{dd MMM yyyy HH:mm:ss,SSS}. If no date format specifier is given then ISO8601 format is assumed.
                i = appendDateFormatRegEx(logContext, sb, ch, i, conversionPattern)
                break
            case 'F': // file name where the logging request was issued.
                regEx = '[^ ]+'
                break
            case 'l': // file name where the logging request was issued. The location information depends on the JVM implementation but usually consists of the fully qualified name of the calling method followed by the callers source the file name and line number between parentheses.
                regEx = '[^ ]+'
                break
            case 'L': // line number from where the logging request was issued.
                regEx = '[\\d^ ]+'
                break
            case 'm': // message
                regEx = '.+'
                break
            case 'M': // method name where the logging request was issued.
                regEx = '[^ ]+'
                break
            case 'n': // line break, skip it
                return i + 2
            case 'p': // priority of the logging event.
                regEx = '\\w+'
                break
            case 'r': // number of milliseconds
                regEx = '[\\d^ ]+'
                break
            case 't': // name of the thread that generated the logging event.
                regEx = '[\\w^ ]+'
                break
            case 'x': // NDC (nested diagnostic context) associated with the thread that generated the logging event.
                regEx = '[\\w^ ]*'
                break
            case 'X': // MDC (mapped diagnostic context) associated with the thread that generated the logging event. The X conversion character must be followed by the key for the map placed between braces, as in %X{clientNumber} where clientNumber is the key. The value in the MDC corresponding to the key will be output.
                regEx = '[[\\w^ ]*'
                break
            default:
                throw new UnsupportedOperationException("Unsupported/unknown logging conversion pattern: ${conversionPattern.substring(i + 1)}; of $conversionPattern")
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

    static String conversionPatternToRegEx(final LogContext logContext, final String conversionPattern) {
        final specClosure = { StringBuilder sb1, char ch, int i -> appendSpecifierRegEx(logContext, sb1, ch, i, conversionPattern) }
        final charClosure = { StringBuilder sb1, char ch, int i -> appendNonSpecifierChar(logContext, sb1, ch, i, conversionPattern) }

        final sb = new StringBuilder()
        final cs = '%' as char
        final length = conversionPattern.length()
        final chars = conversionPattern.toCharArray()
        Closure closure
        for (int i = 0; i < length; i++) {
            char ch = chars[i]
            if (ch == cs) {
                if (i < length && chars[i + 1] == cs) {
                    charClosure.call(sb, '\\' as char, i)
                    charClosure.call(sb, ch, i)
                    i++
                    continue
                }
                closure = specClosure
            } else {
                closure = charClosure
            }
            i = closure.call(sb, ch, i) as int
        }
        return sb.toString()
    }

    private static Set<Character> toSet(String chString) {
        final set = new HashSet<Character>(chString.length())
        for (char ch : chString.toCharArray()) {
            set.add(ch)
        }
        return set
    }
}
