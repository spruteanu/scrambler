package org.prismus.scrambler.log

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class Log4jScrambler {
    private static final String DEFAULT_LOG4J_DATE_FORMAT = 'yyyy-MM-dd HH:mm:ss.SSS' // ISO8601DateFormat

    static String log4jConversionPatternToRegEx(String conversionPattern) {
        final sb = new StringBuilder()
        final cs = '%' as char
        final length = conversionPattern.length()
        final chars = conversionPattern.toCharArray()
        for (int i = 0; i < length; i++) {
            int cs_idx = conversionPattern.indexOf(cs, i)
            if (cs_idx < 0) {
                break
            }
            i = cs_idx
            while (++i < length) {
                final ct = conversionPattern.charAt(i)
                if (ct == '%') {
                    sb.append('\\%')
                } else if (Character.isAlphabetic(ct as int)) {
                    break
                }
            }
            if (i >= length) {
                break
            }
            String specRegEx = ''
            switch (conversionPattern.charAt(i)) {
                case 'c': // logging event category
                    break
                case 'C': // fully qualified class name of the caller
                    break
                case 'd': // date of the logging event. The date conversion specifier may be followed by a date format specifier enclosed between braces. For example, %d{HH:mm:ss,SSS} or %d{dd MMM yyyy HH:mm:ss,SSS}. If no date format specifier is given then ISO8601 format is assumed.
                    break
                case 'F': // file name where the logging request was issued.
                    break
                case 'l': // file name where the logging request was issued. The location information depends on the JVM implementation but usually consists of the fully qualified name of the calling method followed by the callers source the file name and line number between parentheses.
                    break
                case 'L': // line number from where the logging request was issued.
                    break
                case 'm': // message
                    break
                case 'M': // method name where the logging request was issued.
                    break
                case 'n': // line break, skip it
                    break
                case 'p': // priority of the logging event.
                    break
                case 'r': // number of milliseconds
                    break
                case 't': // name of the thread that generated the logging event.
                    break
                case 'x': // NDC (nested diagnostic context) associated with the thread that generated the logging event.
                    break
                case 'X': // MDC (mapped diagnostic context) associated with the thread that generated the logging event. The X conversion character must be followed by the key for the map placed between braces, as in %X{clientNumber} where clientNumber is the key. The value in the MDC corresponding to the key will be output.
                    break
                default:
                    // handle regex special chars
                    break
            }
            if  ((i - cs_idx) > 1) {
                final cj = conversionPattern.charAt(cs_idx + 1)
                if (cj == '-') {
                    specRegEx = '\\s*' + specRegEx
                }
            }
            sb.append(specRegEx)
        }
/*
%r [%t] %-5p %c %x - %m%n
%-6r [%15.15t] %-5p %30.30c %x - %m%n
* */
        return sb.toString()
    }
}
